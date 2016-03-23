package br.edu.utfpr.recominer.batch.bicho;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.RecominerDao;
import java.util.Date;
import java.util.List;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class BichoWriter extends AbstractItemWriter {

    @Inject
    private EntityManagerFactory factory;
    
    @Inject
    private JobContext jobContext;

    private GenericDao dao;
    
    @Override
    public void writeItems(List<Object> items) throws Exception {
        dao = new GenericDao(factory.createEntityManager());
        final RecominerDao recominerDao = new RecominerDao(dao);
        for (Object item : items) {
            final Project project = (Project) item;
            project.setLastItsUpdate(new Date());
            recominerDao.updateProjectUpdate(project);
        }
    }

}
