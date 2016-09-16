package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.model.CommitMetrics;
import br.edu.utfpr.recominer.core.model.Committer;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Repository
public class CommitMetricsRepository extends JdbcRepository<CommitMetrics, Integer> {

    public CommitMetricsRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "commit_metrics";

    public static final RowMapper<CommitMetrics> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                final Committer committer = new Committer(rs.getInt("committer_id"), rs.getString("committer_name"), null);
                final Commit commit = new Commit(rs.getInt("commit_id"), rs.getString("revision"), committer);
                CommitMetrics commitMetrics = new CommitMetrics(rs.getInt("id"), commit);
                return commitMetrics;
            };

    public static final RowUnmapper<CommitMetrics> ROW_UNMAPPER
            = (CommitMetrics commitMetrics) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", commitMetrics.getId());
                mapping.put("commit_id", commitMetrics.getCommit().getId());
                mapping.put("revision", commitMetrics.getRevision());
                mapping.put("committer_id", commitMetrics.getCommit().getCommitter().getId());
                mapping.put("committer_name", commitMetrics.getCommit().getCommitter().getNameOrEmail());
                return mapping;
            };

    public CommitMetrics selectMetricsOf(Commit commit) {
        try {
            return jdbcOperations.queryForObject(
                    getQueryForSchema(
                            "SELECT id, commit_id, revision, committer_id, committer_name"
                            + "  FROM {0}.commit_metrics "
                            + " WHERE commit_id = ?"),
                    rowMapper, commit.getId());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

}
