package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.metric.file.FileMetrics;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class ContextualMetrics implements Persistable<Integer> {

    public static final String HEADER
            = IssuesMetrics.HEADER
            + NetworkMetrics.HEADER
            + CommitMetrics.HEADER
            + FileMetrics.HEADER //+ "changedAfterReopened" // index (1a reabertura, 2a, 3a e assim sucessivamente) onde o arquivo B foi alterado após a issue ter sido reaberta
            ;

    private Integer id;
    private Commit commit;
    private File file;
    private IssuesMetrics issueMetrics;
    private NetworkMetrics networkMetrics;
    private CommitMetrics commitMetrics;
    private FileMetrics fileMetrics;

    public ContextualMetrics() {
    }

    public ContextualMetrics(IssuesMetrics issueMetrics, NetworkMetrics networkMetrics, CommitMetrics commitMetrics, FileMetrics fileMetrics) {
        this.issueMetrics = issueMetrics;
        this.networkMetrics = networkMetrics;
        this.commitMetrics = commitMetrics;
        this.fileMetrics = fileMetrics;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
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
                && Objects.equals(file, other.file);
    }
}
