package br.edu.utfpr.recominer.batch.aggregator;

import br.edu.utfpr.recominer.comparator.VersionComparator;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import br.edu.utfpr.recominer.model.Version;
import br.edu.utfpr.recominer.model.svn.Scmlog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
@Dependent
public class AggregatorProcessor implements ItemProcessor {

    private static final Logger log = LogManager.getLogger();

    @Inject
    private GenericBichoDAO dao;
    
    @Inject
    private JobContext jobContext;

    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;
        String projectName = project.getProjectName().toLowerCase();

        System.out.println("Processing: " + item);
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

        final Map<Long, List<String>> fixedIssuesIdFixVersion = new HashMap<>();
        final Set<Long> fixedIssuesSet = new HashSet<>();

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
                Long issueId = (Long) issueIdAndFixVersions[0];
                String fixVersions = (String) issueIdAndFixVersions[1];

                if (issueId != null) {

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
        for (Map.Entry<Long, List<String>> entrySet : fixedIssuesIdFixVersion.entrySet()) {
            Long issueId = entrySet.getKey();
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

        log.info("\n\n"
                + commits.size() + " of " + totalCommits + " (total) commits has less than or equal to 20 files\n"
                + totalCommitsWithOccurrences + " of " + commits.size() + " commits has at least one occurrence of pattern \"" + issueReferencePattern + "\"\n\n"
                + totalPatternOccurrences + " occurrences of pattern \"" + issueReferencePattern + "\" in commits' message was found\n"
                + totalPatternRelatedWithAnIssue + " of " + totalPatternOccurrences + " occurrences was related with an issue\n\n"
                + fixedIssuesSet.size() + " of " + totalIssues + " (total) issues was fixed\n"
                + countIssuesWithFixVersion + " of " + fixedIssuesSet.size() + " issues has 'fix version'\n\n"
        );
        return null;
    }

    private boolean isIssuesFromBugzilla(Project project) {
        final Object isIssuesFromJira = dao.selectNativeOneWithParams("SELECT 1 "
                + "FROM information_schema.tables "
                + "WHERE table_schema = ? "
                + "    AND table_name = 'issues_ext_bugzilla' "
                + "LIMIT 1", new Object[]{project.getProjectName().toLowerCase() + "_issues"});
        boolean isIssuesFromBugzilla = isIssuesFromJira != null;
        return isIssuesFromBugzilla;
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
