package br.edu.utfpr.recominer.batch.cvsanaly;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import java.util.Date;
import java.util.List;
import javax.batch.api.chunk.AbstractItemWriter;
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
public class CvsanalyWriter extends AbstractItemWriter {

    @PersistenceContext(unitName = "postgresql")
    private EntityManager em;
    
    @Inject
    private JobContext jobContext;

    @Override
    public void writeItems(List<Object> items) throws Exception {
        for (Object item : items) {
            final Project project = (Project) item;
            project.setLastVcsUpdate(new Date());
            em.merge(project);
        }
    }
    
}
