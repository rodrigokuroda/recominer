package br.edu.utfpr.recominer.batch.aggregator;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Entity
@Table(schema = "recominer", name = "projects")
@NamedQueries({
    @NamedQuery(name = "Project.findAll", query = "SELECT p FROM Project p"),
    @NamedQuery(name = "Project.findById", query = "SELECT p FROM Project p WHERE p.id = :id"),
    @NamedQuery(name = "Project.findByProjectName", query = "SELECT p FROM Project p WHERE p.projectName = :projectName"),
    @NamedQuery(name = "Project.findByIssueTrackerUrl", query = "SELECT p FROM Project p WHERE p.issueTrackerUrl = :issueTrackerUrl"),
    @NamedQuery(name = "Project.findByIssueTrackerSystem", query = "SELECT p FROM Project p WHERE p.issueTrackerSystem = :issueTrackerSystem"),
    @NamedQuery(name = "Project.findByVersionControlUrl", query = "SELECT p FROM Project p WHERE p.versionControlUrl = :versionControlUrl"),
    @NamedQuery(name = "Project.findByLastCommitAnalysed", query = "SELECT p FROM Project p WHERE p.lastCommitAnalysed = :lastCommitAnalysed"),
    @NamedQuery(name = "Project.findByLastItsUpdate", query = "SELECT p FROM Project p WHERE p.lastItsUpdate = :lastItsUpdate"),
    @NamedQuery(name = "Project.findByLastVcsUpdate", query = "SELECT p FROM Project p WHERE p.lastVcsUpdate = :lastVcsUpdate")})
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "project_name")
    private String projectName;

    @NotNull
    @Size(min = 1, max = 1024)
    @Column(name = "issue_tracker_url")
    private String issueTrackerUrl;

    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "issue_tracker_system")
    private String issueTrackerSystem;

    @NotNull
    @Size(min = 1, max = 1024)
    @Column(name = "version_control_url")
    private String versionControlUrl;

    @Column(name = "last_commit_analysed")
    private Integer lastCommitAnalysed;

    @Size(max = 45)
    @Column(name = "last_its_update")
    private String lastItsUpdate;

    @Size(max = 45)
    @Column(name = "last_vcs_update")
    private String lastVcsUpdate;

    public Project() {
    }

    public Project(Integer id) {
        this.id = id;
    }

    public Project(Integer id, String projectName, String issueTrackerUrl, String issueTrackerSystem, String versionControlUrl) {
        this.id = id;
        this.projectName = projectName;
        this.issueTrackerUrl = issueTrackerUrl;
        this.issueTrackerSystem = issueTrackerSystem;
        this.versionControlUrl = versionControlUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getIssueTrackerUrl() {
        return issueTrackerUrl;
    }

    public void setIssueTrackerUrl(String issueTrackerUrl) {
        this.issueTrackerUrl = issueTrackerUrl;
    }

    public String getIssueTrackerSystem() {
        return issueTrackerSystem;
    }

    public void setIssueTrackerSystem(String issueTrackerSystem) {
        this.issueTrackerSystem = issueTrackerSystem;
    }

    public String getVersionControlUrl() {
        return versionControlUrl;
    }

    public void setVersionControlUrl(String versionControlUrl) {
        this.versionControlUrl = versionControlUrl;
    }

    public Integer getLastCommitAnalysed() {
        return lastCommitAnalysed;
    }

    public void setLastCommitAnalysed(Integer lastCommitAnalysed) {
        this.lastCommitAnalysed = lastCommitAnalysed;
    }

    public String getLastItsUpdate() {
        return lastItsUpdate;
    }

    public void setLastItsUpdate(String lastItsUpdate) {
        this.lastItsUpdate = lastItsUpdate;
    }

    public String getLastVcsUpdate() {
        return lastVcsUpdate;
    }

    public void setLastVcsUpdate(String lastVcsUpdate) {
        this.lastVcsUpdate = lastVcsUpdate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Project)) {
            return false;
        }
        Project other = (Project) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Project{" + "id=" + id + ", projectName=" + projectName + ", issueTrackerUrl=" + issueTrackerUrl + ", issueTrackerSystem=" + issueTrackerSystem + ", versionControlUrl=" + versionControlUrl + ", lastCommitAnalysed=" + lastCommitAnalysed + ", lastItsUpdate=" + lastItsUpdate + ", lastVcsUpdate=" + lastVcsUpdate + '}';
    }

}
