package br.edu.utfpr.recominer.batch.git;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.batch.cvsanaly.VersionControl;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.externalprocess.ExternalCommand;
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
    
    @Inject
    private GenericDao dao;

    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;
        try {
            final VersionControl versionControl = project.getVersionControl();
            final ExternalCommand command;
            
            final String localGitRepositoryPath = getParameters().getProperty("localGitRepositoryPath", System.getProperty("user.dir"));
            if (project.getLastVcsUpdate() != null) {
                command = new GitPullCommand(versionControl);
            } else {
                command = new GitCloneCommand(versionControl, localGitRepositoryPath);
            }
            // executing bicho as external process
            ExternalProcess ep = new ExternalProcess(command);
            ep.start();

        } catch (InterruptedException | IOException ex) {
            LOGGER.error("An error occurred while executing job.", ex);
            return BatchStatus.FAILED.toString();
        }

        return project;
    }

    private Properties getParameters() {
        JobOperator operator = BatchRuntime.getJobOperator();
        return operator.getParameters(jobContext.getExecutionId());

    }
}
