package br.edu.utfpr.recominer.batch.git;

import br.edu.utfpr.recominer.batch.cvsanaly.*;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import java.io.IOException;
import java.util.Properties;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class GitPullProcessor implements ItemProcessor {

    private static final Logger LOGGER = LogManager.getLogger();

    @Inject
    private JobContext jobContext;

    @Override
    public Object processItem(Object item) throws Exception {
        VersionControlSystem vcs = (VersionControlSystem) item;
        try {
            final String url = vcs.getUrl();
            final String name = vcs.getName();

            // executing bicho as external process
            ExternalProcess ep = new ExternalProcess(new GitPullCommand(name, url));
            ep.start();

        } catch (InterruptedException | IOException ex) {
            LOGGER.error("An error occurred while executing job.", ex);
            return BatchStatus.FAILED.toString();
        }

        return BatchStatus.COMPLETED.toString();
    }

    private Properties getParameters() {
        JobOperator operator = BatchRuntime.getJobOperator();
        return operator.getParameters(jobContext.getExecutionId());

    }
}
