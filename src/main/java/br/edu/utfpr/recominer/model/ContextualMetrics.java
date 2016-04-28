package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.metric.committer.CommitterFileMetrics;
import br.edu.utfpr.recominer.metric.file.FileMetrics;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author douglas
 */
public class ContextualMetrics {

    public static final String HEADER
            = "file1;file2;"
            // metricas da issue
            + IssueMetrics.HEADER
            + NetworkMetrics.HEADER
            + CommitMetrics.HEADER
            + CommitterFileMetrics.HEADER
            + FileMetrics.HEADER
            + "isFilePairChanged;" // o par mudou nesse commit? 0 = não, 1 = sim
            //+ "changedAfterReopened" // index (1a reabertura, 2a, 3a e assim sucessivamente) onde o arquivo B foi alterado após a issue ter sido reaberta
            ;

    private final FileIssue fileIssue;
    private final File file;
    private final String file2;
    private final IssueMetrics issueMetrics;
    private FilePairApriori filePairApriori;
    private NetworkMetrics networkMetrics;
    private CommitMetrics commitMetrics;
    private CommitterFileMetrics committerFileMetrics;
    private FileMetrics fileMetrics;
    private int changedAfterReopened;

    private int changed = 0;

    public ContextualMetrics(String file, String file2, IssueMetrics issueMetrics) {
        this.file = new File(file);
        this.file2 = file2;
        this.issueMetrics = issueMetrics;
        this.fileIssue = new FileIssue(getFile(), issueMetrics.getIssueId());
    }

    public ContextualMetrics(String file, String file2, Commit commit, IssueMetrics issueMetrics) {
        this.file = new File(file);
        this.file2 = file2;
        this.issueMetrics = issueMetrics;
        this.fileIssue = new FileIssueCommit(getFile(), issueMetrics.getIssueId(), commit);
    }

    public ContextualMetrics(String file, String file2, Integer issue, double... metrics) {
        this.file = new File(file);
        this.file2 = file2;
        this.issueMetrics = new EmptyIssueMetrics();
        this.fileIssue = new FileIssue(getFile(), issue);
    }

    public ContextualMetrics(String file, String file2, Integer issue, List<Double> metrics) {
        this.file = new File(file);
        this.file2 = file2;
        this.issueMetrics = new EmptyIssueMetrics();
        this.fileIssue = new FileIssue(getFile(), issue);
    }

    public String getHeader() {
        return HEADER;
    }

    public File getFile() {
        return file;
    }

    public FileIssue getFileIssue() {
        return fileIssue;
    }

    public IssueMetrics getIssueMetrics() {
        return issueMetrics;
    }

    public FilePairApriori getFilePairApriori() {
        return filePairApriori;
    }

    public void setFilePairApriori(FilePairApriori filePairApriori) {
        this.filePairApriori = filePairApriori;
    }

    public NetworkMetrics getNetworkMetrics() {
        return networkMetrics;
    }

    public void setNetworkMetrics(NetworkMetrics networkMetrics) {
        this.networkMetrics = networkMetrics;
    }

    public CommitMetrics getCommitMetrics() {
        return commitMetrics;
    }

    public void setCommitMetrics(CommitMetrics commitMetrics) {
        this.commitMetrics = commitMetrics;
    }

    public CommitterFileMetrics getCommitterFileMetrics() {
        return committerFileMetrics;
    }

    public void setCommitterFileMetrics(CommitterFileMetrics committerFileMetrics) {
        this.committerFileMetrics = committerFileMetrics;
    }

    public FileMetrics getFileMetrics() {
        return fileMetrics;
    }

    public void setFileMetrics(FileMetrics fileMetrics) {
        this.fileMetrics = fileMetrics;
    }

    public int getChangedAfterReopened() {
        return changedAfterReopened;
    }

    public void setFileBChangedAfterReopened(int reopenedIndex) {
        this.changedAfterReopened = reopenedIndex;
    }

    public void changed() {
        changed = 1;
    }

    public void unchanged() {
        changed = 0;
    }

    public int getChanged() {
        return changed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fileIssue).append(";")
                .append(file2).append(";");

        if (issueMetrics != null) {
            sb.append(issueMetrics);
        }

        if (getNetworkMetrics() != null) {
            sb.append(getNetworkMetrics());
        }

        if (getCommitMetrics() != null) {
            sb.append(getCommitMetrics());
        }

        if (getCommitterFileMetrics() != null) {
            sb.append(getCommitterFileMetrics());
        }

        sb.append(getChanged());
        //sb.append(";").append(changedAfterReopened);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + fileIssue.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ContextualMetrics)) {
            return false;
        }
        final ContextualMetrics other = (ContextualMetrics) obj;
        return Objects.equals(fileIssue, other.fileIssue);
    }
}
