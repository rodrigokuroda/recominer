package br.edu.utfpr.recominer.core.model;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class Cochange {
    
    private File file;
    private Commit commit;

    public Cochange(File file, Commit commit) {
        this.file = file;
        this.commit = commit;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.file);
        hash = 17 * hash + Objects.hashCode(this.commit);
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
        final Cochange other = (Cochange) obj;
        if (!Objects.equals(this.file, other.file)) {
            return false;
        }
        if (!Objects.equals(this.commit, other.commit)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return file.getId() + ";" + commit.getId()+ ";";
    }
    
    
}
