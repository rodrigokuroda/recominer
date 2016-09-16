package br.edu.utfpr.recominer.model.issue;

import java.io.Serializable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class IssueWatcher implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private int issueId;
    private int personId;

    public IssueWatcher() {
    }

    public IssueWatcher(Integer id) {
        this.id = id;
    }

    public IssueWatcher(Integer id, int issueId, int personId) {
        this.id = id;
        this.issueId = issueId;
        this.personId = personId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
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
        if (!(object instanceof IssueWatcher)) {
            return false;
        }
        IssueWatcher other = (IssueWatcher) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.edu.utfpr.cm.minerador.model.issue.IssuesWatchers[ id=" + id + " ]";
    }

}
