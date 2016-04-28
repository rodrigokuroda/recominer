package br.edu.utfpr.recominer.services.metric;

import br.edu.utfpr.recominer.dao.BichoDAO;
import br.edu.utfpr.recominer.dao.BichoFileDAO;
import br.edu.utfpr.recominer.dao.BichoPairFileDAO;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.metric.committer.Committer;
import br.edu.utfpr.recominer.metric.committer.CommitterFileMetrics;
import br.edu.utfpr.recominer.metric.committer.CommitterFileMetricsCalculator;
import br.edu.utfpr.recominer.metric.committer.EmptyCommitterFileMetrics;
import br.edu.utfpr.recominer.metric.network.CommunicationNetworkMetricsCalculator;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import br.edu.utfpr.recominer.model.CodeChurn;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.CommitMetrics;
import br.edu.utfpr.recominer.model.ContextualMetrics;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.Issue;
import br.edu.utfpr.recominer.model.IssueMetrics;
import br.edu.utfpr.recominer.model.matrix.EntityMatrix;
import br.edu.utfpr.recominer.model.matrix.EntityMatrixNode;
import br.edu.utfpr.recominer.model.metric.EntityMetric;
import br.edu.utfpr.recominer.services.matrix.BichoPairOfFileInFixVersionServices;
import br.edu.utfpr.recominer.services.matrix.BichoProjectsFilePairReleaseOccurenceServices;
import static br.edu.utfpr.recominer.services.metric.AbstractBichoMetricServices.objectsToNodes;
import br.edu.utfpr.recominer.services.util.MatrixUtils;
import br.edu.utfpr.recominer.util.OutLog;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class BichoPairFileMetricsInFixVersionServices extends AbstractBichoMetricServices {

    private String repository;

    public BichoPairFileMetricsInFixVersionServices() {
        super();
    }

    public BichoPairFileMetricsInFixVersionServices(GenericBichoDAO dao, GenericDao genericDao, EntityMatrix matrix, Map<Object, Object> params, OutLog out) {
        super(dao, genericDao, matrix, params, out);
    }

    private Integer getIntervalOfMonths() {
        return getIntegerParam("intervalOfMonths");
    }

    private String getVersion() {
        return getStringParam("version");
    }

    public String getFutureVersion() {
        return getStringParam("futureVersion");
    }

    public String getAdditionalFilename() {
        return getStringParam("additionalFilename");
    }

    @Override
    public void run() {
        repository = getRepository();
        final String fixVersion = getVersion();
        final String futureVersion;

        out.printLog("Iniciado cálculo da métrica de matriz com " + getMatrix().getNodes().size() + " nodes. Parametros: " + params);

        final int maxFilePerCommit = 20;
        final BichoDAO bichoDAO = new BichoDAO(dao, repository, maxFilePerCommit);
        final BichoFileDAO bichoFileDAO = new BichoFileDAO(dao, repository, maxFilePerCommit);
        final BichoPairFileDAO bichoPairFileDAO = new BichoPairFileDAO(dao, repository, maxFilePerCommit);

        final String pastMajorVersion = bichoDAO.selectPastMajorVersion(fixVersion);
        if (getFutureVersion() != null) {
            futureVersion = getFutureVersion();
        } else {
            futureVersion = bichoDAO.selectFutureMajorVersion(fixVersion);;
        }

        final Map<String, Integer> headerIndexesMap = MatrixUtils.extractHeaderIndexes(matrix);
        final List<EntityMatrixNode> matrixNodes = MatrixUtils.extractValues(matrix);

        final Cacher cacher = new Cacher(bichoFileDAO, bichoPairFileDAO);

        final Set<FilePair> filePairs = getFilePairsFromMatrix(matrix.getNodes().get(0), matrixNodes, headerIndexesMap);

        CommitterFileMetricsCalculator committerFileMetricsCalculator = new CommitterFileMetricsCalculator(null);

        final Set<Committer> majorContributorsInPreviousVersion = new HashSet<>();
        final Map<String, Double> ownerExperience = new HashMap<>(filePairs.size());
        final Map<Committer, CommitterFileMetrics> committerFileMetricsList = new HashMap<>();

        for (FilePair filePair : filePairs) {
            final String filename = filePair.getFile1().getFileName();

            final Set<Committer> fileCommittersInPreviousVersion
                    = bichoFileDAO.selectCommitters(filename, fixVersion);

            for (Committer committer : fileCommittersInPreviousVersion) {
                CommitterFileMetrics committerFileMetrics;
                if (pastMajorVersion != null) {
                    committerFileMetrics
                            = committerFileMetricsCalculator.calculeForVersion(
                                    null, committer);
                } else {
                    committerFileMetrics = new EmptyCommitterFileMetrics();
                }
                committerFileMetricsList.put(committer, committerFileMetrics);
                if (committerFileMetrics.getOwnership() > 0.05) { // maior que 5% = major
                    majorContributorsInPreviousVersion.add(committer);
                }

                if (ownerExperience.containsKey(filename)) {
                    ownerExperience.put(filename, Math.max(committerFileMetrics.getExperience(), ownerExperience.get(filename)));
                } else {
                    ownerExperience.put(filename, committerFileMetrics.getExperience());
                }
            }
        }

        int rank = 1;
        for (FilePair filePair : filePairs) {
            final Set<ContextualMetrics> allFileChanges = new LinkedHashSet<>();

            // par analisado
            final File file = filePair.getFile1();
            final File file2 = filePair.getFile2();
            final String filename = file.getFileName(); // arquivo principal
            final String filename2 = file2.getFileName();


            final List<Integer> issueWhereFileChanged = bichoFileDAO.selectIssues(filename, fixVersion);
            int progress = 0, totalProgress = issueWhereFileChanged.size();

            for (Integer issue : issueWhereFileChanged) {

                if (progress++ % 100 == 0 || progress == totalProgress) {
                    System.out.println("Rank " + rank + " - " + progress + "/" + totalProgress);
                }

                final IssueMetrics issueMetrics = cacher.calculeIssueMetrics(issue);

                final Set<Commit> issueCommits = bichoDAO.selectFilesAndCommitByIssue(issue);

                for (Commit commitInIssue : issueCommits) {
                    Set<File> filesInCommit = commitInIssue.getFiles();

                    // metricas do arquivo com maior confiança, somente
                    final ContextualMetrics fileIssueMetrics = new ContextualMetrics(filename, filename2, commitInIssue, issueMetrics);

                    if (!commitInIssue.getFiles().contains(file)) {
                        continue;
                    }

                    // TODO metrics do commit
                    final CommitMetrics commitMetrics = new CommitMetrics(commitInIssue);

                    final CommitterFileMetrics committerFileMetrics;

                    if (committerFileMetricsList.containsKey(commitInIssue.getCommitter())) {
                        // committer already commits the file
                        committerFileMetrics = committerFileMetricsList.get(commitInIssue.getCommitter());
                    } else {
                        // committer does not commit the file yet (i.e. first commit of committer)
                        committerFileMetrics = new EmptyCommitterFileMetrics();
                    }

                    fileIssueMetrics.setCommitMetrics(commitMetrics);
                    fileIssueMetrics.setCommitterFileMetrics(committerFileMetrics);

                    if (filesInCommit.contains(file2)) { // se houve commit do arquivo 2, então o par mudou
                        // muda a coluna changed para 1, indicando que o par analisado mudou nessa issue
                        fileIssueMetrics.changed();
                    }

                    // calculo das metricas de commit apenas para o primeiro arquivo
                    if (!allFileChanges.contains(fileIssueMetrics)) {
                        // pair file network
                        final NetworkMetrics networkMetrics
                                = new CommunicationNetworkMetricsCalculator(null).calcule(new Issue(issue));

                        fileIssueMetrics.setNetworkMetrics(networkMetrics);

                        final long totalCommitters = bichoFileDAO.calculeCummulativeCommitters(filename, issue, fixVersion);
                        final long totalCommits = bichoFileDAO.calculeCummulativeCommits(filename, fixVersion);

                        final Map<String, Long> futureIssuesTypes
                                = bichoFileDAO.calculeNumberOfIssuesGroupedByType(
                                        filename, futureVersion);
                        final long futureDefects;
                        if (!futureIssuesTypes.containsKey("Bug")) {
                            futureDefects = 0;
                        } else {
                            futureDefects = futureIssuesTypes.get("Bug");
                        }

                        long futureIssues = 0;
                        for (Map.Entry<String, Long> entrySet : futureIssuesTypes.entrySet()) {
                            futureIssues += entrySet.getValue();
                        }

                        final Committer lastCommitter = bichoFileDAO.selectLastCommitter(file.getFileName(), commitInIssue, fixVersion);
                        final boolean sameOwnership = commitInIssue.getCommitter().equals(lastCommitter);

                        final CodeChurn fileCodeChurn = bichoFileDAO.calculeAddDelChanges(filename, issue, commitInIssue.getId(), fixVersion);

                        // pair file age in release interval (days)
                        final int ageRelease = bichoFileDAO.calculeFileAgeInDays(filename, issue, fixVersion);

                        // pair file cummulative age: from first commit until previous (past) release
                        final int ageTotal = bichoFileDAO.calculeTotalFileAgeInDays(filename, issue, fixVersion);

//                        fileIssueMetrics.addMetrics(
//                                // majorContributors
//                                BooleanUtils.toInteger(majorContributorsInPreviousVersion.contains(commitInIssue.getCommitter())),
//                                // ownerExperience,
//                                ownerExperience.get(filename),
//                                // sameOwnership
//                                BooleanUtils.toInteger(sameOwnership),
//                                // committers, totalCommitters, commits, totalCommits,
//                                totalCommitters, totalCommits,
//                                // pairFileCodeChurn.getAdditionsNormalized(), pairFileCodeChurn.getDeletionsNormalized(), pairFileCodeChurn.getChanges()
//                                fileCodeChurn.getAdditionsNormalized(), fileCodeChurn.getDeletionsNormalized(), fileCodeChurn.getChanges(),
//                                // ageRelease, ageTotal
//                                ageRelease, ageTotal,
//                                // futureDefects, futureIssues
//                                futureDefects, futureIssues
//                        );

                        if (fileIssueMetrics.getChangedAfterReopened() == 1) {
                            System.out.println("changedAfterReopened = " + getRepository() + " " + getVersion() + " rank " + rank + " (" + matrix.toString() + ")");
                            out.printLog("changedAfterReopened = " + getRepository() + " " + getVersion() + " rank " + rank + " (" + matrix.toString() + ")");
                        }
                        allFileChanges.add(fileIssueMetrics);
                    }
                }
            }

            EntityMetric metrics = new EntityMetric();
            metrics.setNodes(objectsToNodes(allFileChanges, ContextualMetrics.HEADER));
            metrics.getParams().put("rank", rank++);
            metrics.getParams().put("additionalFilename", getAdditionalFilename());
            saveMetrics(metrics, getClass());
        }
    }

    public String getHeadCSV() {
        return "file;file2;issue;"
                + "issueType;issuePriority;issueAssignedTo;issueSubmittedBy;"
                + "issueWatchers;issueReopened;"
                + "samePackage;sameOwnership;" // arquivos do par são do mesmo pacote = 1, caso contrário 0
                // + "brcAvg;brcSum;brcMax;"
                + "btwSum;btwAvg;btwMdn;btwMax;"
                + "clsSum;clsAvg;clsMdn;clsMax;"
                + "dgrSum;dgrAvg;dgrMdn;dgrMax;"
                //+ "egvSum;egvAvg;egvMax;"
                + "egoBtwSum;egoBtwAvg;egoBtwMdn;egoBtwMax;"
                + "egoSizeSum;egoSizeAvg;egoSizeMdn;egoSizeMax;"
                + "egoTiesSum;egoTiesAvg;egoTiesMdn;egoTiesMax;"
                // + "egoPairsSum;egoPairsAvg;egoPairsMax;"
                + "egoDensitySum;egoDensityAvg;egoDensityMdn;egoDensityMax;"
                + "efficiencySum;efficiencyAvg;efficiencyMdn;efficiencyMax;"
                + "efvSizeSum;efvSizeAvg;efvSizeMdn;efvSizeMax;"
                + "constraintSum;constraintAvg;constraintMdn;constraintMax;"
                + "hierarchySum;hierarchyAvg;hierarchyMdn;hierarchyMax;"
                + "size;ties;density;diameter;"
                + "devCommitsSum;devCommitsAvg;devCommitsMdn;devCommitsMax;"
                + "ownershipSum;ownershipAvg;ownershipMdn;ownershipMax;"
                + "majorContributors;minorContributors;"
                + "oexp;oexp2;"
                + "own;own2;"
                + "numFiles;"
                + "committers;" // committers na release
                + "totalCommitters;" // ddev, committers desde o começo até a data final da relese
                + "commits;" // commits do par de arquivos na release
                + "totalCommits;" // todos commits do par de arquivos
                + "devCommenters;" // número de autores de comentários que são desenvolvedores
                + "commenters;comments;wordiness;"
                + "codeChurn;codeChurn2;codeChurnAvg;"
                + "add;del;changes;"
                // + "rigidityFile1;rigidityFile2;rigidityPairFile;"
                // + "taskImprovement;taskDefect;"
                + "ageRelease;ageTotal;"
                + "updates;futureUpdates;futureDefects;"
                + "fileIssues;file2Issues;allIssues;"
                + "supportFile;supportFile2;supportPairFile;confidence;confidence2;lift;conviction;conviction2;changed";
    }

    @Override
    public List<String> getAvailableMatricesPermitted() {
        return Arrays.asList(BichoPairOfFileInFixVersionServices.class.getName(),
                BichoProjectsFilePairReleaseOccurenceServices.class.getName());
    }

    private String getRepository() {
        return getMatrix().getRepository();
    }
}
