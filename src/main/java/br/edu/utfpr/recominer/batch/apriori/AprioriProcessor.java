package br.edu.utfpr.recominer.batch.apriori;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.FileDao;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.FilePairApriori;
import java.util.List;
import java.util.Properties;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class AprioriProcessor implements ItemProcessor {

    @Inject
    @Mysql
    private EntityManagerFactory factory;

    @Inject
    private JobContext jobContext;

    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;
        final Properties properties = new Properties();
        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, project.getProjectName());
        final GenericDao dao = new GenericDao(factory.createEntityManager(properties));
        final FileDao fileDao = new FileDao(dao, project.getProjectName(), 20);

        final List<Object[]> rawPairFiles = dao.selectNativeWithParams("SELECT pf.id, pf.file1_id, pf.file1_name, pf.file2_id, pf.file2_name"
                + ", (SELECT COUNT(DISTINCT(pfi.issue_id)) FROM pair_file_issue pfi WHERE pf.id = pfi.pair_file_id) FROM pair_file pf ", new Object[]{});

        final Long totalIssuesConsidered = (Long) dao.selectNativeOneWithParams("SELECT COUNT(DISTINCT(pfi.pair_file_id)) FROM pair_file_issue pfi", new Object[]{});

        for (Object[] rawFilePair : rawPairFiles) {
            final Integer id = (Integer) rawFilePair[0];
            final File file1 = new File((Integer) rawFilePair[1], (String) rawFilePair[2]);
            final File file2 = new File((Integer) rawFilePair[3], (String) rawFilePair[4]);

            final FilePair filePair = new FilePair(id, file1, file2);

            final Long filePairIssue = (Long) rawFilePair[5];

            // TODO discuss the issues to consider (e.g. last 100)
            Long file1Issues = fileDao.calculeNumberOfIssues(filePair.getFile1().getFileName());
            Long file2Issues = fileDao.calculeNumberOfIssues(filePair.getFile2().getFileName());

            FilePairApriori apriori = new FilePairApriori(filePair, file1Issues, file2Issues,
                    filePairIssue, totalIssuesConsidered);

        }

        return project;
    }
}
