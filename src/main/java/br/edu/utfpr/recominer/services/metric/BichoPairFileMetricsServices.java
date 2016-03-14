package br.edu.utfpr.recominer.services.metric;

import br.edu.utfpr.recominer.dao.BichoDAO;
import br.edu.utfpr.recominer.dao.BichoFileDAO;
import br.edu.utfpr.recominer.dao.BichoPairFileDAO;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.metric.committer.Committer;
import br.edu.utfpr.recominer.metric.committer.CommitterFileMetrics;
import br.edu.utfpr.recominer.metric.committer.CummulativeCommitterFileMetricsCalculator;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import br.edu.utfpr.recominer.metric.network.NetworkMetricsCalculator;
import br.edu.utfpr.recominer.model.CodeChurn;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.CommitMetrics;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.ContextualMetrics;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.Issue;
import br.edu.utfpr.recominer.model.IssueCommitFile;
import br.edu.utfpr.recominer.model.IssueMetrics;
import br.edu.utfpr.recominer.model.matrix.EntityMatrix;
import br.edu.utfpr.recominer.model.matrix.EntityMatrixNode;
import br.edu.utfpr.recominer.model.metric.EntityMetric;
import br.edu.utfpr.recominer.services.matrix.BichoPairOfFileAprioriServices;
import static br.edu.utfpr.recominer.services.metric.AbstractBichoMetricServices.objectsToNodes;
import br.edu.utfpr.recominer.services.util.MatrixUtils;
import br.edu.utfpr.recominer.util.OutLog;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;
import static br.edu.utfpr.recominer.services.metric.AbstractBichoMetricServices.objectsToNodes;
import static br.edu.utfpr.recominer.services.metric.AbstractBichoMetricServices.objectsToNodes;
import static br.edu.utfpr.recominer.services.metric.AbstractBichoMetricServices.objectsToNodes;
import static br.edu.utfpr.recominer.services.metric.AbstractBichoMetricServices.objectsToNodes;
import static br.edu.utfpr.recominer.services.metric.AbstractBichoMetricServices.objectsToNodes;
import static br.edu.utfpr.recominer.services.metric.AbstractBichoMetricServices.objectsToNodes;
import static br.edu.utfpr.recominer.services.metric.AbstractBichoMetricServices.objectsToNodes;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class BichoPairFileMetricsServices extends AbstractBichoMetricServices {

    private String repository;

    public BichoPairFileMetricsServices() {
        super();
    }

    public BichoPairFileMetricsServices(GenericBichoDAO dao, GenericDao genericDao, EntityMatrix matrix, Map<Object, Object> params, OutLog out) {
        super(dao, genericDao, matrix, params, out);
    }

    private Integer getIntervalOfMonths() {
        return getIntegerParam("intervalOfMonths");
    }

    public String getAdditionalFilename() {
        return getStringParam("additionalFilename");
    }

    @Override
    public void run() {
        repository = getRepository();

        out.printLog("Iniciado calculo da metrica de matriz com " + getMatrix().getNodes().size() + " nodes. Parametros: " + params);

        final int maxFilePerCommit = 20;
        final int numberOfLastIssues = 500;
        final BichoDAO bichoDAO = new BichoDAO(dao, repository, maxFilePerCommit);
        final BichoFileDAO bichoFileDAO = new BichoFileDAO(dao, repository, maxFilePerCommit, numberOfLastIssues);
        final BichoPairFileDAO bichoPairFileDAO = new BichoPairFileDAO(dao, repository, maxFilePerCommit);

        final Map<String, Integer> headerIndexesMap = MatrixUtils.extractHeaderIndexes(matrix);
        final List<EntityMatrixNode> matrixNodes = MatrixUtils.extractValues(matrix);

        final Cacher cacher = new Cacher(bichoFileDAO, bichoPairFileDAO, bichoDAO);

        final Set<FilePair> filePairs = getFilePairsFromMatrix(matrix.getNodes().get(0), matrixNodes, headerIndexesMap);

        final Map<Committer, Map<IssueCommitFile, IssueCommitFile>> cummulativeCommitters = new HashMap<>();

        final Map<String, Set<Commit>> fileCommits = new LinkedHashMap<>();
        final Map<String, Set<Committer>> fileCommitters = new LinkedHashMap<>();

        CummulativeCommitterFileMetricsCalculator committerFileMetricsCalculator = new CummulativeCommitterFileMetricsCalculator(bichoFileDAO);

//        final Set<Committer> majorContributorsInPreviousVersion = new HashSet<>();
//        final Map<String, Double> ownerExperience = new HashMap<>(filePairs.size());
//        final Map<Committer, CommitterFileMetrics> committerFileMetricsList = new HashMap<>();

//        for (FilePair filePair : filePairs) {
//            final String filename = filePair.getFile1();

//            final Set<Committer> fileCommittersInPreviousVersion = bichoFileDAO.selectCommitters(filename);

//            for (Committer committer : fileCommittersInPreviousVersion) {
//                CommitterFileMetrics committerFileMetrics;
//                if (pastMajorVersion != null) {
//                    committerFileMetrics
//                            = committerFileMetricsCalculator.calculeForVersion(filename, committer);
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

        int rank = 1;
        int progressFilePair = 0;
        int totalFilePair = filePairs.size();
//        final Set<FileIssueMetrics> allFileChanges = new LinkedHashSet<>();
        for (FilePair filePair : filePairs) {
            if (++progressFilePair % 100 == 0 || progressFilePair == totalFilePair) {
                System.out.println(progressFilePair + "/" + totalFilePair);
            }
            final Set<ContextualMetrics> allFileChanges = new LinkedHashSet<>();

            // par analisado
            final String filename = filePair.getFile1().getFileName(); // arquivo principal
            final String filename2 = filePair.getFile2().getFileName();
            final File file = filePair.getFile1();
            final File file2 = filePair.getFile2();

            final List<Integer> issueWhereFileChanged = cacher.selectIssues(filename);
            int progress = 0, totalProgress = issueWhereFileChanged.size();

            for (Integer issue : issueWhereFileChanged) {

                if (progress++ % 100 == 0 || progress == totalProgress) {
                    System.out.println("Rank " + rank + " - " + progress + "/" + totalProgress);
                }

                System.out.println("Calculing issue metrics");
                final IssueMetrics issueMetrics = cacher.calculeIssueMetrics(issue);

                System.out.println("Selecting commits of issue");
                final Set<Commit> issueCommits = cacher.selectFilesAndCommitByIssue(issue);

                for (Commit commitInIssue : issueCommits) {
                    final Set<File> filesInCommit = commitInIssue.getFiles();
                    final Committer committer = commitInIssue.getCommitter();

                    // metricas do arquivo com maior confiança, somente
                    final ContextualMetrics fileIssueMetrics = new ContextualMetrics(filename, filename2, commitInIssue, issueMetrics);

                    if (!commitInIssue.getFiles().contains(file)) {
                        continue;
                    }

                    final Set<Commit> commits;
                    if (fileCommits.containsKey(filename)) {
                        commits = fileCommits.get(filename);
                    } else {
                        commits = new HashSet<>();
                        fileCommits.put(filename, commits);
                    }
                    commits.add(commitInIssue);

                    final Set<Committer> committers;
                    if (fileCommitters.containsKey(filename)) {
                        committers = fileCommitters.get(filename);
                    } else {
                        committers = new HashSet<>();
                        fileCommitters.put(filename, committers);
                    }
                    committers.add(committer);

                    // TODO metrics do commit
                    final CommitMetrics commitMetrics = new CommitMetrics(commitInIssue);

                    System.out.println("Calculing committer metrics");
                    final CommitterFileMetrics committerFileMetrics = committerFileMetricsCalculator.calculeForCommit(filename, committer, commitInIssue);
//                    committerFileMetricsList.put(committer, committerFileMetrics);
//                    if (committerFileMetrics.getOwnership() > 0.05) { // maior que 5% = major
//                        majorContributorsInPreviousVersion.add(committer);
//                    }
//
//                    if (ownerExperience.containsKey(filename)) {
//                        ownerExperience.put(filename, Math.max(committerFileMetrics.getExperience(), ownerExperience.get(filename)));
//                    } else {
//                        ownerExperience.put(filename, committerFileMetrics.getExperience());
//                    }

//                    if (committerFileMetricsList.containsKey(commitInIssue.getCommitter())) {
//                        // committer already commits the file
//                        committerFileMetrics = committerFileMetricsList.get(commitInIssue.getCommitter());
//                    } else {
//                        // committer does not commit the file yet (i.e. first commit of committer)
//                        committerFileMetrics = new EmptyCommitterFileMetrics();
//                    }
                    final Map<IssueCommitFile, IssueCommitFile> commiterIssueCommitFileSet;
                    if (cummulativeCommitters.containsKey(commitInIssue.getCommitter())) {
                        commiterIssueCommitFileSet = cummulativeCommitters.get(commitInIssue.getCommitter());
                    } else {
                        commiterIssueCommitFileSet = new LinkedHashMap<>();
                        cummulativeCommitters.put(commitInIssue.getCommitter(), commiterIssueCommitFileSet);
                    }

                    IssueCommitFile commiterIssueCommitFile = new IssueCommitFile(new Issue(issue, null), commitInIssue, filename);

                    if (commiterIssueCommitFileSet.containsKey(commiterIssueCommitFile)) {
                        commiterIssueCommitFile = commiterIssueCommitFileSet.get(commiterIssueCommitFile);
                        commiterIssueCommitFile.add();
                    } else {
                        commiterIssueCommitFileSet.put(commiterIssueCommitFile, commiterIssueCommitFile);
                    }

                    fileIssueMetrics.setCommitMetrics(commitMetrics);
                    fileIssueMetrics.setCommitterFileMetrics(committerFileMetrics);

                    if (filesInCommit.contains(file2)) { // se houve commit do arquivo 2, entao o par mudou
                        // muda a coluna changed para 1, indicando que o par analisado mudou nessa issue
                        fileIssueMetrics.changed();
                    }

                    // calculo das metricas de commit apenas para o primeiro arquivo
                    if (!allFileChanges.contains(fileIssueMetrics)) {
                        // pair file network
                        final NetworkMetrics networkMetrics
                                = new NetworkMetricsCalculator(issue, bichoDAO).getNetworkMetrics();

                        fileIssueMetrics.setNetworkMetrics(networkMetrics);

                        final long totalCommitters = fileCommitters.get(filename).size();// = bichoFileDAO.calculeCummulativeCommitters(filename, issue); // TODO otimizar: contar todos do passado e ir somando
                                
                        final long totalCommits = fileCommits.get(filename).size();// = bichoFileDAO.calculeCummulativeCommits(filename);

//                        final Map<String, Long> futureIssuesTypes
//                                = bichoFileDAO.calculeNumberOfIssuesGroupedByType(filename);
//                        final long futureDefects;
//                        if (!futureIssuesTypes.containsKey("Bug")) {
//                            futureDefects = 0;
//                        } else {
//                            futureDefects = futureIssuesTypes.get("Bug");
//                        }
//
//                        long futureIssues = 0;
//                        for (Map.Entry<String, Long> entrySet : futureIssuesTypes.entrySet()) {
//                            futureIssues += entrySet.getValue();
//                        }

                        final Committer lastCommitter = bichoFileDAO.selectLastCommitter(file.getFileName(), commitInIssue);
                        final boolean sameOwnership = commitInIssue.getCommitter().equals(lastCommitter);

                        System.out.println("Calculing code churn");
                        final CodeChurn fileCodeChurn = bichoFileDAO.calculeAddDelChanges(filename, issue, commitInIssue.getId());

                        System.out.println("Calculing file age");
                        // pair file age in release interval (days)
                        final int ageRelease = bichoFileDAO.calculeFileAgeInDays(filename, issue);

                        System.out.println("Calculing file total age");
                        // pair file cummulative age: from first commit until previous (past) release
                        final int ageTotal = bichoFileDAO.calculeTotalFileAgeInDays(filename, issue);

                        fileIssueMetrics.addMetrics(
                                // majorContributors
                                BooleanUtils.toInteger(committerFileMetrics.getOwnership() > 0.05), //BooleanUtils.toInteger(majorContributorsInPreviousVersion.contains(commitInIssue.getCommitter())),
                                // ownerExperience,
                                committerFileMetrics.getExperience(), //ownerExperience.get(filename),
                                // sameOwnership
                                BooleanUtils.toInteger(sameOwnership),
                                // committers, totalCommitters, commits, totalCommits,
                                totalCommitters, totalCommits,
                                // pairFileCodeChurn.getAdditionsNormalized(), pairFileCodeChurn.getDeletionsNormalized(), pairFileCodeChurn.getChanges()
                                fileCodeChurn.getAdditionsNormalized(), fileCodeChurn.getDeletionsNormalized(), fileCodeChurn.getChanges(),
                                // ageRelease, ageTotal
                                ageRelease, ageTotal//,
                                // futureDefects, futureIssues
//                                futureDefects, futureIssues
                        );

                        allFileChanges.add(fileIssueMetrics);
                    }
                }
            }

            // one file per file pair
            EntityMetric metrics = new EntityMetric();
                    metrics.setNodes(objectsToNodes(allFileChanges, ContextualMetrics.HEADER));
                    metrics.getParams().put("rank", rank++);
                    metrics.getParams().put("additionalFilename", getAdditionalFilename());
                    saveMetrics(metrics, getClass());
        }

        // one file for all file pair
//        EntityMetric metrics = new EntityMetric();
//        metrics.setNodes(objectsToNodes(allFileChanges, ContextualMetrics.HEADER));
//        metrics.getParams().put("rank", rank++);
//        metrics.getParams().put("additionalFilename", getAdditionalFilename());
//        saveMetrics(metrics, getClass());
    }

    @Override
    public List<String> getAvailableMatricesPermitted() {
        return Arrays.asList(BichoPairOfFileAprioriServices.class.getName());
    }

    private String getRepository() {
        return getMatrix().getRepository();
    }
}
