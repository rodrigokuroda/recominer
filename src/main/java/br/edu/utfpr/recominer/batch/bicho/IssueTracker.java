package br.edu.utfpr.recominer.batch.bicho;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Entity
@Table(name = "issue_tracker")
public class IssueTracker implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "system")
    @Enumerated(EnumType.STRING)
    private IssueTrackerSystem system;

    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "user")
    private String user;

    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "password")
    private String password;

    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "token")
    private String token;

    @NotNull
    @Column(name = "mining_delay")
    private Integer miningDelay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IssueTrackerSystem getSystem() {
        return system;
    }

    public void setSystem(IssueTrackerSystem system) {
        this.system = system;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

}
