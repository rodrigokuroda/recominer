package br.edu.utfpr.recominer.batch.bicho;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Entity
@Table(name = "issue_tracker", schema = "recominer")
public class IssueTracker implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "system")
    @Enumerated(EnumType.STRING)
    private IssueTrackerSystem system;

    @Size(min = 1, max = 45)
    @Column(name = "username")
    private String username;

    @Size(min = 1, max = 45)
    @Column(name = "password")
    private String password;

    @Size(min = 1, max = 45)
    @Column(name = "token")
    private String token;

    @NotNull
    @Column(name = "mining_delay")
    private Integer miningDelay;
    
    @OneToMany(mappedBy = "issueTracker", cascade = CascadeType.ALL)
    private List<Project> project;

    public IssueTracker(final Integer id) {
        this.id = id;
    }

    /**
     * "SELECT"
                + "    issue_tracker.mining_delay,"
                + "    issue_tracker.password,"
                + "    issue_tracker.system,"
                + "    issue_tracker.token,"
                + "    issue_tracker.username"
                + "FROM recominer.issue_tracker"
                + " WHERE issue_tracker.id = ?";
     * @param raw 
     */
    public IssueTracker(final Object[] raw) {
        miningDelay = (Integer) raw[0];
        password = (String) raw[1];
        system = IssueTrackerSystem.valueOf((String) raw[2]);
        token = (String) raw[3];
        username = (String) raw[4];
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

    public Integer getMiningDelay() {
        return miningDelay;
    }

    public void setMiningDelay(Integer miningDelay) {
        this.miningDelay = miningDelay;
    }

    public List<Project> getProject() {
        return project;
    }

    public void setProject(List<Project> project) {
        this.project = project;
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

}
