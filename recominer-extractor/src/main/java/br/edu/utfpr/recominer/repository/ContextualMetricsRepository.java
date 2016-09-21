package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import br.edu.utfpr.recominer.metric.file.FileMetrics;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import br.edu.utfpr.recominer.model.CommitMetrics;
import br.edu.utfpr.recominer.model.ContextualMetrics;
import br.edu.utfpr.recominer.model.IssuesMetrics;
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
public class ContextualMetricsRepository extends JdbcRepository<ContextualMetrics, Integer> {

    public ContextualMetricsRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "contextual_metrics";

    public static final RowMapper<ContextualMetrics> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                ContextualMetrics contextualMetrics = new ContextualMetrics();
                contextualMetrics.setId(rs.getInt("id"));
                contextualMetrics.setCommit(new Commit(rs.getInt("commit_id")));
                contextualMetrics.setFile(new File(rs.getInt("file_id")));
                contextualMetrics.setNetworkMetrics(new NetworkMetrics(rs.getInt("network_metrics_id")));
                contextualMetrics.setIssueMetrics(new IssuesMetrics(rs.getInt("issue_metrics_id")));
                contextualMetrics.setCommitMetrics(new CommitMetrics(rs.getInt("commit_metrics_id")));
                contextualMetrics.setFileMetrics(new FileMetrics(rs.getInt("file_metrics_id")));
                return contextualMetrics;
            };
    
    public static final RowUnmapper<ContextualMetrics> ROW_UNMAPPER
            = (ContextualMetrics contextualMetrics) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", contextualMetrics.getId());
                mapping.put("commit_id", contextualMetrics.getCommit().getId());
                mapping.put("file_id", contextualMetrics.getFile().getId());
                mapping.put("network_metrics_id", contextualMetrics.getNetworkMetrics().getId());
                mapping.put("issue_metrics_id", contextualMetrics.getIssueMetrics().getId());
                mapping.put("commit_metrics_id", contextualMetrics.getCommitMetrics().getId());
                mapping.put("file_metrics_id", contextualMetrics.getFileMetrics().getId());
                return mapping;
            };

}