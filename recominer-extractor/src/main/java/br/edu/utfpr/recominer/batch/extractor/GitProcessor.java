package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.externalprocess.ExternalCommand;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import java.io.IOException;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
public class GitProcessor {

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
