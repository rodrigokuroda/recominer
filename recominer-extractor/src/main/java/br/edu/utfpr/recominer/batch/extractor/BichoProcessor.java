package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import br.edu.utfpr.recominer.core.model.Project;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
public class BichoProcessor {

    @Inject
    private JdbcTemplate template;

    public int process(Project project) throws Exception {
        template.execute("CREATE SCHEMA IF NOT EXISTS " + project.getProjectName().toLowerCase() + "_issues CHARACTER SET utf8 COLLATE utf8_general_ci");
        
        final BichoCommand bichoCommand = new BichoCommand(project);
        final ExternalProcess ep = new ExternalProcess(bichoCommand);
        final int processExitCode = ep.startAndWaitFor();

        return processExitCode;
    }
}
