package br.edu.utfpr.recominer.batch.associationrule;

import br.edu.utfpr.recominer.core.model.File;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
public class Fileset implements Persistable<Long> {
    
    private Long id;
    private Set<File> file;

    public Fileset(Long id) {
        this.id = id;
    }

    public Fileset(Long id, Set<File> file) {
        this.id = id;
        this.file = file;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<File> getFiles() {
        return file;
    }

    public void setFile(Set<File> file) {
        this.file = file;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.id);
        hash = 23 * hash + Objects.hashCode(this.file);
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
        final Fileset other = (Fileset) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.file, other.file)) {
            return false;
        }
        return true;
    }
    
    
}
