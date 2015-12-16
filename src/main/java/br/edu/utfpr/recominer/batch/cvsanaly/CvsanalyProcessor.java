package br.edu.utfpr.recominer.batch.cvsanaly;

import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.BatchStatus;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CvsanalyProcessor implements ItemProcessor {

    @Override
    public Object processItem(Object item) throws Exception {
        VersionControlSystem vcs = (VersionControlSystem) item;

        // executing bicho as external process
        ExternalProcess ep = new ExternalProcess(new CvsanalyCommand(vcs.getName(), vcs.getUrl()));
        ep.start();

        return BatchStatus.COMPLETED.toString();
    }
}
