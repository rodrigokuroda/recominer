package br.edu.utfpr.recominer.batch.bicho;

import br.edu.utfpr.recominer.dao.EntityManagerProducer;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.BatchStatus;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class BichoProcessor implements ItemProcessor {

    @Inject
    private EntityManagerProducer producer;

    @Override
    public Object processItem(Object item) throws Exception {
        IssueTracker it = (IssueTracker) item;

        GenericBichoDAO dao = new GenericBichoDAO(producer.createEntityManager(it.getProject() + "_issues"));
        dao.getEntityManager().setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, it.getProject() + "_issues");
        dao.selectWithParams("SELECT COUNT(i) FROM Issue i", new String[0], new Object[0]);

        // executing bicho as external process
        ExternalProcess ep = new ExternalProcess(new BichoCommand(it.getProject(), it.getUrl(), it.getIssueTrackerSystem()));
        ep.start();

        return BatchStatus.COMPLETED.toString();
    }
}
