package br.edu.utfpr.recominer.batch.calculator;

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
public class CommunicationMetricWriter extends AbstractItemWriter {

    @Inject
    private EntityManagerFactory factory;

    @Inject
    private JobContext jobContext;

    private GenericDao dao;

    @Override
    public void writeItems(List<Object> items) throws Exception {
//        dao = new GenericDao(factory.createEntityManager());
//        for (Object item : items) {
//            final Project project = (Project) item;
//            dao.executeNativeQuery(
//                    "UPDATE recominer.project SET last_apriori_update = ? WHERE id = ?",
//                    new Object[]{project.getLastAprioriUpdate(), project.getId()});
//        }
    }

}
