package br.edu.utfpr.recominer.batch.git;

import br.edu.utfpr.recominer.batch.cvsanaly.*;
import javax.batch.api.chunk.AbstractItemReader;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class GitPullReader extends AbstractItemReader {

    public GitPullReader() {
    }

    @Override
    public VersionControlSystem readItem() throws Exception {
        // TODO read projects to miner in database
        return new VersionControlSystem("eclipsejdtcore", "https://bugs.eclipse.org/bugs/buglist.cgi?product=JDT", "root", "root");
    }

}
