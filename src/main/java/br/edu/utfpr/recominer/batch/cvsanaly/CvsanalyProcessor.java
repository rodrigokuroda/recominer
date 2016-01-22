package br.edu.utfpr.recominer.batch.cvsanaly;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
@Dependent
public class CvsanalyProcessor implements ItemProcessor {

    @PersistenceContext(unitName = "postgresql")
    private EntityManager em;
    
    @Inject
    private JobContext context;

    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;
        em.createNativeQuery("CREATE SCHEMA IF NOT EXISTS " + project.getProjectName().toLowerCase() + "_vcs").executeUpdate();

        // executing bicho as external process
        ExternalProcess ep = new ExternalProcess(new CvsanalyCommand(project));
        ep.start();
        
        return project;
    }
}
