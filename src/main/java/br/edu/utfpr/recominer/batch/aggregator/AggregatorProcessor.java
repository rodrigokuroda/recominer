package br.edu.utfpr.recominer.batch.aggregator;

import br.edu.utfpr.recominer.batch.bicho.IssueTrackerSystem;
import br.edu.utfpr.recominer.comparator.VersionComparator;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.model.Version;
import br.edu.utfpr.recominer.model.svn.Scmlog;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class AggregatorProcessor implements ItemProcessor {

    private static final Logger log = LogManager.getLogger();
    private static final String SQL_DELIMITER = ";";
    private static final String RECOMINER_TABLES_SCRIPT_NAME = "recominer_tables.sql";
    private static final String PREPROCESSING_SCRIPT_NAME = "preprocessing.sql";
    
    @Inject
    @Mysql
    private EntityManagerFactory factory;
    
    @Inject
    private JobContext jobContext;

    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;
        String projectName = project.getProjectName().toLowerCase();
        
        final Properties properties = new Properties();
        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, projectName + "_vcs");
        GenericDao dao = new GenericDao(factory.createEntityManager(properties));
        
        executeSqlScript(project, dao, RECOMINER_TABLES_SCRIPT_NAME);
        
        final List<Scmlog> commits = dao.selectAll(Scmlog.class);
        
        int totalPatternOccurrences = 0;
        int totalPatternRelatedWithAnIssue = 0;

        boolean isIssuesFromBugzilla = isIssuesFromBugzilla(project);

        final String selectIssueIdAndFixVersions;
        final String issueReferencePattern;
        if (isIssuesFromBugzilla) {
            issueReferencePattern = "(?i)(bug|issue|fixed|fix|bugzilla)+(\\s)*(id|for)?(:|-)?\\s*#?\\s*(\\d+)(,\\s*\\d+)*";
            selectIssueIdAndFixVersions = "SELECT id FROM " + projectName + "_issues.issues WHERE issue = ?";
        } else {
            issueReferencePattern = "(?i)(" + projectName.toUpperCase() + "\\s*[-]+\\s*\\d+(?=\\.(?!\\w)|-(?![a-zA-Z])|:|\\s|,|]|\\)|\\(|;|_))";
            selectIssueIdAndFixVersions
                    = "SELECT DISTINCT i.id, iej.fix_version FROM " + projectName + "_issues.issues i"
                    + "  JOIN " + projectName + "_issues.changes c ON c.issue_id = i.id"
                    + "  JOIN " + projectName + "_issues.issues_ext_jira iej ON iej.issue_id = i.id"
                    + " WHERE UPPER(iej.issue_key) = ?"
                    + "   AND i.resolution = 'Fixed'"
                    + "   AND c.field = 'Resolution'"
                    + "   AND c.new_value = i.resolution";
        }

        final Long totalIssues = (Long) dao.selectNativeOneWithParams("SELECT COUNT(1) FROM " + projectName + "_issues.issues", new Object[]{});

        final Long totalCommits = (Long) dao.selectNativeOneWithParams("SELECT COUNT(1) FROM " + projectName + "_vcs.scmlog", new Object[]{});

        final Pattern regex = Pattern.compile(issueReferencePattern, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        final Pattern regexNumber = Pattern.compile("\\d+");

        final Map<Integer, List<String>> fixedIssuesIdFixVersion = new HashMap<>();
        final Set<Integer> fixedIssuesSet = new HashSet<>();

        int totalCommitsWithOccurrences = 0;

        for (Scmlog commit : commits) {
            // remove "git-svn-id: https://svn.apache.org/*" from message
            // to avoid false positive matches of pattern
            // (e.g. git-svn-id: https://svn.apache.org/camel-1.1.0)
            final String commitMessage = replaceUrl(commit.getMessage());
            final Matcher matcher = regex.matcher(commitMessage);

            int matcherCount = 0;

            // para cada ocorrência do padrão
            while (matcher.find()) {

                String issueKey = matcher.group().replace(" ", ""); // e.g.: ARIES-1234

                if (isIssuesFromBugzilla) {
                    Matcher matcherNumber = regexNumber.matcher(issueKey);
                    if (matcherNumber.find()) {
                        issueKey = matcherNumber.group(); // e.g.: 1234
                    } else {
                        log.info("Not found issue for match pattern " + issueKey);
                    }
                }

                totalPatternOccurrences++;
                matcherCount++;

                final Object[] issueIdAndFixVersions = (Object[]) dao.selectNativeOneWithParams(selectIssueIdAndFixVersions, new Object[]{issueKey.toUpperCase()});

                if (issueIdAndFixVersions != null) {
                    Integer issueId = (Integer) issueIdAndFixVersions[0];
                    String fixVersions = (String) issueIdAndFixVersions[1];

                    // adiciona as versões da issue corrigida
                    fixedIssuesIdFixVersion.put(issueId, Arrays.asList(fixVersions.replace(" ", "").split(",")));
                    // adiciona a issue corrigida
                    fixedIssuesSet.add(issueId);
                    try {
                        dao.executeNativeQuery("INSERT INTO " + projectName
                                + "_issues.issues_scmlog (issue_id, scmlog_id) VALUES (?, ?)", new Object[]{issueId, commit.getId()});
                        totalPatternRelatedWithAnIssue++;
                    } catch (Exception e) {
                        log.info("Issue " + issueId + " and commit " + commit.getId() + " already exists.");
                    }
                }
            }
            if (matcherCount > 0) {
                totalCommitsWithOccurrences++;
            } else {
                log.info(commitMessage);
            }
//            log.info(matcherCount + " ocorrências para o commit " + commit.getId());
        }

        final String issueFixVersionInsert
                = "INSERT INTO " + projectName + "_issues.issues_fix_version (issue_id, fix_version, minor_fix_version, major_fix_version) VALUES (?, ?, ?, ?)";

        int countIssuesWithFixVersion = 0;

        Set<Version> distincMinorVersion = new HashSet<>();
        for (Map.Entry<Integer, List<String>> entrySet : fixedIssuesIdFixVersion.entrySet()) {
            Integer issueId = entrySet.getKey();
            List<String> versions = entrySet.getValue();

            if (versions.isEmpty() || versions.get(0).isEmpty()) {
                log.info("Issue " + issueId + " has no fix version.");
            } else {
//                log.info("Issue " + issueId + " is fixed in " + versions.size() + " versions.");

                for (String version : versions) {
                    try {
                        String minorVersion = getMinorVersion(version);
//                        String majorVersion = getMajorVersion(version);

                        distincMinorVersion.add(new Version(minorVersion));

                        dao.executeNativeQuery(issueFixVersionInsert, new Object[]{issueId, version, minorVersion, minorVersion});
                    } catch (Exception e) {
                        log.error("An error occurred while inserting fix versions.", e);
                        log.info("Issue " + issueId + " and version " + version + " already exists.");
                    }
                }

                countIssuesWithFixVersion++;
            }
        }

        List<Version> minorVersionsOrdered = new ArrayList<>(distincMinorVersion);
        log.info(Arrays.toString(minorVersionsOrdered.toArray()));

        Collections.sort(minorVersionsOrdered, new VersionComparator());

        String issueFixVersionOrderInsert
                = "INSERT INTO " + projectName
                + "_issues.issues_fix_version_order (minor_fix_version, major_fix_version, version_order) VALUES (?, ?, ?)";
        int order = 1;
        for (Version minorVersion : minorVersionsOrdered) {
            try {
                dao.executeNativeQuery(issueFixVersionOrderInsert, new Object[]{minorVersion.getVersion(), minorVersion.getVersion(), order++});
            } catch (Exception e) {
                log.error("And error occurred while inserting fix version order.", e);
                log.info("Issue " + minorVersion + " order " + order + " already exists.");
            }
        }

        executeSqlScript(project, dao, PREPROCESSING_SCRIPT_NAME);

        log.info("\n\n"
                + commits.size() + " of " + totalCommits + " (total) commits has less than or equal to 20 files\n"
                + totalCommitsWithOccurrences + " of " + commits.size() + " commits has at least one occurrence of pattern \"" + issueReferencePattern + "\"\n\n"
                + totalPatternOccurrences + " occurrences of pattern \"" + issueReferencePattern + "\" in commits' message was found\n"
                + totalPatternRelatedWithAnIssue + " of " + totalPatternOccurrences + " occurrences was related with an issue\n\n"
                + fixedIssuesSet.size() + " of " + totalIssues + " (total) issues was fixed\n"
                + countIssuesWithFixVersion + " of " + fixedIssuesSet.size() + " issues has 'fix version'\n\n"
        );
        return project;
    }

    private void executeSqlScript(Project project, GenericDao dao, String resourceFileName) {
        InputStream script = this.getClass().getClassLoader().getResourceAsStream(resourceFileName);
        
        // Create SQL scanner
        final Scanner scanner = new Scanner(script).useDelimiter(SQL_DELIMITER);

        final String projectName = project.getProjectName().toLowerCase();
        // Loop through the SQL file statements
        while (scanner.hasNext()) {

            // Get statement from file
            final String rawStatement = scanner.next() + SQL_DELIMITER;
            // Execute statement
            try {
                dao.executeNativeQuery(rawStatement.replace("{0}", projectName), new Object[0]);
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate column name")) {
                    log.warn(e.getMessage());
                } else {
                    throw e;
                }
            }
        }
    }

    private boolean isIssuesFromBugzilla(Project project) {
        return project.getIssueTracker().getSystem() == IssueTrackerSystem.BUGZILLA;
    }

    private String replaceUrl(String text) {
        return text.replaceAll("(\\s+git-svn-id:\\shttps://svn.apache.org/).*", "");
    }

    // 1.1.1 > 1
    private String getMajorVersion(String version) {
        String majorVersion;
        String[] versionsSplited = version.split("[.]");
        if (versionsSplited.length > 1) {
            majorVersion = versionsSplited[0];
        } else {
            majorVersion = version;
        }
        return majorVersion;
    }

    // 1.1.1 > 1.1
    private String getMinorVersion(String version) {
        String minorVersion;
        String[] versionsSplited = version.split("[.]");
        if (versionsSplited.length > 2) {
            minorVersion = versionsSplited[0] + "." + versionsSplited[1];
        } else {
            minorVersion = version;
        }
        return minorVersion;
    }

}
