package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.core.model.Project;
import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
public class MetricProcessor implements ItemProcessor {

    private final Logger log = LoggerFactory.getLogger(MetricProcessor.class);

    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;
//        final Properties properties = new Properties();
//        final String projectName = project.getProjectName();
//        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, projectName);
//        final GenericDao dao = new GenericDao(factory.createEntityManager(properties));
//
//        final int maxFilePerCommit = 20;
//        final BichoDAO bichoDAO = new BichoDAO(dao, projectName, maxFilePerCommit);
//        final FileMetricDao fileDao = new FileMetricInLastIssuesDao(dao, projectName);
//        final BichoPairFileDAO bichoPairFileDAO = new BichoPairFileDAO(dao, projectName, maxFilePerCommit);
//        final RecominerDao recominerDao = new RecominerDao(dao);
//        final Cacher cacher = new Cacher(null, bichoPairFileDAO);
//
//        final Set<FilePair> filePairs = recominerDao.selectFilePairInOpenedIssues(project);
//        
//        final CommitterFileMetricsCalculator committerFileMetricsCalculator = new CommitterFileMetricsCalculator(fileDao);
//
//        final FileMetricCalculator fileMetricCalculator = new FileMetricCalculator(fileDao);
//        for (FilePair filePair : filePairs) {
//            final Set<ContextualMetrics> allFileChanges = new LinkedHashSet<>();
//
//            // par analisado
//            final File file = filePair.getFile1();
//            final File file2 = filePair.getFile2();
//            final String filename = file.getFileName(); // arquivo principal
//            final String filename2 = file2.getFileName();
//
//            final Set<Issue> issueWhereFileChanged = fileDao.selectIssues(file);
//
//            for (Issue issue : issueWhereFileChanged) {
//
//                final IssueMetrics issueMetrics = cacher.calculeIssueMetrics(issue);
//
//                final Set<Commit> issueCommits = bichoDAO.selectFilesAndCommitByIssue(issue.getId());
//
//                for (Commit commitInIssue : issueCommits) {
//                    final Set<File> filesInCommit = commitInIssue.getFiles();
//
//                    // metricas do arquivo com maior confiança, somente
//                    final ContextualMetrics metrics = new ContextualMetrics(filename, filename2, commitInIssue, issueMetrics);
//
//                    if (!commitInIssue.getFiles().contains(file)) {
//                        continue;
//                    }
//
//                    final CommitMetrics commitMetrics = new CommitMetrics(commitInIssue);
//
//                    final CommitterFileMetrics committerFileMetrics = committerFileMetricsCalculator.calculeForVersion(file, commitInIssue.getCommitter());
////
////                    if (committerFileMetricsList.containsKey(commitInIssue.getCommitter())) {
////                        // committer already commits the file
////                        committerFileMetrics = committerFileMetricsList.get(commitInIssue.getCommitter());
////                    } else {
////                        // committer does not commit the file yet (i.e. first commit of committer)
////                        committerFileMetrics = new EmptyCommitterFileMetrics();
////                    }
//
//                    metrics.setCommitMetrics(commitMetrics);
//                    metrics.setCommitterFileMetrics(committerFileMetrics);
//
//                    if (filesInCommit.contains(file2)) { // se houve commit do arquivo 2, então o par mudou
//                        // muda a coluna changed para 1, indicando que o par analisado mudou nessa issue
//                        metrics.changed();
//                    }
//
//                    // calculo das metricas de commit apenas para o primeiro arquivo
//                    if (!allFileChanges.contains(metrics)) {
//                        // pair file network
//                        final CommunicationNetworkDao communicationNetworkDao = new BichoCommunicationNetworkDao(dao, project);
//                        final NetworkMetrics networkMetrics
//                                = new CommunicationNetworkMetricsCalculator(communicationNetworkDao)
//                                        .calcule(issue);
//
//                        metrics.setNetworkMetrics(networkMetrics);
//
//                        final FileMetrics fileMetrics
//                                = fileMetricCalculator.calcule(file, issue, commitInIssue);
//                        metrics.setFileMetrics(fileMetrics);
//                        allFileChanges.add(metrics);
//                    }
//                }
//            }
//        }
        return project;
    }
    
    public void calcule(Project project) {
//        final Properties properties = new Properties();
//        final String projectName = project.getProjectName();
//        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, projectName);
//        final GenericDao dao = new GenericDao(factory.createEntityManager(properties));
//
//        final IssueMetricCalculator issueMetricCalculator = new IssueMetricCalculator(project, dao);
//        final RecominerDao recominerDao = new RecominerDao(dao);
//        final RecominerMetricDao recominerMetricDao = new RecominerMetricDao(dao, project);
//        
//        final Set<Commit> newCommits = recominerDao.selectNewCommits(project);
//        
//        for (Commit newCommit : newCommits) {
//            // 1. Generating test (input for classification)
//            // 1.1 calcule metrics for new commit and issue related with it
//            final Issue issueFromNewCommit = newCommit.getIssue();
//            issueMetricCalculator.calculeIssueMetrics(issueFromNewCommit);
//            
//            // 2. Generating train
//            for (File file : newCommit.getFiles()) {
//                // 2.1 get all file's commits and issues in past
////                recominerMetricDao.selectFileCommits(file);
//                recominerMetricDao.selectPairFiles(file);
//                // 2.2 calcule issue and commit metrics
//            }
//        }
    }
}
