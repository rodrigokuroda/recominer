package br.edu.utfpr.recominer.batch.cvsanaly;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import java.util.Date;
import java.util.List;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CvsanalyWriter extends AbstractItemWriter {

    @Inject
    private GenericBichoDAO dao;

    @Override
    public void writeItems(List<Object> items) throws Exception {
        items.stream().forEach(item -> {
            final Project project = (Project) item;
            project.setLastItsUpdate(new Date());
            dao.edit(project);
        });
    }

}
