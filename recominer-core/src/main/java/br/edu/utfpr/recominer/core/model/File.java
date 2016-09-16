package br.edu.utfpr.recominer.core.model;

import br.edu.utfpr.recominer.core.model.Commit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class File implements Persistable<Integer>{

    private final Integer id;
    private final String fileName;
    private final Set<Commit> commits;

    public File(Integer id) {
        this.id = id;
        this.fileName = null;
        this.commits = new HashSet<>();
    }
    
    public File(Integer id, String fileName) {
        this.id = id;
        this.fileName = fileName;
        this.commits = new HashSet<>();
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }
    
    public void addCommit(Commit commit) {
        this.commits.add(commit);
    }
    
    public Set<Commit> getCommits() {
        return commits;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.id);
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
        final File other = (File) obj;
        if (Objects.equals(this.id, other.id)) {
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
        return fileName + ";";
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}
