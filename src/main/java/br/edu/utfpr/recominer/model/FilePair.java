package br.edu.utfpr.recominer.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FilePair {

    private Integer id;
    private final File file1;
    private final File file2;

    public FilePair(final String file1, final String file2) {
        this.file1 = new File(file1);
        this.file2 = new File(file2);
    }

    public FilePair(final File file1, final File file2) {
        this.file1 = file1;
        this.file2 = file2;
    }

    public FilePair(final Integer id, final File file1, final File file2) {
        this.id = id;
        this.file1 = file1;
        this.file2 = file2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public File getFile1() {
        return file1;
    }

    public File getFile2() {
        return file2;
    }
    
    public Set<Commit> getCommits() {
        final Set<Commit> intersectionCommitsFromFile1AndFile2 = new HashSet<>(file1.getCommits());
        intersectionCommitsFromFile1AndFile2.retainAll(file2.getCommits());
        return intersectionCommitsFromFile1AndFile2;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.file1) + Objects.hashCode(this.file2);
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
        final FilePair other = (FilePair) obj;
        if (Objects.equals(this.file1, other.file1)
                && Objects.equals(this.file2, other.file2)) {
            return true;
        }
        if (Objects.equals(this.file1, other.file2)
                && Objects.equals(this.file2, other.file1)) {
            return true;
        }
        return false;
    }

    /**
     * Prints the filename, filename2. The issueWeight and issues are printed
     * only if was added issues.
     *
     * @return String The attributes separated by semicolon (;)
     */
    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        toString.append(file1).append(';').append(file2).append(';');
        return toString.toString();
    }

    public static String getToStringHeader() {
        return "file1;file2;";
    }
}
