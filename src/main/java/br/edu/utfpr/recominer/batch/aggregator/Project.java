package br.edu.utfpr.recominer.batch.aggregator;

import br.edu.utfpr.recominer.batch.extractor.IssueTracker;
import br.edu.utfpr.recominer.batch.extractor.VersionControl;
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
    private Integer id;

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

    @Column(name = "last_issue_update_analyzed_for_cochange")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastIssueUpdateAnalyzedForCochange;
    
    @Column(name = "last_issue_update_analyzed_for_version")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastIssueUpdateAnalyzedForVersion;

    @Column(name = "last_apriori_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAprioriUpdate;
    
    @Column(name = "last_metrics_calculation")
    @Temporal(TemporalType.TIMESTAMP)
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
     * "SELECT id,"
                    + "    issue_tracker_url,"
                    + "    last_its_update,"
                    + "    last_vcs_update,"
                    + "    project_name,"
                    + "    repository_path,"
                    + "    version_control_url,"
                    + "    issue_tracker,"
                    + "    version_control,"
                    + "    last_commit_date_analyzed,"
                    + "    last_issue_update_analyzed,"
                    + "    last_issue_update_analyzed_for_cochange,"
                    + "    last_apriori_update"
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

}
