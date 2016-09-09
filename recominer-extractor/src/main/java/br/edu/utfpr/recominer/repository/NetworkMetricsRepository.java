package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@Repository
public class NetworkMetricsRepository extends JdbcRepository<NetworkMetrics, Integer> {

    public NetworkMetricsRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "communication_network_metrics";

    public static final RowMapper<NetworkMetrics> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                NetworkMetrics networkMetrics = new NetworkMetrics();
                networkMetrics.setId(rs.getInt("id"));
                networkMetrics.setIssue(new Issue(rs.getInt("issue_id")));
                networkMetrics.setCommit(new Commit(rs.getInt("commit_id")));
                networkMetrics.setBetweennessSum(rs.getDouble("betweenness_sum"));
                networkMetrics.setBetweennessMean(rs.getDouble("betweenness_mean"));
                networkMetrics.setBetweennessMedian(rs.getDouble("betweenness_median"));
                networkMetrics.setBetweennessMaximum(rs.getDouble("betweenness_maximum"));
                networkMetrics.setClosenessSum(rs.getDouble("closeness_sum"));
                networkMetrics.setClosenessMean(rs.getDouble("closeness_mean"));
                networkMetrics.setClosenessMedian(rs.getDouble("closeness_median"));
                networkMetrics.setClosenessMaximum(rs.getDouble("closeness_maximum"));
                networkMetrics.setConstraintSum(rs.getDouble("constraint_sum"));
                networkMetrics.setConstraintMean(rs.getDouble("constraint_mean"));
                networkMetrics.setConstraintMedian(rs.getDouble("constraint_median"));
                networkMetrics.setConstraintMaximum(rs.getDouble("constraint_maximum"));
                networkMetrics.setDegreeSum(rs.getDouble("degree_sum"));
                networkMetrics.setDegreeMean(rs.getDouble("degree_mean"));
                networkMetrics.setDegreeMedian(rs.getDouble("degree_median"));
                networkMetrics.setDegreeMaximum(rs.getDouble("degree_maximum"));
                networkMetrics.setEffectiveSizeSum(rs.getDouble("effective_size_sum"));
                networkMetrics.setEffectiveSizeMean(rs.getDouble("effective_size_mean"));
                networkMetrics.setEffectiveSizeMedian(rs.getDouble("effective_size_median"));
                networkMetrics.setEffectiveSizeMaximum(rs.getDouble("effective_size_maximum"));
                networkMetrics.setEfficiencySum(rs.getDouble("efficiency_sum"));
                networkMetrics.setEfficiencyMean(rs.getDouble("efficiency_mean"));
                networkMetrics.setEfficiencyMedian(rs.getDouble("efficiency_median"));
                networkMetrics.setEfficiencyMaximum(rs.getDouble("efficiency_maximum"));
                networkMetrics.setHierarchySum(rs.getDouble("hierarchy_sum"));
                networkMetrics.setHierarchyMean(rs.getDouble("hierarchy_mean"));
                networkMetrics.setHierarchyMedian(rs.getDouble("hierarchy_median"));
                networkMetrics.setHierarchyMaximum(rs.getDouble("hierarchy_maximum"));
                networkMetrics.setTies(rs.getLong("ties"));
                networkMetrics.setDensity(rs.getDouble("density"));
                networkMetrics.setDiameter(rs.getDouble("diameter"));
                networkMetrics.setSize(rs.getLong("size"));
                return networkMetrics;
            };
    
    public static final RowUnmapper<NetworkMetrics> ROW_UNMAPPER
            = (NetworkMetrics networkMetrics) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", networkMetrics.getId());
                mapping.put("issue_id", networkMetrics.getIssue().getId());
                mapping.put("commit_id", networkMetrics.getCommit().getId());
                mapping.put("betweenness_sum", networkMetrics.getBetweennessSum());
                mapping.put("betweenness_mean", networkMetrics.getBetweennessMean());
                mapping.put("betweenness_median", networkMetrics.getBetweennessMedian());
                mapping.put("betweenness_maximum", networkMetrics.getBetweennessMaximum());
                mapping.put("closeness_sum", networkMetrics.getClosenessSum());
                mapping.put("closeness_mean", networkMetrics.getClosenessMean());
                mapping.put("closeness_median", networkMetrics.getClosenessMedian());
                mapping.put("closeness_maximum", networkMetrics.getClosenessMaximum());
                mapping.put("constraint_sum", networkMetrics.getConstraintSum());
                mapping.put("constraint_mean", networkMetrics.getConstraintMean());
                mapping.put("constraint_median", networkMetrics.getConstraintMedian());
                mapping.put("constraint_maximum", networkMetrics.getConstraintMaximum());
                mapping.put("degree_sum", networkMetrics.getDegreeSum());
                mapping.put("degree_mean", networkMetrics.getDegreeMean());
                mapping.put("degree_median", networkMetrics.getDegreeMedian());
                mapping.put("degree_maximum", networkMetrics.getDegreeMaximum());
                mapping.put("effective_size_sum", networkMetrics.getEffectiveSizeSum());
                mapping.put("effective_size_mean", networkMetrics.getEffectiveSizeMean());
                mapping.put("effective_size_median", networkMetrics.getEffectiveSizeMedian());
                mapping.put("effective_size_maximum", networkMetrics.getEffectiveSizeMaximum());
                mapping.put("efficiency_sum", networkMetrics.getEfficiencySum());
                mapping.put("efficiency_mean", networkMetrics.getEfficiencyMean());
                mapping.put("efficiency_median", networkMetrics.getEfficiencyMedian());
                mapping.put("efficiency_maximum", networkMetrics.getEfficiencyMaximum());
                mapping.put("hierarchy_sum", networkMetrics.getHierarchySum());
                mapping.put("hierarchy_mean", networkMetrics.getHierarchyMean());
                mapping.put("hierarchy_median", networkMetrics.getHierarchyMedian());
                mapping.put("hierarchy_maximum", networkMetrics.getHierarchyMaximum());
                mapping.put("ties", networkMetrics.getTies());
                mapping.put("density", networkMetrics.getDensity());
                mapping.put("diameter", networkMetrics.getDiameter());
                mapping.put("size", networkMetrics.getSize());
                return mapping;
            };

    public NetworkMetrics selectMetricsOf(Issue issue) {
        return jdbcOperations.queryForObject(getQueryForSchema(
                "SELECT * FROM {0}.{1} WHERE issue_id = ?"), 
                rowMapper, issue.getId());
    }

    public NetworkMetrics selectMetricsOf(Issue issue, Commit commit) {
        return jdbcOperations.queryForObject(getQueryForSchema(
                "SELECT * FROM {0}.{1} WHERE issue_id = ? AND commit_id = ?"), 
                rowMapper, issue.getId(), commit.getId());
    }
}
