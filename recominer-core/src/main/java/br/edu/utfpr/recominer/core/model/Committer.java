package br.edu.utfpr.recominer.core.model;

import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class Committer implements Persistable<Integer> {

    private final Integer id;
    private final String name;
    private final String email;

    public Committer(Integer id) {
        this.id = id;
        this.name = null;
        this.email = null;
    }

    public Committer(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    
    public String getNameOrEmail() {
        return name == null ? email : name == null ? "" : name;
    }

    @Override
    public String toString() {
        return name + ";";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.id);
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
        final Committer other = (Committer) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

}
