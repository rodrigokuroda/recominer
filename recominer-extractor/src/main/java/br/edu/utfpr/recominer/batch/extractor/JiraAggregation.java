package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.model.issue.IssueScmlog;
import br.edu.utfpr.recominer.model.svn.Scmlog;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class JiraAggregation {

    private final Logger log = LoggerFactory.getLogger(AssociationProcessor.class);

    private final String gitUrlPattern = "(\\s+git-svn-id:\\shttps://svn.apache.org/).*";
    private final String issueReferencePattern;
    private final String selectIssueByIssueKey;
    private final String insertAssociation;

    private final Pattern regex;

    private final JdbcTemplate template;

    private final Project project;

    public JiraAggregation(final JdbcTemplate template, final Project project) {
        this.template = template;
        this.project = project;
        final String projectName = project.getProjectName();

        this.issueReferencePattern
                = "(?i)(" + projectName.toUpperCase()
                + "\\s*[-]+\\s*\\d+(?=\\.(?!\\w)|-(?![a-zA-Z])|:|\\s|,|]|\\)|\\(|;|_))";

        // relating all issues, including that not fixed
        this.selectIssueByIssueKey
                = QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT i.id, i.submitted_on, i.fixed_on"
                        + "  FROM {0}_issues.issues i"
                        + "  JOIN {0}_issues.issues_ext_jira iej ON iej.issue_id = i.id"
                        + " WHERE UPPER(iej.issue_key) = ?" 
                        //                + "   AND i.resolution = \"Fixed\""
                        //                + "   AND i.fixed_on IS NOT NULL"
                        , project);

        this.regex = Pattern.compile(issueReferencePattern, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

        this.insertAssociation
                = QueryUtils.getQueryForDatabase(
                        "INSERT INTO {0}.issues_scmlog (issue_id, scmlog_id) VALUES (?, ?)",
                        project);
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
                Issue issue;
                try {
                    issue = template.queryForObject(selectIssueByIssueKey,
                            (ResultSet rs, int rowNum) -> new Issue(rs.getInt(1), issueKey, null, rs.getDate(2), rs.getDate(3), null),
                            issueKey.toUpperCase());
                } catch (EmptyResultDataAccessException ex) {
                    log.warn("Issue with key " + issueKey + " not found.");
                    continue;
                }

                if (issue != null) {

                    final IssueScmlog issueScmlog = new IssueScmlog(commit.getId(), issue.getId());

                    // data cleaning: commits must have committed between
                    // submit and fixed date
                    if (!issueAndCommitAssociated.contains(issueScmlog)
                            && (issue.getSubmittedOn() == null || !commit.getDate().before(issue.getSubmittedOn()))
                            && (issue.getFixDate() == null || !commit.getDate().after(issue.getFixDate()))) {
                        issueAndCommitAssociated.add(issueScmlog);
                        log.debug("Associating issue " + issue.getId() + " and commit " + commit.getId());
                        template.update(insertAssociation, issue.getId(), commit.getId());
                    }
                }
            }
        }
    }

    private String replaceUrl(String text) {
        return text.replaceAll(gitUrlPattern, "");
    }
}
