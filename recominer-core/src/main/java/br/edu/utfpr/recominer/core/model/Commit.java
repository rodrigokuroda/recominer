package br.edu.utfpr.recominer.core.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class Commit implements Persistable<Integer> {

    private Integer id;
    private String revision;
    private Date commitDate;
    private Committer committer;
    private Issue issue;
    private final Set<File> files;

    public Commit(Integer id) {
        this.id = id;
        this.revision = null;
        this.committer = null;
        this.commitDate = null;
        this.files = new HashSet<>();
        this.issue = null;
    }

    public Commit(Integer id, String revision, Committer commiter) {
        this.id = id;
        this.revision = revision;
        this.committer = commiter;
        this.commitDate = null;
        this.files = new HashSet<>();
        this.issue = null;
    }

    public Commit(Integer id, String revision, Committer commiter, Date commitDate) {
        this.id = id;
        this.revision = revision;
        this.committer = commiter;
        this.commitDate = commitDate;
        this.files = new HashSet<>();
        this.issue = null;
    }

    public Commit(Integer id, String revision, Committer commiter, Date commitDate, Set<File> files) {
        this.id = id;
        this.revision = revision;
        this.committer = commiter;
        this.commitDate = commitDate;
        this.files = files;
        this.issue = null;
    }

    public Commit(Integer id, String revision, Committer commiter, Date commitDate, Issue issue, Set<File> files) {
        this.id = id;
        this.revision = revision;
        this.committer = commiter;
        this.commitDate = commitDate;
        this.files = files;
        this.issue = issue;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public Committer getCommitter() {
        return committer;
    }

    public void setCommitter(Committer committer) {
        this.committer = committer;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    @Override
    public String toString() {
        return revision + ";"
                + committer.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.id);
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
        final Commit other = (Commit) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

}
