package br.edu.utfpr.recominer.core.model;

import java.io.Serializable;
import java.util.Date;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class Project implements Persistable<Integer>, Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String projectName;
    private String issueTrackerUrl;
    private IssueTracker issueTracker;
    private String versionControlUrl;
    private VersionControl versionControl;
    private String repositoryPath;
    private Date lastCommitDateAnalyzed;
    private Date lastIssueUpdateAnalyzed;
    private Date lastItsUpdate;
    private Date lastVcsUpdate;
    private Date lastIssueUpdateAnalyzedForCochange;
    private Date lastIssueUpdateAnalyzedForVersion;
    private Date lastAprioriUpdate;
    private Date lastIssueUpdateForMetrics;

    public Project() {
    }

    public Project(Integer id) {
        this.id = id;
    }

    public Project(Integer id, String projectName, String issueTrackerUrl, IssueTracker issueTrackerSystem, String versionControlUrl) {
        this.id = id;
        this.projectName = projectName;
        this.issueTrackerUrl = issueTrackerUrl;
        this.issueTracker = issueTrackerSystem;
        this.versionControlUrl = versionControlUrl;
    }

    /**
     * "SELECT id," + " issue_tracker_url," + " last_its_update," + "
     * last_vcs_update," + " project_name," + " repository_path," + "
     * version_control_url," + " issue_tracker," + " version_control," + "
     * last_commit_date_analyzed," + " last_issue_update_analyzed," + "
     * last_issue_update_analyzed_for_cochange," + " last_apriori_update"
     *
     * @param rawProject
     */
    public Project(Object[] rawProject) {
        id = (Integer) rawProject[0];
        issueTrackerUrl = (String) rawProject[1];
        lastItsUpdate = (Date) rawProject[2];
        lastVcsUpdate = (Date) rawProject[3];
        projectName = (String) rawProject[4];
        repositoryPath = (String) rawProject[5];
        versionControlUrl = (String) rawProject[6];
        issueTracker = new IssueTracker((Integer) rawProject[7]);
        versionControl = new VersionControl((Integer) rawProject[8]);
        lastCommitDateAnalyzed = (Date) rawProject[9];
        lastIssueUpdateAnalyzed = (Date) rawProject[10];
        lastIssueUpdateAnalyzedForCochange = (Date) rawProject[11];
        lastAprioriUpdate = (Date) rawProject[12];
    }

    @Override
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

    public Date getLastIssueUpdateAnalyzedForCochange() {
        return lastIssueUpdateAnalyzedForCochange;
    }

    public void setLastIssueUpdateAnalyzedForCochange(Date lastIssueUpdateAnalyzedForCochange) {
        this.lastIssueUpdateAnalyzedForCochange = lastIssueUpdateAnalyzedForCochange;
    }

    public Date getLastIssueUpdateAnalyzedForVersion() {
        return lastIssueUpdateAnalyzedForVersion;
    }

    public void setLastIssueUpdateAnalyzedForVersion(Date lastIssueUpdateAnalyzedForVersion) {
        this.lastIssueUpdateAnalyzedForVersion = lastIssueUpdateAnalyzedForVersion;
    }

    public Date getLastAprioriUpdate() {
        return lastAprioriUpdate;
    }

    public void setLastAprioriUpdate(Date lastAprioriUpdate) {
        this.lastAprioriUpdate = lastAprioriUpdate;
    }

    public Date getLastIssueUpdateForMetrics() {
        return lastIssueUpdateForMetrics;
    }

    public void setLastIssueUpdateForMetrics(Date lastIssueUpdateForMetrics) {
        this.lastIssueUpdateForMetrics = lastIssueUpdateForMetrics;
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

    @Override
    public boolean isNew() {
        return id == null;
    }

}
