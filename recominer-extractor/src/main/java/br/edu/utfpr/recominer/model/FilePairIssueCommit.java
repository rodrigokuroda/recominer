package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.core.model.FilePair;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class FilePairIssueCommit implements Persistable<Integer> {
    
    private Integer id;
    private FilePair filePair;
    private Issue issue;
    private Commit commit;

    public FilePairIssueCommit() {
    }

    public FilePairIssueCommit(Integer id) {
        this.id = id;
    }

    public FilePairIssueCommit(FilePair filePair, Issue issue, Commit commit) {
        this.filePair = filePair;
        this.issue = issue;
        this.commit = commit;
    }

    public FilePairIssueCommit(Integer id, FilePair filePair, Issue issue, Commit commit) {
        this.id = id;
        this.filePair = filePair;
        this.issue = issue;
        this.commit = commit;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

    public FilePair getFilePair() {
        return filePair;
    }

    public void setFilePair(FilePair filePair) {
        this.filePair = filePair;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.filePair);
        hash = 67 * hash + Objects.hashCode(this.issue);
        hash = 67 * hash + Objects.hashCode(this.commit);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FilePairIssueCommit other = (FilePairIssueCommit) obj;
        if (!Objects.equals(this.filePair, other.filePair)) {
            return false;
        }
        if (!Objects.equals(this.issue, other.issue)) {
            return false;
        }
        if (!Objects.equals(this.commit, other.commit)) {
            return false;
        }
        return true;
    }
    
}
