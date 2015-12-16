package br.edu.utfpr.recominer.batch.bicho;

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
public class BichoReader extends AbstractItemReader {

    @Inject
    private JobContext jobContext;

    @Override
    public IssueTracker readItem() throws Exception {
        final Properties parameters = getParameters();
        final String project = parameters.getProperty("project");
        final String projectIssueTrackerSystem = parameters.getProperty("projectIssueTrackerSystem");
        final String url = parameters.getProperty("issueTrackerUrl");

        return new IssueTracker(project, url,
                "root", "root",
                IssueTrackerSystem.valueOf(projectIssueTrackerSystem));
    }

    private Properties getParameters() {
        JobOperator operator = BatchRuntime.getJobOperator();
        return operator.getParameters(jobContext.getExecutionId());
    }

}
