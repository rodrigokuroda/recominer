package br.edu.utfpr.recominer.batch.aggregator;

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
public class AggregatorWriter extends AbstractItemWriter {
    
    @Inject
    private EntityManagerFactory factory;
    
    @Inject
    private JobContext jobContext;

    @Override
    public void writeItems(List<Object> items) throws Exception {
        final GenericDao dao = new GenericDao(factory.createEntityManager());
        
        // TODO update status in database
        items.stream().forEach(System.out::println);
    }

}
