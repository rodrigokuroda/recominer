package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.model.IssuesMetrics;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Repository
public class IssuesMetricsRepository extends JdbcRepository<IssuesMetrics, Integer> {

    public IssuesMetricsRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "issues_metrics";

    public static final RowMapper<IssuesMetrics> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                return new IssuesMetrics(
                        rs.getInt("id"),
                        new Issue(rs.getInt("issue_id")),
                        rs.getString("issue_key"), // iej.issue_key
                        rs.getString("issue_type"), // i.type
                        rs.getString("priority"), // i.priority
                        rs.getString("assigned_to"), // assigned.user_id
                        rs.getString("submitted_by"), // submitted.user_id
                        rs.getInt("commenters"), // i.num_commenters
                        rs.getInt("dev_commenters"), // i.num_dev_commenters
                        rs.getTimestamp("updated_on"), // last update
                        rs.getTimestamp("comments_updated_on"), // last comments update
                        rs.getInt("age"), // i.submitted_on
                        rs.getLong("wordiness_body"), // i.body
                        rs.getLong("wordiness_comments"), // comments
                        rs.getInt("comments"),
                        new Commit(rs.getInt("commit_id"))
                );
            };

    public static final RowUnmapper<IssuesMetrics> ROW_UNMAPPER
            = (IssuesMetrics issuesMetrics) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", issuesMetrics.getId());
                mapping.put("issue_id", issuesMetrics.getIssue().getId());
                mapping.put("issue_key", issuesMetrics.getIssueKey());
                mapping.put("issue_type", issuesMetrics.getIssueType());
                mapping.put("assigned_to", issuesMetrics.getAssignedTo());
                mapping.put("submitted_by", issuesMetrics.getSubmittedBy());
                mapping.put("commenters", issuesMetrics.getCommenters());
                mapping.put("dev_commenters", issuesMetrics.getDevCommenters());
                mapping.put("updated_on", issuesMetrics.getUpdatedOn());
                mapping.put("comments_updated_on", issuesMetrics.getCommentsUpdatedOn());
                mapping.put("age", issuesMetrics.getAge());
                mapping.put("wordiness_body", issuesMetrics.getWordnessBody());
                mapping.put("wordiness_comments", issuesMetrics.getWordnessComments());
                mapping.put("comments", issuesMetrics.getComments());
                mapping.put("priority", issuesMetrics.getPriority());
                mapping.put("commit_id", issuesMetrics.getCommit().getId());
                return mapping;
            };

    
//    public IssuesMetrics selectMetricsOf(Issue issue) {
//        return jdbcOperations.queryForObject(
//                getQueryForSchema(
//                        "SELECT * FROM {0}.{1} "
//                        + " WHERE issue_id = ?"), rowMapper, issue.getId());
//    }

    public IssuesMetrics selectMetricsOf(Issue issue, Commit commit) {
        return jdbcOperations.queryForObject(
                getQueryForSchema(
                        "SELECT * FROM {0}.{1} "
                        + " WHERE issue_id = ?"
                        + "   AND commit_id = ?"), rowMapper, issue.getId(), commit.getId());
    }
}
