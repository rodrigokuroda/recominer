package br.edu.utfpr.recominer.batch.cochange;

import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.model.FilePair;
import java.util.List;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class CochangeIdentifierWriter extends AbstractItemWriter {

    @Inject
    private EntityManagerFactory factory;

    @Inject
    private JobContext jobContext;

    private GenericDao dao;

    @Override
    public void writeItems(List<Object> items) throws Exception {
        dao = new GenericDao(factory.createEntityManager());
        for (Object item : items) {
            final FilePair filePair = (FilePair) item;
            dao.executeNativeQuery("INSERT INTO file_pair (file1_id, file2_id, file1_path, file2_path) VALUES (?, ?, ? ,?)",
                    new Object[]{filePair.getFile1().getId(), filePair.getFile1().getId(), filePair.getFile1().getFileName(), filePair.getFile2().getFileName()});
        }
    }

}
