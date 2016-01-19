package br.edu.utfpr.recominer.batch.cvsanaly;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.EntityManagerProducer;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CvsanalyProcessor implements ItemProcessor {

    @Inject
    private EntityManagerProducer entityManagerProducer;

    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;
        EntityManager entityManager = entityManagerProducer.createMysqlEntityManager();
        entityManager.createNativeQuery("CREATE SCHEMA IF NOT EXISTS " + project.getProjectName().toLowerCase() + "_vcs").executeUpdate();
        entityManagerProducer.closeEntityManager(entityManager);

        // executing bicho as external process
        ExternalProcess ep = new ExternalProcess(new CvsanalyCommand(project));
        ep.start();

        return project;
    }
}
