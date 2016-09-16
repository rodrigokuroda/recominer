package br.edu.utfpr.recominer.batch.dataset;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;
import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class IssueCommit {
    
    private Issue issue;
    private Commit commit;

    public IssueCommit(Issue issue, Commit commit) {
        this.issue = issue;
        this.commit = commit;
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
        int hash = 5;
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
        final IssueCommit other = (IssueCommit) obj;
        if (!Objects.equals(this.issue, other.issue)) {
            return false;
        }
        if (!Objects.equals(this.commit, other.commit)) {
            return false;
        }
        return true;
    }
    
    
}
