package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.BichoDAO;
import br.edu.utfpr.recominer.dao.BichoPairFileDAO;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.dao.QueryUtils;
import br.edu.utfpr.recominer.metric.committer.CommitterFileMetrics;
import br.edu.utfpr.recominer.metric.committer.CommitterFileMetricsCalculator;
import br.edu.utfpr.recominer.metric.file.FileMetricCalculator;
import br.edu.utfpr.recominer.metric.file.FileMetrics;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import br.edu.utfpr.recominer.metric.network.NetworkMetricsCalculator;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.CommitMetrics;
import br.edu.utfpr.recominer.model.ContextualMetrics;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.Issue;
import br.edu.utfpr.recominer.model.IssueMetrics;
import br.edu.utfpr.recominer.services.metric.Cacher;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
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
public class MetricProcessor implements ItemProcessor {

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

        final int maxFilePerCommit = 20;
        final BichoDAO bichoDAO = new BichoDAO(dao, projectName, maxFilePerCommit);
        final FileMetricDao fileDao = new FileMetricInLastIssuesDao(dao, projectName);
        final BichoPairFileDAO bichoPairFileDAO = new BichoPairFileDAO(dao, projectName, maxFilePerCommit);

        final Cacher cacher = new Cacher(null, bichoPairFileDAO);

        final Set<FilePair> filePairs = new LinkedHashSet<>();
        // TODO which issues we will consider for train?
        final String selectFilePairs = QueryUtils.getQueryForDatabase(
                "SELECT file_pair_id, file1_id, file2_id, file1_path, file2_path "
                + "  FROM {0}.file_pairs fp "
                + "  JOIN {0}.file_pair_apriori fpa ON fpa.file_pair_id = fp.id "
                + " WHERE (fpa.file1_issues > 1 OR fpa.file2_issues > 1) "
                + "   AND (fpa.file1_confidence > 0.5 OR fpa.file2_confidence > 0.5) ",
                projectName);

        final List<Object[]> rawFilePairs = dao.selectNativeWithParams(selectFilePairs, "", "", "");

        final CommitterFileMetricsCalculator committerFileMetricsCalculator = new CommitterFileMetricsCalculator(fileDao);

//        for (FilePair filePair : filePairs) {
//            final String filename = filePair.getFile1().getFileName();
//
//            final Set<Committer> fileCommittersInPreviousVersion
//                    = bichoFileDAO.selectCommitters(filename, fixVersion);
//
//            for (Committer committer : fileCommittersInPreviousVersion) {
//                final CommitterFileMetrics committerFileMetrics;
//                if (pastMajorVersion != null) {
//                    committerFileMetrics
//                            = committerFileMetricsCalculator.calculeForVersion(
//                                    filename, committer, pastMajorVersion);
//                } else {
//                    committerFileMetrics = new EmptyCommitterFileMetrics();
//                }
//                committerFileMetricsList.put(committer, committerFileMetrics);
//                if (committerFileMetrics.getOwnership() > 0.05) { // maior que 5% = major
//                    majorContributorsInPreviousVersion.add(committer);
//                }
//
//                if (ownerExperience.containsKey(filename)) {
//                    ownerExperience.put(filename, Math.max(committerFileMetrics.getExperience(), ownerExperience.get(filename)));
//                } else {
//                    ownerExperience.put(filename, committerFileMetrics.getExperience());
//                }
//            }
//        }
        final FileMetricCalculator fileMetricCalculator = new FileMetricCalculator(fileDao);
        for (FilePair filePair : filePairs) {
            final Set<ContextualMetrics> allFileChanges = new LinkedHashSet<>();

            // par analisado
            final File file = filePair.getFile1();
            final File file2 = filePair.getFile2();
            final String filename = file.getFileName(); // arquivo principal
            final String filename2 = file2.getFileName();

            final Set<Issue> issueWhereFileChanged = fileDao.selectIssues(file);

            for (Issue issue : issueWhereFileChanged) {

                final IssueMetrics issueMetrics = cacher.calculeIssueMetrics(issue);

                final Set<Commit> issueCommits = bichoDAO.selectFilesAndCommitByIssue(issue.getId());

                for (Commit commitInIssue : issueCommits) {
                    final Set<File> filesInCommit = commitInIssue.getFiles();

                    // metricas do arquivo com maior confiança, somente
                    final ContextualMetrics metrics = new ContextualMetrics(filename, filename2, commitInIssue, issueMetrics);

                    if (!commitInIssue.getFiles().contains(file)) {
                        continue;
                    }

                    final CommitMetrics commitMetrics = new CommitMetrics(commitInIssue);

                    final CommitterFileMetrics committerFileMetrics = committerFileMetricsCalculator.calculeForVersion(file, commitInIssue.getCommitter());
//
//                    if (committerFileMetricsList.containsKey(commitInIssue.getCommitter())) {
//                        // committer already commits the file
//                        committerFileMetrics = committerFileMetricsList.get(commitInIssue.getCommitter());
//                    } else {
//                        // committer does not commit the file yet (i.e. first commit of committer)
//                        committerFileMetrics = new EmptyCommitterFileMetrics();
//                    }

                    metrics.setCommitMetrics(commitMetrics);
                    metrics.setCommitterFileMetrics(committerFileMetrics);

                    if (filesInCommit.contains(file2)) { // se houve commit do arquivo 2, então o par mudou
                        // muda a coluna changed para 1, indicando que o par analisado mudou nessa issue
                        metrics.changed();
                    }

                    // calculo das metricas de commit apenas para o primeiro arquivo
                    if (!allFileChanges.contains(metrics)) {
                        // pair file network
                        final NetworkMetrics networkMetrics
                                = new NetworkMetricsCalculator(issue, bichoDAO).getNetworkMetrics();

                        metrics.setNetworkMetrics(networkMetrics);

                        final FileMetrics fileMetrics
                                = fileMetricCalculator.calcule(file, issue, commitInIssue);
                        metrics.setFileMetrics(fileMetrics);
                        allFileChanges.add(metrics);
                    }
                }
            }
        }
        return project;
    }
}
