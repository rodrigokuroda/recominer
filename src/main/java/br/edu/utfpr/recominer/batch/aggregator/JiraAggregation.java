package br.edu.utfpr.recominer.batch.aggregator;

import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.QueryUtils;
import br.edu.utfpr.recominer.model.issue.IssueScmlog;
import br.edu.utfpr.recominer.model.svn.Scmlog;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class JiraAggregation {

    private final String gitUrlPattern = "(\\s+git-svn-id:\\shttps://svn.apache.org/).*";
    private final String issueReferencePattern;
    private final String selectIssueIdAndFixVersions;
    private final String insertAssociation;

    private final Pattern regexNumber = Pattern.compile("\\d+");
    private final Pattern regex;

    private final GenericDao dao;
    private final Project project;

    public JiraAggregation(final GenericDao dao, final Project project) {
        this.dao = dao;
        this.project = project;
        final String projectName = project.getProjectName();

        this.issueReferencePattern
                = "(?i)(" + projectName.toUpperCase()
                + "\\s*[-]+\\s*\\d+(?=\\.(?!\\w)|-(?![a-zA-Z])|:|\\s|,|]|\\)|\\(|;|_))";

        this.selectIssueIdAndFixVersions
                = QueryUtils.getQueryForDatabase(
                  "SELECT DISTINCT i.id, i.submitted_on, i.fixed_on"
                + "  FROM {0}_issues.issues i"
                + "  JOIN {0}_issues.changes c ON c.issue_id = i.id"
                + "  JOIN {0}_issues.issues_ext_jira iej ON iej.issue_id = i.id"
                + " WHERE UPPER(iej.issue_key) = ?"
                + "   AND i.resolution = 'Fixed'"
                + "   AND c.field = 'Resolution'"
                + "   AND c.new_value = i.resolution", projectName);

        this.regex = Pattern.compile(issueReferencePattern, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

        this.insertAssociation
                = QueryUtils.getQueryForDatabase(
                        "INSERT INTO {0}.issues_scmlog (issue_id, scmlog_id) VALUES (?, ?)",
                        projectName);
    }

    public void aggregate(Iterable<Scmlog> commits) {
        final Set<IssueScmlog> issueAndCommitAssociated = new HashSet<>();
        for (Scmlog commit : commits) {
            // remove "git-svn-id: https://svn.apache.org/*" from message
            // to avoid false positive matches of pattern
            // (e.g. git-svn-id: https://svn.apache.org/camel-1.1.0)
            final String commitMessage = replaceUrl(commit.getMessage());
            final Matcher matcher = regex.matcher(commitMessage);

            // for each occurrence of pattern (issue key)
            while (matcher.find()) {

                String issueKey = matcher.group().replace(" ", ""); // e.g.: ARIES-1234

//                Matcher matcherNumber = regexNumber.matcher(issueKey);
//                if (matcherNumber.find()) {
//                    issueKey = matcherNumber.group(); // e.g.: 1234
//                }

                final Object[] issueIdAndFixVersions = (Object[]) dao.selectNativeOneWithParams(selectIssueIdAndFixVersions, new Object[]{issueKey.toUpperCase()});

                if (issueIdAndFixVersions != null) {
                    Integer issueId = (Integer) issueIdAndFixVersions[0];
                    Timestamp submittedOn = (Timestamp) issueIdAndFixVersions[1];
                    Timestamp fixedOn = (Timestamp) issueIdAndFixVersions[2];

                    final IssueScmlog issueScmlog = new IssueScmlog(commit.getId(), issueId);

                    // data cleaning: commits must have committed between
                    // issues' submit date and fixed date
                    if (!issueAndCommitAssociated.contains(issueScmlog)
                            && !commit.getDate().after(fixedOn)
                            && !commit.getDate().before(submittedOn)) {
                        issueAndCommitAssociated.add(issueScmlog);
                        dao.executeNativeQuery(insertAssociation, new Object[]{issueId, commit.getId()});
                    }
                }
            }
        }
    }

    private String replaceUrl(String text) {
        return text.replaceAll(gitUrlPattern, "");
    }
}
