package br.edu.utfpr.recominer.batch.bicho;

import java.util.List;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class BichoWriter extends AbstractItemWriter {

    @Override
    public void writeItems(List<Object> items) throws Exception {
        // TODO update status in database
        for (Object item : items) {
            System.out.println("Writing: " + item.toString());
        }
    }

}
