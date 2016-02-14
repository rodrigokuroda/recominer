package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.metric.committer.Committer;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class Commit {

    public static final String HEADER = Committer.HEADER + "numFiles;";

    private final Integer id;
    private final Date commitDate;
    private final Committer committer;
    private final Issue issue;
    private final Set<File> files;

    public Commit(Integer id) {
        this.id = id;
        this.committer = null;
        this.commitDate = null;
        this.files = new HashSet<>();
        this.issue = null;
    }

    public Commit(Integer id, Committer commiter, Date commitDate) {
        this.id = id;
        this.committer = commiter;
        this.commitDate = commitDate;
        this.files = new HashSet<>();
        this.issue = null;
    }

    public Commit(Integer id, Committer commiter, Date commitDate, Set<File> files) {
        this.id = id;
        this.committer = commiter;
        this.commitDate = commitDate;
        this.files = files;
        this.issue = null;
    }

    public Commit(Integer id, Committer commiter, Date commitDate, Issue issue, Set<File> files) {
        this.id = id;
        this.committer = commiter;
        this.commitDate = commitDate;
        this.files = files;
        this.issue = issue;
    }

    public Integer getId() {
        return id;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public int getNumberOfFiles() {
        return files.size();
    }

    public Committer getCommitter() {
        return committer;
    }

    public Set<File> getFiles() {
        return files;
    }

    public Issue getIssue() {
        return issue;
    }

    @Override
    public String toString() {
        return committer.toString() + getNumberOfFiles() + ";";
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

}
