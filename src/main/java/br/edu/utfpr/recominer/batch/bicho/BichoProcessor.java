package br.edu.utfpr.recominer.batch.bicho;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class BichoProcessor implements ItemProcessor {

    @Inject
    @Mysql
    private EntityManagerFactory factory;
    
    @Inject
    private JobContext jobContext;

    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;
        EntityManager em = factory.createEntityManager();
        em.createNativeQuery("CREATE SCHEMA IF NOT EXISTS " + project.getProjectName().toLowerCase() + "_issues").executeUpdate();

        // executing bicho as external process
        ExternalProcess ep = new ExternalProcess(new BichoCommand(project));
        //ep.start();

        return project;
    }
}
