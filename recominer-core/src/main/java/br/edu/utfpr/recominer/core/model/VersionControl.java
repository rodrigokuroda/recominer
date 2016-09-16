package br.edu.utfpr.recominer.core.model;

import br.edu.utfpr.recominer.core.model.Project;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class VersionControl implements Serializable {

    private Integer id;
    
    private String username;
    
    private String password;
    
    private List<Project> project;

    public VersionControl() {
    }

    public VersionControl(final Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Project> getProject() {
        return project;
    }

    public void setProject(List<Project> project) {
        this.project = project;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.id);
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
        final VersionControl other = (VersionControl) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
