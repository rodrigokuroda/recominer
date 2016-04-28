package br.edu.utfpr.recominer.services.metric;

import br.edu.utfpr.recominer.dao.BichoDAO;
import br.edu.utfpr.recominer.dao.BichoFileDAO;
import br.edu.utfpr.recominer.dao.BichoPairFileDAO;
import br.edu.utfpr.recominer.metric.committer.Committer;
import br.edu.utfpr.recominer.metric.network.CommunicationNetworkMetricsCalculator;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import br.edu.utfpr.recominer.model.CodeChurn;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.FilePairIssue;
import br.edu.utfpr.recominer.model.Issue;
import br.edu.utfpr.recominer.model.IssueMetrics;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class Cacher {

    // cache for optimization number of pull requests where file is in,
    // reducing access to database
    private final Map<String, Long> issueFileMap = new HashMap<>();
    private final Map<String, Long> lastIssueFileMap = new HashMap<>();

    // cache for optimization file code churn (add, del, change),
    // reducing access to database
    private final Map<String, CodeChurn> fileCodeChurnMap = new HashMap<>();
    private final Map<String, CodeChurn> fileCodeChurnByIssueMap = new HashMap<>();
    // cummulative (i.e. until a date)
    private final Map<String, CodeChurn> cummulativeCodeChurnRequestFileMap = new HashMap<>();

    // cache for optimization file commits made by user,
    // reducing access to database
    private final Map<String, CodeChurn> fileUserCodeChurnMap = new HashMap<>();
    private final Map<String, CodeChurn> fileUserCummulativeCodeChurnMap = new HashMap<>();

    // future issues
    private final Map<FilePair, Long> futureIssuesMap = new HashMap<>();
    private final Map<FilePair, Long> totalCommittersMap = new HashMap<>();
    private final Map<FilePair, Long> totalPastCommittersMap = new HashMap<>();
    private final Map<FilePairIssue, Long> totalCommittersUntiIssueFixDateMap = new HashMap<>();
    private final Map<FilePair, Long> totalCommitsMap = new HashMap<>();
    private final Map<FilePair, Long> totalPastCommitsMap = new HashMap<>();
    private final Map<FilePairIssue, Long> totalCommitsUntiIssueFixDateMap = new HashMap<>();
    private final Map<FilePair, Map<String, Long>> futureIssueTypessMap = new HashMap<>();

    private final Map<Integer, IssueMetrics> issuesCommentsCacher = new HashMap<>();
    private final Map<Integer, Long> issuesReopenedCountCacher = new HashMap<>();

    private final Map<Integer, NetworkMetrics> networkMetricsMap = new HashMap<>();

    private final Map<FilePairIssue, CodeChurn> cummulativeCodeChurnMap = new HashMap<>();

    private final Map<String, Set<Committer>> fileCommitters = new HashMap<>();
    private final Map<String, List<Integer>> filesIssuesCacher = new HashMap<>();
    private final Map<Integer, Set<Commit>> issuesCommitsCacher = new HashMap<>();

    private final BichoDAO dao;
    private final BichoFileDAO fileDAO;
    private final BichoPairFileDAO pairFileDAO;

    public Cacher(BichoFileDAO fileDAO) {
        this.fileDAO = fileDAO;
        this.pairFileDAO = null;
        this.dao = null;
    }

    public Cacher(BichoFileDAO fileDAO, BichoPairFileDAO pairFileDAO) {
        this.fileDAO = fileDAO;
        this.pairFileDAO = pairFileDAO;
        this.dao = null;
    }

    public Cacher(BichoFileDAO fileDAO, BichoPairFileDAO pairFileDAO, BichoDAO dao) {
        this.fileDAO = fileDAO;
        this.pairFileDAO = pairFileDAO;
        this.dao = dao;
    }

    // Internal method with Map (cacher) parameter
    private CodeChurn calculeFileCodeChurn(Map<String, CodeChurn> codeChurnCacher,
            String fileName, Date beginDate, Date endDate, Collection<Integer> issues) {
        if (codeChurnCacher.containsKey(fileName)) {
            return codeChurnCacher.get(fileName);
        } else {
            CodeChurn sumCodeChurnFile = fileDAO.sumCodeChurnByFilename(fileName, beginDate, endDate, issues);
            codeChurnCacher.put(fileName, sumCodeChurnFile);
            return sumCodeChurnFile;
        }
    }

    // Internal method with Map (cacher) parameter
    private CodeChurn calculeFileCodeChurn(Map<String, CodeChurn> codeChurnCacher,
            String fileName, String fixVersion, Collection<Integer> issues) {
        if (codeChurnCacher.containsKey(fileName)) {
            return codeChurnCacher.get(fileName);
        } else {
            CodeChurn sumCodeChurnFile = fileDAO.sumCodeChurnByFilename(fileName, fixVersion, issues);
            codeChurnCacher.put(fileName, sumCodeChurnFile);
            return sumCodeChurnFile;
        }
    }

    public Long calculeNumberOfIssues(File file, Date futureBeginDate, Date futureEndDate) {
        return calculeNumberOfIssues(file.getFileName(), futureBeginDate, futureEndDate);
    }

    public Long calculeNumberOfIssues(String fileName, Date futureBeginDate, Date futureEndDate) {
        Long fileNumberOfPullrequestOfPairFuture;
        if (issueFileMap.containsKey(fileName)) {
            fileNumberOfPullrequestOfPairFuture = issueFileMap.get(fileName);
        } else {
            fileNumberOfPullrequestOfPairFuture = fileDAO.calculeNumberOfIssues(fileName, futureBeginDate, futureEndDate);
            issueFileMap.put(fileName, fileNumberOfPullrequestOfPairFuture);
        }
        return fileNumberOfPullrequestOfPairFuture;
    }

    public Long calculeNumberOfIssues(File file, String fixVersion) {
        return calculeNumberOfIssues(file.getFileName(), fixVersion);
    }

    public Long calculeNumberOfIssues(String fileName, String fixVersion) {
        Long fileNumberOfPullrequestOfPairFuture;
        if (issueFileMap.containsKey(fileName)) {
            fileNumberOfPullrequestOfPairFuture = issueFileMap.get(fileName);
        } else {
            fileNumberOfPullrequestOfPairFuture = fileDAO.calculeNumberOfIssues(fileName, fixVersion);
            issueFileMap.put(fileName, fileNumberOfPullrequestOfPairFuture);
        }
        return fileNumberOfPullrequestOfPairFuture;
    }

    public Long calculeNumberOfIssues(File file) {
        return calculeNumberOfIssues(file.getFileName());
    }

    public Long calculeNumberOfIssues(String fileName) {
        Long fileNumberOfPullrequestOfPairFuture;
        if (issueFileMap.containsKey(fileName)) {
            fileNumberOfPullrequestOfPairFuture = issueFileMap.get(fileName);
        } else {
            fileNumberOfPullrequestOfPairFuture = fileDAO.calculeNumberOfIssues(fileName);
            issueFileMap.put(fileName, fileNumberOfPullrequestOfPairFuture);
        }
        return fileNumberOfPullrequestOfPairFuture;
    }

    public Long calculeNumberOfIssues(File file, Set<Issue> issues) {
        return calculeNumberOfIssues(file.getFileName(), issues);
    }

    public Long calculeNumberOfIssues(String fileName, Set<Issue> issues) {
        Long fileNumberOfPullrequestOfPairFuture;
        if (issueFileMap.containsKey(fileName)) {
            fileNumberOfPullrequestOfPairFuture = issueFileMap.get(fileName);
        } else {
            fileNumberOfPullrequestOfPairFuture = fileDAO.calculeNumberOfIssues(fileName, issues);
            issueFileMap.put(fileName, fileNumberOfPullrequestOfPairFuture);
        }
        return fileNumberOfPullrequestOfPairFuture;
    }

    public Long calculeNumberOfIssuesInLastIssues(File file, int numberOfLastIssues) {
        return calculeNumberOfIssuesInLastIssues(file.getFileName(), numberOfLastIssues);
    }

    public Long calculeNumberOfIssuesInLastIssues(String fileName, int numberOfLastIssues) {
        Long countIssues;
        if (lastIssueFileMap.containsKey(fileName)) {
            countIssues = lastIssueFileMap.get(fileName);
        } else {
            countIssues = fileDAO.calculeNumberOfIssues(fileName);
            lastIssueFileMap.put(fileName, countIssues);
        }
        return countIssues;
    }

    public double calculeDevFileExperience(final Long changes, String fileName, String user, Date beginDate, Date endDate, Collection<Integer> issues) {
        final long devChanges;
        if (fileUserCodeChurnMap.containsKey(fileName)) {
            CodeChurn sumCodeChurnFile = fileUserCodeChurnMap.get(fileName);
            devChanges = sumCodeChurnFile.getChanges();
        } else {
            CodeChurn sumCodeChurnFile = fileDAO.sumCodeChurnByFilename(fileName, user, beginDate, endDate, issues);
            fileUserCodeChurnMap.put(fileName, sumCodeChurnFile);
            devChanges = sumCodeChurnFile.getChanges();
        }
        return changes == 0 ? 0.0 : (double) devChanges / (double) changes;
    }

    public double calculeCummulativeDevFileExperience(final Long changes, String fileName, String user, Date endDate, Collection<Integer> issues) {
        final long devChanges;
        if (fileUserCummulativeCodeChurnMap.containsKey(fileName)) {
            CodeChurn sumCodeChurnFile = fileUserCummulativeCodeChurnMap.get(fileName);
            devChanges = sumCodeChurnFile.getChanges();
        } else {
            CodeChurn sumCodeChurnFile = fileDAO.sumCodeChurnByFilename(fileName, user, null, endDate, issues);
            fileUserCummulativeCodeChurnMap.put(fileName, sumCodeChurnFile);
            devChanges = sumCodeChurnFile.getChanges();
        }
        return changes == 0 ? 0.0 : (double) devChanges / (double) changes;
    }

    public double calculeCummulativeDevFileExperience(final Long changes, String fileName, String user, String fixVersion, Collection<Integer> issues) {
        final long devChanges;
        if (fileUserCummulativeCodeChurnMap.containsKey(fileName)) {
            CodeChurn sumCodeChurnFile = fileUserCummulativeCodeChurnMap.get(fileName);
            devChanges = sumCodeChurnFile.getChanges();
        } else {
            CodeChurn sumCodeChurnFile = fileDAO.sumCummulativeCodeChurnByFilename(fileName, user, fixVersion, issues);
            fileUserCummulativeCodeChurnMap.put(fileName, sumCodeChurnFile);
            devChanges = sumCodeChurnFile.getChanges();
        }
        return changes == 0 ? 0.0 : (double) devChanges / (double) changes;
    }

    public double calculeDevFileExperience(final Long changes, String fileName, String user, String fixVersion, Collection<Integer> issues) {
        final long devChanges;
        if (fileUserCodeChurnMap.containsKey(fileName)) {
            CodeChurn sumCodeChurnFile = fileUserCodeChurnMap.get(fileName);
            devChanges = sumCodeChurnFile.getChanges();
        } else {
            CodeChurn sumCodeChurnFile = fileDAO.sumCodeChurnByFilename(fileName, user, fixVersion, issues);
            fileUserCodeChurnMap.put(fileName, sumCodeChurnFile);
            devChanges = sumCodeChurnFile.getChanges();
        }
        return changes == 0 ? 0.0 : (double) devChanges / (double) changes;
    }

    public CodeChurn calculeFileCummulativeCodeChurn(String fileName, Date endDate, Set<Integer> issues) {
        return calculeFileCodeChurn(cummulativeCodeChurnRequestFileMap, fileName, null, endDate, issues);
    }

    public CodeChurn calculeFileCummulativeCodeChurn(String fileName, String fixVersion, Set<Integer> issues) {
        if (cummulativeCodeChurnRequestFileMap.containsKey(fileName)) {
            return cummulativeCodeChurnRequestFileMap.get(fileName);
        } else {
            CodeChurn sumCodeChurnFile = fileDAO.sumCummulativeCodeChurnByFilename(fileName, fixVersion, issues);
            cummulativeCodeChurnRequestFileMap.put(fileName, sumCodeChurnFile);
            return sumCodeChurnFile;
        }
    }

    public CodeChurn calculeFileCodeChurnByIssues(String fileName, Date beginDate, Date endDate, Set<Integer> issues) {
        return calculeFileCodeChurn(fileCodeChurnByIssueMap, fileName, beginDate, endDate, issues);
    }

    public CodeChurn calculeFileCodeChurnByIssues(String fileName, String fixVersion, Set<Integer> issues) {
        return calculeFileCodeChurn(fileCodeChurnByIssueMap, fileName, fixVersion, issues);
    }

    public CodeChurn calculeFileCodeChurn(String fileName, Date beginDate, Date endDate) {
        return calculeFileCodeChurn(fileCodeChurnMap, fileName, beginDate, endDate, null);
    }

    public CodeChurn calculeFileCodeChurn(String fileName, String fixVersion) {
        return calculeFileCodeChurn(fileCodeChurnMap, fileName, fixVersion, null);
    }

    public long calculeFutureNumberOfIssues(String file1, String file2, String futureVersion) {
        FilePair fileFifle = new FilePair(file1, file2);
        return calculeFutureNumberOfIssues(fileFifle, futureVersion);
    }

    public long calculeFutureNumberOfIssues(FilePair fileFile, String futureVersion) {
        long futureIssues;
        if (futureIssuesMap.containsKey(fileFile)) {
            futureIssues = futureIssuesMap.get(fileFile);
        } else {
            futureIssues = pairFileDAO.calculeNumberOfIssues(
                    fileFile.getFile1().getFileName(), fileFile.getFile2().getFileName(),
                    futureVersion);
            futureIssuesMap.put(fileFile, futureIssues);
        }
        return futureIssues;
    }

    public IssueMetrics calculeIssueMetrics(Issue issue) {
        return calculeIssueMetrics(issue.getId());
    }

    public IssueMetrics calculeIssueMetrics(Integer issue) {
        if (issuesCommentsCacher.containsKey(issue)) {
            return issuesCommentsCacher.get(issue);
        } else {
            IssueMetrics metric = pairFileDAO.listIssues(issue);
            issuesCommentsCacher.put(issue, metric);
            return metric;
        }
    }

    public NetworkMetrics calculeNetworkMetrics(Integer issue) {
        NetworkMetrics networkMetrics;
        if (networkMetricsMap.containsKey(issue)) {
            networkMetrics = networkMetricsMap.get(issue);
        } else {
            networkMetrics = new CommunicationNetworkMetricsCalculator(null).calcule(new Issue(issue));
            networkMetricsMap.put(issue, networkMetrics);
        }
        return networkMetrics;
    }

    public long calculeCummulativeCommitters(String file1, String file2, String fixVersion) {
        FilePair fileFile = new FilePair(file1, file2);
        long totalCommitters;
        if (totalCommittersMap.containsKey(fileFile)) {
            totalCommitters = totalCommittersMap.get(fileFile);
        } else {
            totalCommitters = pairFileDAO.calculeCummulativeCommitters(
                    fileFile.getFile1().getFileName(), fileFile.getFile2().getFileName(), fixVersion);
            totalCommittersMap.put(fileFile, totalCommitters);
        }
        return totalCommitters;
    }

    public long calculePastCommitters(FilePair fileFile, String fixVersion) {
        long totalCommitters;
        if (totalPastCommittersMap.containsKey(fileFile)) {
            totalCommitters = totalPastCommittersMap.get(fileFile);
        } else {
            totalCommitters = pairFileDAO.calculePastCommitters(
                    fileFile.getFile1().getFileName(), fileFile.getFile2().getFileName(), fixVersion);
            totalPastCommittersMap.put(fileFile, totalCommitters);
        }
        return totalCommitters;
    }

    public long calculeCummulativeCommitters(FilePairIssue fileFile, String fixVersion) {
        long totalCommitters;
        if (totalCommittersUntiIssueFixDateMap.containsKey(fileFile)) {
            totalCommitters = totalCommittersUntiIssueFixDateMap.get(fileFile);
        } else {
            totalCommitters = pairFileDAO.calculeCummulativeCommitters(
                    fileFile.getFile1(), fileFile.getFile2(), fileFile.getIssue(), fixVersion);
            totalCommittersUntiIssueFixDateMap.put(fileFile, totalCommitters);
        }
        return totalCommitters;
    }

    public Long calculeCummulativeCommits(FilePair fileFile, String fixVersion) {
        long totalCommits;
        if (totalCommitsMap.containsKey(fileFile)) {
            totalCommits = totalCommitsMap.get(fileFile);
        } else {
            totalCommits = pairFileDAO.calculeCommits(fileFile.getFile1().getFileName(), fileFile.getFile2().getFileName(),
                    fixVersion);
            totalCommitsMap.put(fileFile, totalCommits);
        }
        return totalCommits;
    }

    public Long calculePastCommits(FilePair fileFile, String fixVersion) {
        long totalCommits;
        if (totalPastCommitsMap.containsKey(fileFile)) {
            totalCommits = totalPastCommitsMap.get(fileFile);
        } else {
            totalCommits = pairFileDAO.calculePastCommitsByFixVersion(fileFile.getFile1().getFileName(), fileFile.getFile2().getFileName(),
                    fixVersion);
            totalPastCommitsMap.put(fileFile, totalCommits);
        }
        return totalCommits;
    }

    public Long calculeCummulativeCommits(FilePairIssue fileFile, String fixVersion) {
        long totalCommits;
        if (totalCommitsUntiIssueFixDateMap.containsKey(fileFile)) {
            totalCommits = totalCommitsUntiIssueFixDateMap.get(fileFile);
        } else {
            totalCommits = pairFileDAO.calculeCommits(fileFile.getFile1(), fileFile.getFile2(),
                    fileFile.getIssue(), fixVersion);
            totalCommitsUntiIssueFixDateMap.put(fileFile, totalCommits);
        }
        return totalCommits;
    }

    public Map<String, Long> calculeFutureNumberOfIssuesWithType(FilePair fileFile, String futureVersion) {
        final Map<String, Long> futureIssuesTypes;
        if (futureIssueTypessMap.containsKey(fileFile)) {
            futureIssuesTypes = futureIssueTypessMap.get(fileFile);
        } else {
            futureIssuesTypes = pairFileDAO.countIssuesTypes(fileFile.getFile1().getFileName(), fileFile.getFile2().getFileName(), futureVersion);
            futureIssueTypessMap.put(fileFile, futureIssuesTypes);
        }
        return futureIssuesTypes;
    }

    public long calculeIssueReopenedTimes(Integer issue) {
        final long issueReopened;
        if (issuesReopenedCountCacher.containsKey(issue)) {
            issueReopened = issuesReopenedCountCacher.get(issue);
        } else {
            issueReopened = fileDAO.calculeIssueReopenedTimes(issue);
            issuesReopenedCountCacher.put(issue, issueReopened);
        }
        return issueReopened;
    }

    public CodeChurn calculeCummulativeCodeChurnAddDelChange(String fileName, String fileName2, Integer issue, Set<Integer> allPairFileIssues, String fixVersion) {
        final CodeChurn codeChurn;
        final FilePairIssue fileFile = new FilePairIssue(fileName, fileName2, issue);

        if (cummulativeCodeChurnMap.containsKey(fileFile)) {
            codeChurn = cummulativeCodeChurnMap.get(fileFile);
        } else {
            codeChurn = pairFileDAO.calculeCummulativeCodeChurnAddDelChange(
                    fileName, fileName2, issue, allPairFileIssues, fixVersion);
            cummulativeCodeChurnMap.put(fileFile, codeChurn);
        }
        return codeChurn;
    }

    Set<Committer> selectCommitters(String filename, Set<Integer> issues) {
        if (fileCommitters.containsKey(filename)) {
            return fileCommitters.get(filename);
        } else {
            final Set<Committer> selectCommitters = fileDAO.selectCommitters(filename, issues);
            fileCommitters.put(filename, selectCommitters);
            return selectCommitters;
        }
    }

    List<Integer> selectIssues(String filename) {
        if (filesIssuesCacher.containsKey(filename)) {
            return filesIssuesCacher.get(filename);
        } else {
            final List<Integer> fileIssues = fileDAO.selectIssues(filename);
            filesIssuesCacher.put(filename, fileIssues);
            return fileIssues;
        }
    }

    Set<Commit> selectFilesAndCommitByIssue(Integer issue) {
        if (issuesCommitsCacher.containsKey(issue)) {
            return issuesCommitsCacher.get(issue);
        } else {
            final Set<Commit> fileIssues = dao.selectFilesAndCommitByIssue(issue);
            issuesCommitsCacher.put(issue, fileIssues);
            return fileIssues;
        }
    }
}
