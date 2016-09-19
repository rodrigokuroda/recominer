package br.edu.utfpr.recominer.model.issue;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class IssueExtBugzilla implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String alias;
    private Date deltaTs;
    private String reporterAccessible;
    private String cclistAccessible;
    private String classificationId;
    private String classification;
    private String product;
    private String component;
    private String version;
    private String repPlatform;
    private String opSys;
    private Integer dupId;
    private String bugFileLoc;
    private String statusWhiteboard;
    private String targetMilestone;
    private Integer votes;
    private String everconfirmed;
    private String qaContact;
    private String estimatedTime;
    private String remainingTime;
    private String actualTime;
    private Date deadline;
    private String keywords;
    private String flag;
    private String cc;
    private String groupBugzilla;
    private int issueId;

    public IssueExtBugzilla() {
    }

    public IssueExtBugzilla(Integer id) {
        this.id = id;
    }

    public IssueExtBugzilla(Integer id, Date deltaTs, int issueId) {
        this.id = id;
        this.deltaTs = deltaTs;
        this.issueId = issueId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Date getDeltaTs() {
        return deltaTs;
    }

    public void setDeltaTs(Date deltaTs) {
        this.deltaTs = deltaTs;
    }

    public String getReporterAccessible() {
        return reporterAccessible;
    }

    public void setReporterAccessible(String reporterAccessible) {
        this.reporterAccessible = reporterAccessible;
    }

    public String getCclistAccessible() {
        return cclistAccessible;
    }

    public void setCclistAccessible(String cclistAccessible) {
        this.cclistAccessible = cclistAccessible;
    }

    public String getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(String classificationId) {
        this.classificationId = classificationId;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRepPlatform() {
        return repPlatform;
    }

    public void setRepPlatform(String repPlatform) {
        this.repPlatform = repPlatform;
    }

    public String getOpSys() {
        return opSys;
    }

    public void setOpSys(String opSys) {
        this.opSys = opSys;
    }

    public Integer getDupId() {
        return dupId;
    }

    public void setDupId(Integer dupId) {
        this.dupId = dupId;
    }

    public String getBugFileLoc() {
        return bugFileLoc;
    }

    public void setBugFileLoc(String bugFileLoc) {
        this.bugFileLoc = bugFileLoc;
    }

    public String getStatusWhiteboard() {
        return statusWhiteboard;
    }

    public void setStatusWhiteboard(String statusWhiteboard) {
        this.statusWhiteboard = statusWhiteboard;
    }

    public String getTargetMilestone() {
        return targetMilestone;
    }

    public void setTargetMilestone(String targetMilestone) {
        this.targetMilestone = targetMilestone;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public String getEverconfirmed() {
        return everconfirmed;
    }

    public void setEverconfirmed(String everconfirmed) {
        this.everconfirmed = everconfirmed;
    }

    public String getQaContact() {
        return qaContact;
    }

    public void setQaContact(String qaContact) {
        this.qaContact = qaContact;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getActualTime() {
        return actualTime;
    }

    public void setActualTime(String actualTime) {
        this.actualTime = actualTime;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getGroupBugzilla() {
        return groupBugzilla;
    }

    public void setGroupBugzilla(String groupBugzilla) {
        this.groupBugzilla = groupBugzilla;
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IssueExtBugzilla)) {
            return false;
        }
        IssueExtBugzilla other = (IssueExtBugzilla) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.edu.utfpr.cm.minerador.model.issue.IssuesExtBugzilla[ id=" + id + " ]";
    }

}
