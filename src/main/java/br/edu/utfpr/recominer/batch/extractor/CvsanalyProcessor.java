package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CvsanalyProcessor {

    @Inject
    @Mysql
    private EntityManagerFactory factory;

    public int process(Project project) throws IOException, InterruptedException {
        factory.createEntityManager()
                .createNativeQuery("CREATE SCHEMA IF NOT EXISTS " + project.getProjectName().toLowerCase() + "_vcs CHARACTER SET utf8 COLLATE utf8_general_ci")
                .executeUpdate();

        ExternalProcess ep = new ExternalProcess(new CvsanalyCommand(project));
        int processExitCode = ep.startAndWaitFor();

        return processExitCode;
    }
}
