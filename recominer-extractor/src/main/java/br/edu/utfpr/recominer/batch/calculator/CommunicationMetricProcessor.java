package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.metric.network.BichoCommunicationNetworkRepository;
import br.edu.utfpr.recominer.metric.network.CommunicationNetworkMetricsCalculator;
import br.edu.utfpr.recominer.metric.network.CommunicationNetworkRepository;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import java.sql.ResultSet;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CommunicationMetricProcessor {

    private final Logger log = LoggerFactory.getLogger(CommunicationMetricProcessor.class);

    @Inject
    private JdbcTemplate template;

    public NetworkMetrics process(Project project, Issue issue, Commit commit) {
        Integer metricId;
        try {
            metricId = template.queryForObject(
                    QueryUtils.getQueryForDatabase(
                            "SELECT id "
                            + " FROM {0}.communication_network_metrics "
                            + "WHERE issue_id = ?"
                            + "  AND commit_id = ?",  project),
                    (ResultSet rs, int rowNum) -> rs.getInt("id"),
                    issue.getId(), commit.getId());
        } catch (EmptyResultDataAccessException e) {
            metricId = null;
        }

        CommunicationNetworkRepository communicationNetworkDao = new BichoCommunicationNetworkRepository(project, template);
        CommunicationNetworkMetricsCalculator networkMetricsCalculator
                = new CommunicationNetworkMetricsCalculator(communicationNetworkDao);

        final NetworkMetrics networkMetrics = networkMetricsCalculator.calcule(issue, commit);
        networkMetrics.setId(metricId);

        return networkMetrics;
    }
}
