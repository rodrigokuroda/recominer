package br.edu.utfpr.recominer.batch.aggregator;

import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import java.util.List;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class AggregatorWriter extends AbstractItemWriter {

    @Inject
    private GenericBichoDAO dao;

    @Override
    public void writeItems(List<Object> items) throws Exception {
        // TODO update status in database
        items.stream().forEach(System.out::println);
    }

}
