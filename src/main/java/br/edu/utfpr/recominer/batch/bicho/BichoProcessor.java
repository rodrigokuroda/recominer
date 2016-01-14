package br.edu.utfpr.recominer.batch.bicho;

import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.BatchStatus;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class BichoProcessor implements ItemProcessor {

    @Inject
    @Mysql
    private EntityManager entityManager;

    @Override
    public Object processItem(Object item) throws Exception {
        IssueTracker it = (IssueTracker) item;
        
        entityManager.createNativeQuery("CREATE SCHEMA IF NOT EXISTS " + it.getProject() + "_issues").executeUpdate();
        entityManager.flush();
        entityManager.close();

        // executing bicho as external process
        ExternalProcess ep = new ExternalProcess(new BichoCommand(it));
        ep.start();

        return BatchStatus.COMPLETED.toString();
    }
}
