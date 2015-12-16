package br.edu.utfpr.recominer.batch.cvsanaly;

import java.util.Properties;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CvsanalyReader extends AbstractItemReader {

    @Inject
    private JobContext jobContext;

    @Override
    public VersionControlSystem readItem() throws Exception {
        final Properties parameters = getParameters();
        final String project = parameters.getProperty("project");
        final String url = parameters.getProperty("issueTrackerUrl");

        return new VersionControlSystem(project, url, "root", "root");
    }

    private Properties getParameters() {
        JobOperator operator = BatchRuntime.getJobOperator();
        return operator.getParameters(jobContext.getExecutionId());
    }
}
