package br.edu.utfpr.recominer.batch.cvsanaly;

import java.util.List;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CvsanalyWriter extends AbstractItemWriter {

    @Override
    public void writeItems(List<Object> items) throws Exception {
        for (Object item : items) {
            System.out.println("Writing: " + item.toString());
        }
    }

}
