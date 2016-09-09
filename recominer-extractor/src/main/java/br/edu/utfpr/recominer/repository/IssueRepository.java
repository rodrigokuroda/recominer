package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@Repository
public class IssueRepository extends JdbcRepository<Issue, Integer> {

    public IssueRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "issues";

    public static final RowMapper<Issue> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                Issue issue = new Issue(rs.getInt("id"), rs.getString("type"),
                        null, rs.getDate("submitted_on"), rs.getDate("fixed_on"), null);
                return issue;
            };

    private static final RowUnmapper<Issue> ROW_UNMAPPER
            = (Issue issue) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", issue.getId());

                return mapping;
            };

    public List<Issue> selectUpdatedIssuesRelatedTo(Commit commit) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT i.id, i.type, i.submitted_on, i.fixed_on"
                        + "  FROM {0}_issues.issues i"
                        + "  JOIN {0}.issues_scmlog i2s ON i2s.issue_id = i.id"
                        + "  LEFT JOIN {0}.issues_metrics im ON im.issue_id = i.id"
                        + " WHERE i2s.scmlog_id = ? "
                        // issues with new updates or new comments
                        + "   AND (im.updated_on IS NULL "
                        + "    OR im.updated_on < (SELECT MAX(c.changed_on) FROM {0}_issues.changes c WHERE c.issue_id = i.id))"
                        + "   AND (im.comments_updated_on IS NULL "
                        + "    OR im.comments_updated_on < (SELECT MAX(comments.submitted_on) FROM {0}_issues.comments comments WHERE comments.issue_id = i.id))",
                        project.getProjectName()),
                ROW_MAPPER, commit.getId());
    }

    public List<Issue> selectIssuesRelatedTo(Commit commit) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT i.id, i.type, i.submitted_on, i.fixed_on"
                        + "  FROM {0}_issues.issues i"
                        + "  JOIN {0}.issues_scmlog i2s ON i2s.issue_id = i.id"
                        + "  LEFT JOIN {0}.issues_metrics im ON im.issue_id = i.id"
                        + " WHERE i2s.scmlog_id = ? ",
                        project.getProjectName()),
                ROW_MAPPER, commit.getId());
    }

    public List<Issue> selectIssuesWithNewCommentsOf(Issue issue) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT i.id, i.type, i.submitted_on, i.fixed_on"
                        + " r FROM {0}_issues.issues i"
                        + "  JOIN {0}.issues_scmlog i2s ON i2s.issue_id = i.id"
                        + "  LEFT JOIN {0}.communication_network_metrics cn ON cn.issue_id = i.id"
                        + " WHERE i2s.scmlog_id = ? "
                        // issues with new updates or new comments
                        + "   AND (im.updated_on IS NULL "
                        + "    OR im.updated_on < (SELECT MAX(c.changed_on) FROM {0}_issues.changes c WHERE c.issue_id = i.id))"
                        + "   AND (im.comments_updated_on IS NULL "
                        + "    OR im.comments_updated_on < (SELECT MAX(comments.submitted_on) FROM {0}_issues.comments comments WHERE comments.issue_id = i.id))",
                        project.getProjectName()),
                ROW_MAPPER, issue.getId());
    }

    public List<Issue> selectUpdatedIssuesOf(File file) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT i.id, i.type, i.submitted_on, i.fixed_on"
                        + "  FROM {0}_issues.issues i"
                        + "  JOIN {0}.issues_scmlog i2s ON i2s.issue_id = i.id "
                        + "  JOIN {0}.files_commits fc ON i2s.scmlog_id = fc.commit_id"
                        + "  LEFT JOIN {0}.issues_metrics im ON im.issue_id = i.id"
                        + " WHERE fc.file_id = ? "
                        + "   AND i.fixed_on IS NOT NULL "
                        + "   AND (im.updated_on IS NULL "
                        + "    OR im.updated_on < (SELECT MAX(c.changed_on) FROM {0}_issues.changes c WHERE c.issue_id = i.id))",
                        project.getProjectName()),
                ROW_MAPPER, file.getId());
    }

    public List<Issue> selectIssuesOf(File file) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT i.id, i.type, i.submitted_on, i.fixed_on"
                        + "  FROM {0}_issues.issues i"
                        + "  JOIN {0}.issues_scmlog i2s ON i2s.issue_id = i.id "
                        + "  JOIN {0}_vcs.scmlog s ON i2s.scmlog_id = s.id "
                        + "  JOIN {0}.files_commits fc ON i2s.scmlog_id = fc.commit_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND fc.file_id = ? "
                        + "   AND i.fixed_on IS NOT NULL ",
                        project.getProjectName()),
                ROW_MAPPER, 
                "max_files_per_commit", file.getId());
    }

    public Long countComputedIssues() {
        return jdbcOperations.queryForObject(
                QueryUtils.getQueryForDatabase(
                        "SELECT COUNT(DISTINCT(im.issue_id)) "
                        + "  FROM {0}.issues_metrics im",
                        project.getProjectName()),
                Long.class);
    }

    public Long countFixedIssues() {
        return jdbcOperations.queryForObject(
                QueryUtils.getQueryForDatabase(
                        "SELECT COUNT(DISTINCT(i2s.issue_id)) "
                        + "  FROM {0}.issues_scmlog i2s "
                        + "  JOIN {0}_issues.issues i ON i2s.issue_id = i.id "
                        + " WHERE i.fixed_on IS NOT NULL ",
                        project.getProjectName()),
                Long.class);
    }

    public List<Issue> selectFixedIssuesOf(File changedFile, Commit until) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT i.id, i.type, i.submitted_on, i.fixed_on"
                        + "  FROM {0}_issues.issues i"
                        + "  JOIN {0}.issues_scmlog i2s ON i2s.issue_id = i.id "
                        + "  JOIN {0}.files_commits fc ON i2s.scmlog_id = fc.commit_id"
                        + "  JOIN {0}_vcs.scmlog s ON s.id = i2s.scmlog_id"
                        + "  LEFT JOIN {0}.issues_metrics im ON im.issue_id = i.id"
                        + " WHERE fc.file_id = ? "
                        + "   AND s.date < (SELECT s2.date FROM {0}_vcs.scmlog s2 WHERE s2.id = ?)"
                        + "   AND i.fixed_on IS NOT NULL ",
                        project.getProjectName()),
                ROW_MAPPER, changedFile.getId(), until.getId());
    }

}
