package br.edu.utfpr.recominer.model.issue;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class Issue implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private int trackerId;
    private String issue;
    private String type;
    private String summary;
    private String description;
    private String status;
    private String resolution;
    private String priority;
    private int submittedBy;
    private Date submittedOn;
    private int assignedTo;

    public Issue() {
    }

    public Issue(Integer id) {
        this.id = id;
    }

    public Issue(Integer id, int trackerId, String issue, String summary, String description, String status, int submittedBy, Date submittedOn, int assignedTo) {
        this();
        this.id = id;
        this.trackerId = trackerId;
        this.issue = issue;
        this.summary = summary;
        this.description = description;
        this.status = status;
        this.submittedBy = submittedBy;
        this.submittedOn = submittedOn;
        this.assignedTo = assignedTo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(int trackerId) {
        this.trackerId = trackerId;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(int submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Date getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(Date submittedOn) {
        this.submittedOn = submittedOn;
    }

    public int getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(int assignedTo) {
        this.assignedTo = assignedTo;
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
        if (!(object instanceof Issue)) {
            return false;
        }
        Issue other = (Issue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.edu.utfpr.cm.minerador.model.issue.Issues[ id=" + id + " ]";
    }

}
