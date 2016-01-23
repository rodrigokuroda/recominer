package br.edu.utfpr.recominer.batch.cvsanaly;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.GenericDao;
import java.util.Date;
import java.util.List;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CvsanalyWriter extends AbstractItemWriter {
    
    private final Logger log = LogManager.getLogger();
    
    @Inject
    private EntityManagerFactory factory;
    
    @Inject
    private JobContext jobContext;

    private GenericDao dao;
    
    @Override
    public void writeItems(List<Object> items) throws Exception {
        dao = new GenericDao(factory.createEntityManager());
        for (Object item : items) {
            final Project project = (Project) item;
            project.setLastVcsUpdate(new Date());
            dao.edit(project);
        }
    }
    
}
