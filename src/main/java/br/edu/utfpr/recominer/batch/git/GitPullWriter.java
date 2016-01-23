package br.edu.utfpr.recominer.batch.git;

import java.util.List;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class GitPullWriter extends AbstractItemWriter {

    @Inject
    private JobContext jobContext;
    
    @Override
    public void writeItems(List<Object> items) throws Exception {
        for (Object item : items) {
            System.out.println("Writing: " + item.toString());
        }
    }

}
