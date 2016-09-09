package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.core.model.File;
import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class CodeChurn {

    public static final String HEADER = "addedLines;deletedLines;changedLines;";

    private final File file;
    private final File file2; // optional
    private long additions;
    private long deletions;

    public CodeChurn(File file, long additions, long deletions) {
        this.file = file;
        this.file2 = null;
        this.additions = additions;
        this.deletions = deletions;
    }

    public CodeChurn(File file, File file2, long additions, long deletions) {
        this.file = file;
        this.file2 = file2;
        this.additions = additions;
        this.deletions = deletions;
    }

    public void add(final CodeChurn codeChurn) {
        this.additions += codeChurn.additions;
        this.deletions += codeChurn.deletions;
    }

    public File getFile() {
        return file;
    }

    public File getFile2() {
        return file2;
    }

    public long getAdditions() {
        return additions;
    }

    public void setAdditions(long additions) {
        this.additions = additions;
    }

    public long getDeletions() {
        return deletions;
    }

    public void setDeletions(long deletions) {
        this.deletions = deletions;
    }

    public double getAdditionsNormalized() {
        return getChanges() == 0 ? 0 : (double) additions / (double) getChanges();
    }

    public double getDeletionsNormalized() {
        return getChanges() == 0 ? 0 : (double) deletions / (double) getChanges();
    }

    public long getChanges() {
        return additions + deletions;
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

    @Override
    public String toString() {
        return additions + ";" + deletions + ";" + getChanges() + ";";
    }
    
    
}
