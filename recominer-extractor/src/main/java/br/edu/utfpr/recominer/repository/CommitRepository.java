package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Committer;
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
public class CommitRepository extends JdbcRepository<Commit, Integer> {

    public CommitRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "commit_id";
    private static final String TABLE_NAME = "commits";

    public static final RowMapper<Commit> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                Commit commit = new Commit(rs.getInt("commit_id"));
                commit.setRevision(rs.getString("rev"));
                commit.setCommitDate(rs.getDate("date"));
                commit.setCommitter(new Committer(rs.getInt("committer_id")));
                return commit;
            };
    public static final RowUnmapper<Commit> ROW_UNMAPPER
            = (Commit commit) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("commit_id", commit.getId());
                mapping.put("rev", commit.getRevision());
                mapping.put("date", commit.getCommitDate());
                mapping.put("committer", commit.getCommitter().getId());
                return mapping;
            };

    /**
     * Gets all commits associated with non-fixed issues. The commits must have
     * a number of maximum files configured in 'recominer.configuration' table,
     * as key 'max_files_per_commit'.
     *
     * @return Commits of non-fixed issues.
     */
    public List<Commit> selectNewCommits() {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date"
                        + "  FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}_issues.issues i ON i.id = i2s.issue_id "
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND i.fixed_on IS NULL",
                        getTable().getSchema()),
                ROW_MAPPER,
                "max_files_per_commit");
    }

    @Deprecated
    protected List<Commit> selectCommitsOf(Issue issue) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND i2s.issue_id = ?", table.getSchema()),
                ROW_MAPPER,
                "max_files_per_commit", issue.getId());
    }

    public List<Commit> selectCommitsOf(Issue issue, File file) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}.files_commits fc ON i2s.scmlog_id = fc.commit_id"
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND i2s.issue_id = ?"
                        + "   AND fc.file_id = ?", table.getSchema()),
                ROW_MAPPER,
                "max_files_per_commit", issue.getId(), file.getId());
    }

    public Commit selectWithCommitter(Commit commit) {
        return jdbcOperations.queryForObject(
                getQueryForSchema(
                        "SELECT c.commit_id, c.rev, c.committer_id, p.name, p.email, c.date"
                        + "  FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}_vcs.people p ON p.id = c.committer_id "
                        + " WHERE c.commit_id = ?"),
                (ResultSet rs, int rowNum) -> {
                    Commit c = new Commit(rs.getInt("commit_id"));
                    c.setRevision(rs.getString("rev"));
                    c.setCommitDate(rs.getDate("date"));
                    c.setCommitter(new Committer(rs.getInt("committer_id"), rs.getString("name"), rs.getString("email")));
                    return c;
                },
                commit.getId());
    }

}
