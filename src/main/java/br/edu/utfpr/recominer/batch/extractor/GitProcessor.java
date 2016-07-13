package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.externalprocess.ExternalCommand;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import java.io.IOException;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class GitProcessor {

    private final Logger log = LogManager.getLogger();
    
    public int process(Project project) throws IOException, InterruptedException {
        final ExternalCommand command;

        if (project.getLastVcsUpdate() != null) {
            command = new GitPullCommand(project);
        } else {
            command = new GitCloneCommand(project);
        }
        // executing bicho as external process
        ExternalProcess ep = new ExternalProcess(command);
        return ep.startAndWaitFor();
    }
}
