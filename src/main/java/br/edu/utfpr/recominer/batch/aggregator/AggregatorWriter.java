package br.edu.utfpr.recominer.batch.aggregator;

import br.edu.utfpr.recominer.dao.GenericDao;
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
public class AggregatorWriter extends AbstractItemWriter {
    @PersistenceContext(unitName = "postgresql")
    private EntityManager em;
    
    private GenericDao dao;
    
    @Inject
    private JobContext jobContext;

    @Override
    public void writeItems(List<Object> items) throws Exception {
        dao = new GenericDao(em);
        
        // TODO update status in database
        items.stream().forEach(System.out::println);
    }

}
