package br.edu.utfpr.recominer.batch.cvsanaly;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CvsanalyProcessor implements ItemProcessor {

    @Inject
    @Mysql
    private EntityManagerFactory factory;

    @Inject
    private JobContext context;

    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;
        
        factory.createEntityManager()
                .createNativeQuery("CREATE SCHEMA IF NOT EXISTS " + project.getProjectName().toLowerCase() + "_vcs CHARACTER SET utf8 COLLATE utf8_general_ci")
                .executeUpdate();

        // executing bicho as external process
        ExternalProcess ep = new ExternalProcess(new CvsanalyCommand(project));
        ep.start();

        return project;
    }
}
