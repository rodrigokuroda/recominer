package br.edu.utfpr.recominer.batch.apriori;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.FileDao;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.dao.QueryUtils;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.FilePairApriori;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class AprioriProcessor implements ItemProcessor {

    private final Logger log = LogManager.getLogger();

    @Inject
    @Mysql
    private EntityManagerFactory factory;

    @Inject
    private JobContext jobContext;

    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;
        final Properties properties = new Properties();
        final String projectName = project.getProjectName();
        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, projectName);
        final GenericDao dao = new GenericDao(factory.createEntityManager(properties));
        final FileDao fileDao = new FileDao(dao, projectName, 20);

        // TODO discuss the issues to consider (e.g. last 100)
        final List<Object[]> rawPairFiles = dao.selectNativeWithParams(
                QueryUtils.getQueryForDatabase("SELECT pf.id, pf.file1_id, pf.file1_path, pf.file2_id, pf.file2_path"
                        + ", (SELECT COUNT(DISTINCT(pfi.issue_id)) FROM {0}.file_pair_issue pfi WHERE pf.id = pfi.file_pair_id) FROM {0}.file_pairs pf ",
                        projectName), new Object[]{});

        final Long totalIssuesConsidered = (Long) dao.selectNativeOneWithParams(
                QueryUtils.getQueryForDatabase("SELECT COUNT(DISTINCT(pfi.file_pair_id)) FROM {0}.file_pair_issue pfi", projectName),
                new Object[]{});

        final String countIssuesOfFileSql
                = QueryUtils.getQueryForDatabase("SELECT COUNT(DISTINCT(i2s.issue_id))"
                        + "  FROM avro.commits com"
                        + "  JOIN avro_issues.issues_scmlog i2s ON i2s.scmlog_id = com.commit_id"
                        + "  JOIN avro.file_pair_issue fpi ON fpi.issue_id = i2s.issue_id"
                        + " WHERE com.file_path = ?"
                        + "   AND EXISTS (SELECT 1 FROM avro_issues.issues_fix_version ifv WHERE ifv.issue_id = i2s.issue_id)", projectName);
        
        log.debug("Total pair file to calculate apriori: " + rawPairFiles.size());
        
        final FileWriter fw = new FileWriter(new java.io.File("/home/kuroda/Desktop/test.csv"));
        fw.append(FilePairApriori.getToStringHeader()).append("\r\n");
        for (Object[] rawFilePair : rawPairFiles) {
            final Integer id = (Integer) rawFilePair[0];
            final File file1 = new File((Integer) rawFilePair[1], (String) rawFilePair[2]);
            final File file2 = new File((Integer) rawFilePair[3], (String) rawFilePair[4]);

            final FilePair filePair = new FilePair(id, file1, file2);

            final Long filePairIssue = (Long) rawFilePair[5];

            Long file1Issues = dao.selectNativeOneWithParams(countIssuesOfFileSql, filePair.getFile1().getFileName());
            Long file2Issues = dao.selectNativeOneWithParams(countIssuesOfFileSql, filePair.getFile2().getFileName());

            FilePairApriori apriori = new FilePairApriori(filePair, file1Issues, file2Issues,
                    filePairIssue, totalIssuesConsidered);

            log.debug(apriori.toString());
            fw.append(apriori.toString()).append("\r\n");

        }
        fw.flush();
        fw.close();

        return project;
    }
}
