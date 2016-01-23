package br.edu.utfpr.recominer.batch.aggregator;

import br.edu.utfpr.recominer.dao.GenericDao;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Properties;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

/**
 * Reads the commits and associates with a correspondent issue.
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class AggregatorReader extends AbstractItemReader {

    @Inject
    private EntityManagerFactory factory;

    @Inject
    private JobContext jobContext;
    
    private Iterator<Project> iterator;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        final GenericDao dao = new GenericDao(factory.createEntityManager());
        iterator = dao.selectAll(Project.class).iterator();
    }

    @Override
    public Project readItem() throws Exception {
        // TODO read projects to miner in database
        while (iterator.hasNext()) {
            return iterator.next();
        }

        return null;
    }

    private Properties getParameters() {
        JobOperator operator = BatchRuntime.getJobOperator();
        return operator.getParameters(jobContext.getExecutionId());
    }
}
