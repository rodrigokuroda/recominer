package br.edu.utfpr.recominer.batch.apriori;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.dao.QueryUtils;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.FilePairApriori;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        //final FileDao fileDao = new FileDao(dao, projectName, 20);
        
        final String selectFilePair;
        if (project.getLastAprioriUpdate() == null) {
            selectFilePair = QueryUtils.getQueryForDatabase(
                    "SELECT pf.id, pf.file1_id, pf.file1_path, pf.file2_id, pf.file2_path"
                    + ", (SELECT COUNT(DISTINCT(pfi.issue_id)) FROM {0}.file_pair_issue pfi WHERE pf.id = pfi.file_pair_id) "
                    + "FROM {0}.file_pairs pf ",
                    projectName);
        } else {
            selectFilePair = QueryUtils.getQueryForDatabase(
                    "SELECT pf.id, pf.file1_id, pf.file1_path, pf.file2_id, pf.file2_path"
                    + ", (SELECT COUNT(DISTINCT(pfi.issue_id)) FROM {0}.file_pair_issue pfi WHERE pf.id = pfi.file_pair_id) "
                    + "FROM {0}.file_pairs pf "
                    + "WHERE pf.updated_on > ?",
                    projectName);
        }

        // TODO discuss the issues to consider (e.g. last 100)
        final List<Object[]> rawPairFiles = dao.selectNativeWithParams(
                selectFilePair, new Object[]{project.getLastAprioriUpdate()});

        final Long totalIssuesConsidered = (Long) dao.selectNativeOneWithParams(
                QueryUtils.getQueryForDatabase("SELECT COUNT(DISTINCT(pfi.file_pair_id)) FROM {0}.file_pair_issue pfi", projectName),
                new Object[]{});

        final String countIssuesOfFileSql
                = QueryUtils.getQueryForDatabase("SELECT COUNT(DISTINCT(i2s.issue_id))"
                        + "  FROM {0}.commits com"
                        + "  JOIN {0}_issues.issues_scmlog i2s ON i2s.scmlog_id = com.commit_id"
                        + "  JOIN {0}.file_pair_issue fpi ON fpi.issue_id = i2s.issue_id"
                        + " WHERE com.file_path = ?"
                        + "   AND EXISTS (SELECT 1 FROM {0}_issues.issues_fix_version ifv WHERE ifv.issue_id = i2s.issue_id)", projectName);
        
        log.debug("Total pair file to calculate apriori: " + rawPairFiles.size());

        final String insertApriori
                = QueryUtils.getQueryForDatabase(
                        "INSERT INTO {0}.file_pair_apriori "
                        + " (file_pair_id, file_pair_issues, file1_issues, file2_issues, file1_support, file2_support, file_pair_support, file1_confidence, file2_confidence, updated_on) "
                        + " VALUES "
                        + " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", projectName);

        final Date now = new Date();
        final Map<String, Long> fileIssues = new HashMap<>();
        for (Object[] rawFilePair : rawPairFiles) {
            final Integer id = (Integer) rawFilePair[0];
            final File file1 = new File((Integer) rawFilePair[1], (String) rawFilePair[2]);
            final File file2 = new File((Integer) rawFilePair[3], (String) rawFilePair[4]);

            final FilePair filePair = new FilePair(id, file1, file2);

            final Long filePairIssue = (Long) rawFilePair[5];

            final Long file1Issues;
            if (fileIssues.containsKey(file1.getFileName())) {
                file1Issues = fileIssues.get(file1.getFileName());
            } else {
                file1Issues = dao.selectNativeOneWithParams(countIssuesOfFileSql, file1.getFileName());
                fileIssues.put(filePair.getFile1().getFileName(), file1Issues);
            }

            final Long file2Issues;
            if (fileIssues.containsKey(file2.getFileName())) {
                file2Issues = fileIssues.get(file2.getFileName());
            } else {
                file2Issues = dao.selectNativeOneWithParams(countIssuesOfFileSql, file2.getFileName());
                fileIssues.put(file2.getFileName(), file1Issues);
            }
            final FilePairApriori apriori = new FilePairApriori(filePair, file1Issues, file2Issues,
                    filePairIssue, totalIssuesConsidered);

            dao.executeNativeQuery(insertApriori, new Object[]{
                filePair.getId(),
                filePairIssue,
                file1Issues,
                file2Issues,
                apriori.getSupportFile(),
                apriori.getSupportFile2(),
                apriori.getSupportFilePair(),
                apriori.getConfidence(),
                apriori.getConfidence2(),
                now});
        }

        if (!rawPairFiles.isEmpty()) {
            project.setLastAprioriUpdate(now);
        }
        return project;
    }
}
