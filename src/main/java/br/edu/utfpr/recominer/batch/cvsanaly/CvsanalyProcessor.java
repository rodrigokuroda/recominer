package br.edu.utfpr.recominer.batch.cvsanaly;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CvsanalyProcessor implements ItemProcessor {

    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;

        // executing bicho as external process
        ExternalProcess ep = new ExternalProcess(new CvsanalyCommand(project));
        ep.start();

        return project;
    }
}
