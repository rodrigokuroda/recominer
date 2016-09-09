package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.metric.file.FileMetrics;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import java.util.Objects;

/**
 *
 * @author douglas
 */
public class ContextualMetrics {

    public static final String HEADER
            = IssuesMetrics.HEADER
            + NetworkMetrics.HEADER
            + CommitMetrics.HEADER
            + FileMetrics.HEADER
            //+ "changedAfterReopened" // index (1a reabertura, 2a, 3a e assim sucessivamente) onde o arquivo B foi alterado após a issue ter sido reaberta
            ;

    private File file;
    private IssuesMetrics issueMetrics;
    private NetworkMetrics networkMetrics;
    private CommitMetrics commitMetrics;
    private FileMetrics fileMetrics;

    public ContextualMetrics(IssuesMetrics issueMetrics, NetworkMetrics networkMetrics, CommitMetrics commitMetrics, FileMetrics fileMetrics) {
        this.issueMetrics = issueMetrics;
        this.networkMetrics = networkMetrics;
        this.commitMetrics = commitMetrics;
        this.fileMetrics = fileMetrics;
    }
    
    public String getHeader() {
        return HEADER;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    public IssuesMetrics getIssueMetrics() {
        return issueMetrics;
    }

    public void setIssueMetrics(IssuesMetrics issueMetrics) {
        this.issueMetrics = issueMetrics;
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

    public FileMetrics getFileMetrics() {
        return fileMetrics;
    }

    public void setFileMetrics(FileMetrics fileMetrics) {
        this.fileMetrics = fileMetrics;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (issueMetrics != null) {
            sb.append(issueMetrics);
        }

        if (getNetworkMetrics() != null) {
            sb.append(getNetworkMetrics());
        }

        if (getCommitMetrics() != null) {
            sb.append(getCommitMetrics());
        }

        if (getFileMetrics() != null) {
            sb.append(getFileMetrics());
        }

        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + file.hashCode();
        hash = 89 * hash + issueMetrics.hashCode();
        hash = 89 * hash + commitMetrics.hashCode();
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
        return Objects.equals(issueMetrics, other.issueMetrics)
                && Objects.equals(commitMetrics, other.commitMetrics)
                && Objects.equals(file, other.file)
                ;
    }
}
