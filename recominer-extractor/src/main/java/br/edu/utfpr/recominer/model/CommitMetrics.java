package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.core.model.Commit;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class CommitMetrics implements Persistable<Integer> {

    public static final String HEADER
            = "commitMetricId;commitMetricCommitId;revision;committer;";

    private Integer id;
    private Commit commit;
    private Integer commitId;
    private String revision;
    private Integer committerId;
    private String committerName;

    public CommitMetrics() {
    }

    public CommitMetrics(Integer id) {
        this.id = id;
    }

    public CommitMetrics(Integer id, Commit commit) {
        this.id = id;
        this.commit = commit;
        this.commitId = commit.getId();
        this.revision = commit.getRevision();
        this.committerId = commit.getCommitter().getId();
        this.committerName = commit.getCommitter().getNameOrEmail();
    }

    @Override
    public boolean isNew() {
        return id == null || id == 0;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Commit getCommit() {
        return commit;
    }
    
    public Integer getCommitId() {
        return commitId;
    }

    public void setCommitId(Integer commitId) {
        this.commitId = commitId;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Integer getCommitterId() {
        return committerId;
    }

    public void setCommitterId(Integer committerId) {
        this.committerId = committerId;
    }

    public String getCommitterName() {
        return committerName;
    }

    public void setCommitterName(String committerName) {
        this.committerName = committerName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(";")
                .append(commit.getId()).append(";")
                .append(revision).append(";")
                .append(committerName).append(";");
        
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.commitId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CommitMetrics other = (CommitMetrics) obj;
        if (!Objects.equals(this.commitId, other.commitId)) {
            return false;
        }
        return true;
    }
}
