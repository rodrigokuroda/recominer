package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.dao.RecominerDao;
import br.edu.utfpr.recominer.metric.network.CommunicationNetworkMetricsCalculator;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import br.edu.utfpr.recominer.model.Issue;
import java.util.Properties;
import java.util.Set;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CommunicationMetricProcessor implements ItemProcessor {

    private final Logger log = LogManager.getLogger();

    @Inject
    @Mysql
    private EntityManagerFactory factory;

    @Inject
    private JobContext jobContext;

    @Override
    public Object processItem(Object item) throws Exception {
        final Project project = (Project) item;
        final Properties properties = new Properties();
        final String projectName = project.getProjectName();
        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, projectName);
        final GenericDao dao = new GenericDao(factory.createEntityManager(properties));

        final RecominerDao recominerDao = new RecominerDao(dao);
        final CommunicationNetworkMetricsCalculator networkMetricsCalculator = new CommunicationNetworkMetricsCalculator(null);
        
        final Set<Issue> nonFixedIssues = recominerDao.selectLastNonFixedIssues(project);
        for (final Issue nonFixedIssue : nonFixedIssues) {
            final NetworkMetrics networkMetrics = networkMetricsCalculator.calcule(nonFixedIssue);
            networkMetricsCalculator.saveNetworkMetrics(networkMetrics);
        }
        
        return project;
    }
}
