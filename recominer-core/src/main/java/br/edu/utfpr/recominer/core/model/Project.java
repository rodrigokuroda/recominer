package br.edu.utfpr.recominer.core.model;

import java.io.Serializable;
import java.util.Date;
import org.springframework.data.domain.Persistable;

/**
 * Represents a software project, containing information about of it.
 * 
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class Project implements Persistable<Integer>, Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String projectName;
    private String schemaPrefix;
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

    public Project(Integer id, String projectName, String schemaPrefix, String issueTrackerUrl, IssueTracker issueTrackerSystem, String versionControlUrl) {
        this.id = id;
        this.projectName = projectName;
        this.schemaPrefix = schemaPrefix;
        this.issueTrackerUrl = issueTrackerUrl;
        this.issueTracker = issueTrackerSystem;
        this.versionControlUrl = versionControlUrl;
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

    public String getSchemaPrefix() {
        return schemaPrefix;
    }

    public void setSchemaPrefix(String schemaPrefix) {
        this.schemaPrefix = schemaPrefix;
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
