package br.edu.utfpr.recominer.batch.aggregator;

import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import br.edu.utfpr.recominer.model.Commit;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Properties;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Reads the commits and associates with a correspondent issue.
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class AggregatorReader extends AbstractItemReader {

    @Inject
    private JobContext jobContext;

    @Inject
    private GenericBichoDAO dao;

    private Iterator<Commit> iterator;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        final Properties parameters = getParameters();
        final String project = parameters.getProperty("project");
        final String lastCommitAnalysed = parameters.getProperty("lastCommitAnalysed");

        if (lastCommitAnalysed == null) {
            // reads all commits
            iterator = dao.selectWithParams("SELECT c FROM Commit c",
                    new String[0],
                    new Object[0],
                    Commit.class).iterator();
        } else {
            // reads all commits that was not analysed yet (i.e. new commits without issue)
            iterator = dao.selectWithParams("SELECT c FROM Commit c WHERE c.id > :lastCommitAnalysed",
                    new String[]{"lastCommitAnalysed"},
                    new Object[]{Integer.valueOf(lastCommitAnalysed)},
                    Commit.class).iterator();
        }
    }

    @Override
    public Commit readItem() throws Exception {
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
