package br.edu.utfpr.recominer.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class IssueCommitFile {

    private final Issue issue;
    private final Commit commit;
    private final String filename;
    private final AtomicInteger count;

    public IssueCommitFile(Issue issue, Commit commit, String filename) {
        this.issue = issue;
        this.commit = commit;
        this.filename = filename;
        count = new AtomicInteger(1);
    }

    public Issue getIssue() {
        return issue;
    }

    public Commit getCommit() {
        return commit;
    }

    public String getFilename() {
        return filename;
    }

    public int getCount() {
        return count.get();
    }

    public int add() {
        return count.incrementAndGet();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.issue);
        hash = 79 * hash + Objects.hashCode(this.commit);
        hash = 79 * hash + Objects.hashCode(this.filename);
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
        final IssueCommitFile other = (IssueCommitFile) obj;
        if (!Objects.equals(this.issue, other.issue)) {
            return false;
        }
        if (!Objects.equals(this.commit, other.commit)) {
            return false;
        }
        if (!Objects.equals(this.filename, other.filename)) {
            return false;
        }
        return true;
    }

}
