package br.edu.utfpr.recominer.batch.cochange;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.GenericDao;
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
public class CochangeIdentifierWriter extends AbstractItemWriter {

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
            dao.edit(project);
        }
    }

}