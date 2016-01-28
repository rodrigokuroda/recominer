package br.edu.utfpr.recominer.batch.aggregator;

import br.edu.utfpr.recominer.batch.bicho.IssueTracker;
import br.edu.utfpr.recominer.batch.cvsanaly.VersionControl;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Entity
@Table(name = "project", schema = "recominer")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "project_name")
    private String projectName;

    @NotNull
    @Size(min = 1, max = 1024)
    @Column(name = "issue_tracker_url")
    private String issueTrackerUrl;

    @ManyToOne
    @JoinColumn(name = "issue_tracker", referencedColumnName = "id")
    private IssueTracker issueTracker;

    @NotNull
    @Size(min = 1, max = 1024)
    @Column(name = "version_control_url")
    private String versionControlUrl;

    @ManyToOne
    @JoinColumn(name = "version_control", referencedColumnName = "id")
    private VersionControl versionControl;
    
    @Column(name = "repository_path")
    private String repositoryPath;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_commit_date_analyzed")
    private Date lastCommitDateAnalyzed;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_issue_update_analyzed")
    private Date lastIssueUpdateAnalyzed;

    @Column(name = "last_its_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastItsUpdate;

    @Column(name = "last_vcs_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastVcsUpdate;

    public Project() {
    }

    public Project(Long id) {
        this.id = id;
    }

    public Project(Long id, String projectName, String issueTrackerUrl, IssueTracker issueTrackerSystem, String versionControlUrl) {
        this.id = id;
        this.projectName = projectName;
        this.issueTrackerUrl = issueTrackerUrl;
        this.issueTracker = issueTrackerSystem;
        this.versionControlUrl = versionControlUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public IssueTracker getIssueTracker() {
        return issueTracker;
    }

    public void setIssueTracker(IssueTracker issueTracker) {
        this.issueTracker = issueTracker;
    }

    public String getVersionControlUrl() {
        return versionControlUrl;
    }

    public void setVersionControlUrl(String versionControlUrl) {
        this.versionControlUrl = versionControlUrl;
    }

    public VersionControl getVersionControl() {
        return versionControl;
    }

    public void setVersionControl(VersionControl versionControl) {
        this.versionControl = versionControl;
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public Date getLastCommitDateAnalyzed() {
        return lastCommitDateAnalyzed;
    }

    public void setLastCommitDateAnalyzed(Date lastCommitDateAnalyzed) {
        this.lastCommitDateAnalyzed = lastCommitDateAnalyzed;
    }

    public Date getLastIssueUpdateAnalyzed() {
        return lastIssueUpdateAnalyzed;
    }

    public void setLastIssueUpdateAnalyzed(Date lastIssueUpdateAnalyzed) {
        this.lastIssueUpdateAnalyzed = lastIssueUpdateAnalyzed;
    }

    public Date getLastItsUpdate() {
        return lastItsUpdate;
    }

    public void setLastItsUpdate(Date lastItsUpdate) {
        this.lastItsUpdate = lastItsUpdate;
    }

    public Date getLastVcsUpdate() {
        return lastVcsUpdate;
    }

    public void setLastVcsUpdate(Date lastVcsUpdate) {
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
        return "Project{" + "id=" + id + ", projectName=" + projectName + ", issueTrackerUrl=" + issueTrackerUrl + ", issueTrackerSystem=" + issueTracker + ", versionControlUrl=" + versionControlUrl + ", lastCommitAnalysed=" + lastCommitDateAnalyzed + ", lastItsUpdate=" + lastItsUpdate + ", lastVcsUpdate=" + lastVcsUpdate + '}';
    }

}
