package br.edu.utfpr.recominer.model;

import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class CodeChurn {

    // TODO mudar de String para File
    private final String file;
    private final String file2; // optional
    private long additions;
    private long deletions;
    private long changes;

    public CodeChurn(String file, long additions, long deletions) {
        this.file = file;
        this.file2 = null;
        this.additions = additions;
        this.deletions = deletions;
        this.changes = additions + deletions;
    }

    public CodeChurn(String file, long additions, long deletions, long changes) {
        this.file = file;
        this.file2 = null;
        this.additions = additions;
        this.deletions = deletions;
        this.changes = changes;
    }

    public CodeChurn(String file, String file2, long additions, long deletions) {
        this.file = file;
        this.file2 = file2;
        this.additions = additions;
        this.deletions = deletions;
        this.changes = additions + deletions;
    }

    public CodeChurn(String file, String file2, long additions, long deletions, long changes) {
        this.file = file;
        this.file2 = file2;
        this.additions = additions;
        this.deletions = deletions;
        this.changes = changes;
    }

    public CodeChurn(long additions, long deletions, long changes) {
        this.file = null;
        this.file2 = null;
        this.additions = additions;
        this.deletions = deletions;
        this.changes = changes;
    }

    public void add(final CodeChurn codeChurn) {
        this.additions += codeChurn.additions;
        this.deletions += codeChurn.deletions;
        this.changes += codeChurn.changes;
    }

    public String getFile() {
        return file;
    }

    public String getFile2() {
        return file2;
    }

    public long getAdditions() {
        return additions;
    }

    public long getDeletions() {
        return deletions;
    }

    public double getAdditionsNormalized() {
        return changes == 0 ? 0 : (double) additions / (double) changes;
    }

    public double getDeletionsNormalized() {
        return changes == 0 ? 0 : (double) deletions / (double) changes;
    }

    public long getChanges() {
        return changes;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.file)
                + (this.file2 == null ? 0 : Objects.hashCode(this.file2));
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
        final CodeChurn other = (CodeChurn) obj;

        if (Objects.equals(this.file, other.file)
                && Objects.equals(this.file2, other.file2)) {
            return true;
        }
        if (Objects.equals(this.file, other.file2)
                && Objects.equals(this.file2, other.file)) {
            return true;
        }
        return false;
    }

}
