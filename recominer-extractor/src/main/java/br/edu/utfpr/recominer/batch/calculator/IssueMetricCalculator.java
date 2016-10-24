package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import br.edu.utfpr.recominer.metric.discussion.LuceneUtil;
import br.edu.utfpr.recominer.model.IssuesMetrics;
import com.google.common.base.Strings;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
public class IssueMetricCalculator {

    private final Logger log = LoggerFactory.getLogger(IssueMetricCalculator.class);

    @Inject
    private JdbcTemplate template;

//    public IssuesMetrics calculeIssueMetrics(final Project project, final Issue issue) {
//        final List<String> comments = template.query(
//                QueryUtils.getQueryForDatabase(
//                        "SELECT comments.text"
//                        + "  FROM {0}_issues.comments comments "
//                        + " WHERE comments.issue_id = ?",  project),
//                (ResultSet rs, int rowNum) -> rs.getString(1),
//                issue.getId());
//
//        final String selectMetric = "SELECT DISTINCT im.id, i.id AS issue_id, iej.issue_key, "
//                + "                     i.description, i.type, i.priority, "
//                + "                     assigned.user_id AS assigned_to, "
//                + "                     submitted.user_id AS submitted_by, "
//                + "                     COUNT(DISTINCT(comments2.submitted_by)) AS num_commenters, "
//                
//                + "                     (SELECT COUNT(DISTINCT(comments2.submitted_by)) "
//                + "                        FROM {0}_issues.comments comments2"
//                + "                        JOIN {0}_issues.people pc ON pc.id = comments2.submitted_by"
//                + "                       WHERE comments2.issue_id = i.id"
//                + "                         AND pc.is_dev = 1) AS num_dev_commenters, "
//                
//                + "                     i.submitted_on, i.fixed_on, "
//                + "                     MAX(c.changed_on) as updated_on, "
//                + "                     MAX(comments.submitted_on) AS comments_updated_on"
//                + "                  FROM {0}_issues.issues i"
//                + "                  LEFT JOIN {0}.issues_metrics im ON im.issue_id = i.id"
//                + "                  JOIN {0}_issues.issues_ext_jira iej ON iej.issue_id = i.id"
//                + "                  LEFT JOIN {0}_issues.changes c ON c.issue_id = i.id "
//                + "                  LEFT JOIN {0}_issues.comments comments ON comments.issue_id = i.id"
//                + "                  JOIN {0}_issues.people assigned ON assigned.id = i.assigned_to"
//                + "                  JOIN {0}_issues.people submitted ON submitted.id = i.submitted_by"
//                + "                 WHERE i.id = ?"
//                + "                 GROUP BY i.id " // for "MAX(c.changed_on)" work
//                + "                 ORDER BY i.submitted_on" 
//                ;
//
//        IssuesMetrics metrics = template.queryForObject(
//                QueryUtils.getQueryForDatabase(selectMetric,  project),
//                (ResultSet rs, int rowNum) -> {
//                    return new IssuesMetrics(
//                            rs.getInt("id"),
//                            new Issue(rs.getInt("issue_id")),
//                            rs.getString("issue_key"), // iej.issue_key
//                            rs.getString("type"), // i.type
//                            rs.getString("priority"), // i.priority
//                            rs.getString("assigned_to"), // assigned.user_id
//                            rs.getString("submitted_by"), // submitted.user_id
//                            rs.getInt("num_commenters"), // i.num_commenters
//                            rs.getInt("num_dev_commenters"), // i.num_dev_commenters
//                            rs.getTimestamp("updated_on"), // last update
//                            rs.getTimestamp("comments_updated_on"), // last comment
//                            calculeAge(rs.getTimestamp("submitted_on"), rs.getTimestamp("fixed_on")), // i.submitted_on
//                            calculeWordiness(rs.getString("description")), // i.body
//                            calculeWordiness(comments),
//                            comments.size(),
//                            null
//                    );
//                },
//                issue.getId());
//
//        return metrics;
//    }

    public IssuesMetrics calculeIssueMetrics(final Project project, final Issue issue, final Commit commit) {
        final List<String> comments = template.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT comments.text"
                        + "  FROM {0}_issues.comments comments "
                        + " WHERE comments.issue_id = ?"
                        + "   AND comments.submitted_on <= (SELECT s.date FROM {0}_vcs.scmlog s WHERE s.id = ?)",  project),
                (ResultSet rs, int rowNum) -> rs.getString(1),
                issue.getId(), commit.getId());

        final String selectMetric = "SELECT DISTINCT im.commit_id, im.id, i.id AS issue_id, iej.issue_key, "
                + "                     i.description, i.type, i.priority, "
                + "                     assigned.user_id AS assigned_to, "
                + "                     submitted.user_id AS submitted_by, "
                + "                     (SELECT COUNT(DISTINCT(comments2.submitted_by)) "
                + "                        FROM {0}_issues.comments comments2"
                + "                       WHERE comments2.issue_id = i.id"
                + "                         AND comments2.submitted_on <= "
                + "                           s.date) AS num_commenters, "
                
                + "                     (SELECT COUNT(DISTINCT(comments2.submitted_by)) "
                + "                        FROM {0}_issues.comments comments2"
                + "                        JOIN {0}_issues.people pc ON pc.id = comments2.submitted_by"
                + "                       WHERE comments2.issue_id = i.id"
                + "                         AND pc.is_dev = 1"
                + "                         AND comments2.submitted_on <= "
                + "                            s.date) AS num_dev_commenters, "
                + "                     i.submitted_on, i.fixed_on, "
                + "                     i.updated_on, "
                + "                     i.comments_updated_on"
                + "                  FROM {0}_issues.issues i"
                + "                  LEFT JOIN {0}.issues_metrics im ON im.issue_id = i.id AND im.commit_id = ?"
                + "                  JOIN {0}_issues.issues_ext_jira iej ON iej.issue_id = i.id"
                + "                  LEFT JOIN {0}_issues.comments comments ON comments.issue_id = i.id"
                + "                  JOIN {0}_issues.people assigned ON assigned.id = i.assigned_to"
                + "                  JOIN {0}_issues.people submitted ON submitted.id = i.submitted_by"
                + "                  JOIN {0}.issues_scmlog i2s ON i2s.issue_id = i.id"
                + "                  JOIN {0}_vcs.scmlog s ON s.id = i2s.scmlog_id"
                + "                 WHERE i.id = ? "
                + "                 GROUP BY i.id, im.id, s.date "
                + "                 ORDER BY i.submitted_on" 
                ;

        IssuesMetrics metrics = template.queryForObject(
                QueryUtils.getQueryForDatabase(selectMetric,  project),
                (ResultSet rs, int rowNum) -> {
                    return new IssuesMetrics(
                            rs.getInt("id"),
                            new Issue(rs.getInt("issue_id")),
                            rs.getString("issue_key"), // iej.issue_key
                            rs.getString("type"), // i.type
                            rs.getString("priority"), // i.priority
                            rs.getString("assigned_to"), // assigned.user_id
                            rs.getString("submitted_by"), // submitted.user_id
                            rs.getInt("num_commenters"), // i.num_commenters
                            rs.getInt("num_dev_commenters"), // i.num_dev_commenters
                            rs.getTimestamp("updated_on"), // last update
                            rs.getTimestamp("comments_updated_on"), // last comment
                            calculeAge(rs.getTimestamp("submitted_on"), rs.getTimestamp("fixed_on")), // i.submitted_on
                            calculeWordiness(rs.getString("description")), // i.body
                            calculeWordiness(comments),
                            comments.size(),
                            commit
                    );
                },
                commit.getId(), issue.getId());

        return metrics;
    }

    private int calculeAge(Timestamp submittedOn, Timestamp fixedOn) {
        if (submittedOn == null) {
            return -1;
        }
        final LocalDate createdAt = new LocalDate(submittedOn.getTime());
        final LocalDate finalDate;
        if (fixedOn != null) {
            finalDate = new LocalDate(fixedOn.getTime());
        } else {
            // if issue is not fixed yet, calcule age based on actual date (today)
            finalDate = new LocalDate();
        }
        return Days.daysBetween(createdAt, finalDate).getDays();
    }

    public long calculeWordiness(List<String> comments) {
        return comments
                .parallelStream()
                .mapToLong(c -> calculeWordiness(c))
                .sum();
    }

    public long calculeWordiness(String body) {
        return LuceneUtil.tokenizeString(Strings.nullToEmpty(body)).size();
    }
}
