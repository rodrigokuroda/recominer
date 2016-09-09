package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.metric.file.FileMetrics;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@Repository
public class FileMetricsRepository extends JdbcRepository<FileMetrics, Integer> {

    public FileMetricsRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "file_metrics";

    public static final RowMapper<FileMetrics> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                FileMetrics fileMetrics = new FileMetrics(rs.getInt("id"), new File(rs.getInt("file_id")));
                fileMetrics.setCommit(new Commit(rs.getInt("commit_id")));
                fileMetrics.setAdditions(rs.getLong("additions"));
                fileMetrics.setDeletions(rs.getLong("deletions"));
                fileMetrics.setCommitters(rs.getLong("committers"));
                fileMetrics.setCommits(rs.getLong("commits"));
                fileMetrics.setAge(rs.getLong("age"));
                return fileMetrics;
            };

    public static final RowUnmapper<FileMetrics> ROW_UNMAPPER
            = (FileMetrics fileMetrics) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", fileMetrics.getId());
                mapping.put("file_id", fileMetrics.getFile().getId());
                mapping.put("commit_id", fileMetrics.getCommit().getId());
                mapping.put("additions", fileMetrics.getAdditions());
                mapping.put("deletions", fileMetrics.getDeletions());
                mapping.put("changes", fileMetrics.getChanges());
                mapping.put("committers", fileMetrics.getCommitters());
                mapping.put("commits", fileMetrics.getCommits());
                mapping.put("age", fileMetrics.getAge());
                return mapping;
            };

    public FileMetrics selectMetricsOf(File file, Commit commit) {
        try {
            return jdbcOperations.queryForObject(
                    "SELECT id, file_id, commit_id, additions, deletions, changes, committers, commits, age"
                    + " FROM " + table.getSchema() + ".file_metrics "
                    + " WHERE file_id = ? AND commit_id = ?",
                    ROW_MAPPER, file.getId(), commit.getId());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
}
