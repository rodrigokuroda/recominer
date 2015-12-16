package br.edu.utfpr.recominer.services.executor;

import br.edu.utfpr.recominer.dao.BichoFileDAO;
import br.edu.utfpr.recominer.dao.BichoPairFileDAO;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.services.metric.Cacher;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class ProcessNewCommit implements Runnable {

    private final Commit commit;
    private final Cacher cacher;
    private final CummulativeMetrics cummulativeMetrics;

    public ProcessNewCommit(final Commit commit, final BichoFileDAO fileDao, final BichoPairFileDAO pairFileDao) {
        this.commit = commit;
        this.cacher = new Cacher(fileDao, pairFileDao);
        this.cummulativeMetrics = new CummulativeMetrics(commit);
    }

    @Override
    public void run() {
//        final List<File> files = new ArrayList<>(commit.getFiles());
//        final List<FilePair> filePairList = new FilePairBuilder(files).pairFiles();
//
//        final Issue commitIssue = commit.getIssue(); // TODO pode estar relacionado com mais de uma issue
//        final IssueMetrics issueMetrics = cacher.calculeIssueMetrics(commitIssue);
//        final Committer committer = commit.getCommitter();
//        final CummulativeCommitterFileMetricsCalculator committerFileMetricsCalculator = new CummulativeCommitterFileMetricsCalculator(bichoFileDAO);
//        for (FilePair filePair : filePairList) {
//
//            // TODO refatorar para não precisar passar o issue metrics
//            // Metrics is computed only for file 1, file 2 is used to check if it co-changed with file 1
//            final FileIssueMetrics fileIssueMetrics = new FileIssueMetrics(filePair.getFile1().getFileName(), filePair.getFile2().getFileName(), commit, issueMetrics);
//
//            IssueCommitFile commiterIssueCommitFile = new IssueCommitFile(commitIssue, commit, filePair.getFile1().getFileName());
//
//            final CommitMetrics commitMetrics = new CommitMetrics(commit);
//            fileIssueMetrics.setCommitMetrics(commitMetrics);
//
//            final CommitterFileMetrics committerFileMetrics = committerFileMetricsCalculator.calculeForCommit(filePair.getFile1().getFileName(), committer, commitInIssue);
//            fileIssueMetrics.setCommitterFileMetrics(committerFileMetrics);
//
//            if (commit.getFiles().contains(filePair.getFile1())) { // se houve commit do arquivo 2, entao o par mudou
//                // muda a coluna changed para 1, indicando que o par analisado mudou nessa issue
//                fileIssueMetrics.changed();
//            }
//        }
    }

}
