package br.edu.utfpr.recominer.core.model;

import java.io.Serializable;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class IssueTracker implements Persistable<Integer>, Serializable {

    private Integer id;
    private IssueTrackerSystem system;
    private String username;
    private String password;
    private String token;
    private Integer extractionDelay;

    public IssueTracker() {
    }

    public IssueTracker(final Integer id) {
        this.id = id;
    }

    public IssueTracker(Integer id, IssueTrackerSystem system, String username, String password, String token, Integer miningDelay) {
        this.id = id;
        this.system = system;
        this.username = username;
        this.password = password;
        this.token = token;
        this.extractionDelay = miningDelay;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public IssueTrackerSystem getSystem() {
        return system;
    }

    public void setSystem(IssueTrackerSystem system) {
        this.system = system;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getExtractionDelay() {
        return extractionDelay;
    }

    public void setExtractionDelay(Integer extractionDelay) {
        this.extractionDelay = extractionDelay;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.id);
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
        final IssueTracker other = (IssueTracker) obj;
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
