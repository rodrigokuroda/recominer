package br.edu.utfpr.recominer.core.model;

import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Commit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class FilePair {

    public static final String HEADER = "file1;file2;";
    
    private Integer id;
    private final File file1;
    private final File file2;

    public FilePair(Integer id) {
        this.id = id;
        file1 = null;
        file2 = null;
    }

    public FilePair(File file1, File file2) {
        this.file1 = file1;
        this.file2 = file2;
    }

    public FilePair(Integer id, File file1, File file2) {
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
        toString.append(file1).append(file2);
        return toString.toString();
    }

}
