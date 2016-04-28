package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.QueryUtils;
import br.edu.utfpr.recominer.model.Issue;
import br.edu.utfpr.recominer.model.IssueMetrics;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author kuroda
 */
public class IssueMetricCalculator {

    private Project project;
    private GenericDao dao;

    public IssueMetricCalculator(Project project, GenericDao dao) {
        this.project = project;
        this.dao = dao;
    }

    public IssueMetrics calculeIssueMetrics(final Issue issue) {
        final String selectIssuesById
                = QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT i.id, iej.issue_key, i.issue, "
                        + "     i.description, i.type, i.priority, "
                        + "     assigned.user_id, submitted.user_id, "
                        + "     i.num_watchers, i.reopened_times,"
                        + "     i.num_commenters, i.num_dev_commenters,"
                        + "     i.submitted_on, i.fixed_on, MAX(c.changed_on)"
                        + "  FROM {0}_issues.issues i"
                        + "  JOIN {0}_issues.issues_ext_jira iej ON iej.issue_id = i.id"
                        + "  JOIN {0}_issues.changes c ON c.issue_id = i.id"
                        + "  JOIN {0}_issues.people assigned ON assigned.id = i.assigned_to"
                        + "  JOIN {0}_issues.people submitted ON submitted.id = i.submitted_by"
                        + " WHERE i.id = ?"
                        + " ORDER BY i.submitted_on", project.getProjectName());

        final Object[] rawIssues = dao.selectNativeOneWithParams(selectIssuesById, new Object[]{issue.getId()});

        final Integer issueNumber = (Integer) rawIssues[0];

        final List<String> comments = dao.selectNativeWithParams(
                QueryUtils.getQueryForDatabase(
                        "SELECT comments.text"
                        + "  FROM {0}_issues.comments comments "
                        + " WHERE comments.issue_id = ?", project.getProjectName()), new Object[]{issueNumber});

        final IssueMetrics issueWithComments = new IssueMetrics(
                issueNumber,
                (String) rawIssues[1], // iej.issue_key
                (String) rawIssues[2], // i.url
                (String) rawIssues[3], // i.body
                (String) rawIssues[4], // i.type
                (String) rawIssues[5], // i.priority
                (String) rawIssues[6], // assigned.user_id
                (String) rawIssues[7], // submitted.user_id
                (Integer) rawIssues[8], // i.num_watchers
                (Integer) rawIssues[9], // i.num_reopened
                comments,
                (Integer) rawIssues[10], // i.num_commenters
                (Integer) rawIssues[11], // i.num_dev_commenters
                (Timestamp) rawIssues[12], // i.submitted_on
                (Timestamp) rawIssues[13], // c.changed_on where value = "Fixed"
                (Timestamp) rawIssues[14] // last update
        );

        return issueWithComments;
    }

    public void saveIssueMetrics(IssueMetrics metrics) {
        final String insertIssueMetrics
                = QueryUtils.getQueryForDatabase(
                        "INSERT INTO avro.issues_metrics "
                        + "(issue_id, "
                        + "issue_update_on, "
                        + "issue_type, "
                        + "issue_priority, "
                        + "issue_assigned_to, "
                        + "issue_submitted_by, "
                        + "issue_watchers, "
                        + "issue_reopened, "
                        + "commenters, "
                        + "dev_commenters, "
                        + "comments, "
                        + "wordiness, "
                        + "issue_age) "
                        + "VALUES "
                        + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", 
                        project.getProjectName());
        
        dao.executeNativeQuery(insertIssueMetrics, 
                metrics.getIssueId(),
                metrics.getUpdatedOn(),
                metrics.getIssueType(),
                metrics.getPriority(),
                metrics.getAssignedTo(),
                metrics.getSubmittedBy(),
                metrics.getNumberOfWatchers(),
                metrics.getReopenedTimes(),
                metrics.getCommenters(),
                metrics.getDevCommenters(), 
                metrics.getComments().size(),
                metrics.getWordiness(),
                metrics.getIssueAge()
                );
    }
}
