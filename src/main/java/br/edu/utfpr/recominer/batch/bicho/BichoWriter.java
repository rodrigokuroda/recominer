package br.edu.utfpr.recominer.batch.bicho;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import java.util.Date;
import java.util.List;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
@Dependent
public class BichoWriter extends AbstractItemWriter {

    @Inject
    private GenericBichoDAO dao;
    
    @Inject
    private JobContext jobContext;
    
    @Override
    public void writeItems(final List<Object> items) throws Exception {
        items.stream().forEach(item -> {
            final Project project = (Project) item;
            project.setLastItsUpdate(new Date());
            dao.edit(project);
        });
    }

}
