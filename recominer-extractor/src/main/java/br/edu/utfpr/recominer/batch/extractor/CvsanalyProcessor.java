package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CvsanalyProcessor {

    @Inject
    private JdbcTemplate template;

    public int process(Project project) throws IOException, InterruptedException {
        template.execute("CREATE SCHEMA IF NOT EXISTS " + project.getProjectName().toLowerCase() + "_vcs CHARACTER SET utf8 COLLATE utf8_general_ci");

        ExternalProcess ep = new ExternalProcess(new CvsanalyCommand(project));
        int processExitCode = ep.startAndWaitFor();

        return processExitCode;
    }
}
