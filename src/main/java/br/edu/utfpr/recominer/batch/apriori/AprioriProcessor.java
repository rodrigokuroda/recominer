package br.edu.utfpr.recominer.batch.apriori;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.FileDao;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.Mysql;
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

        //Cacher cacher = new Cacher(fileDAO);
        dao.executeNativeQuery("SELECT id, file1_id, file2_id, file1_name, file2_name"
                + " FROM pair_file", new Object[]{});

//        for (FilePair fileFile : pairFiles.keySet()) {
//            // TODO discuss the issues to consider (e.g. last 100)
//            Long file1Issues = fileDao.calculeNumberOfIssues(fileFile.getFile1().getFileName());
//            Long file2Issues = fileDao.calculeNumberOfIssues(fileFile.getFile2().getFileName());
//
//            FilePairAprioriOutput filePairOutput = pairFiles.get(fileFile);
//
//            FilePairApriori apriori = new FilePairApriori(fileFile, file1Issues, file2Issues,
//                    filePairOutput.getIssuesIdWeight(), allConsideredIssues.size());
//
//            filePairOutput.setFilePairApriori(apriori);
//
//            pairFileList.add(filePairOutput);
//        }

        return project;
    }
}
