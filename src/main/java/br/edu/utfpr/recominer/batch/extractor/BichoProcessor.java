package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class BichoProcessor {

    @Inject
    @Mysql
    private EntityManagerFactory factory;

    public int process(Project project) throws Exception {
        EntityManager em = factory.createEntityManager();
        em.createNativeQuery("CREATE SCHEMA IF NOT EXISTS " + project.getProjectName().toLowerCase() + "_issues CHARACTER SET utf8 COLLATE utf8_general_ci").executeUpdate();

        ExternalProcess ep = new ExternalProcess(new BichoCommand(project));
        int processExitCode = ep.startAndWaitFor();

        return processExitCode;
    }
}
