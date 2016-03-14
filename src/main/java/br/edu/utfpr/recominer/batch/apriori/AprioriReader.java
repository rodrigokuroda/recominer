package br.edu.utfpr.recominer.batch.apriori;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.GenericDao;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class AprioriReader extends AbstractItemReader {

    @Inject
    private EntityManagerFactory factory;

    @Inject
    private JobContext jobContext;

    private Iterator<Project> iterator;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        final GenericDao dao = new GenericDao(factory.createEntityManager());

        // reads all VCS' projects available (database schemas)
        final List<Project> projects = dao.selectAll(Project.class);
        iterator = projects.listIterator();
    }

    @Override
    public Project readItem() throws Exception {
        if (!iterator.hasNext()) {
            return null;
        }

        final Project project = iterator.next();
        return project;
    }

    private Properties getParameters() {
        JobOperator operator = BatchRuntime.getJobOperator();
        return operator.getParameters(jobContext.getExecutionId());
    }

}