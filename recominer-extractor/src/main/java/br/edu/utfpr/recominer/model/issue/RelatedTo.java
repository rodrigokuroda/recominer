package br.edu.utfpr.recominer.model.issue;

import java.io.Serializable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class RelatedTo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private int issueId;
    private int relatedTo;
    private String type;

    public RelatedTo() {
    }

    public RelatedTo(Integer id) {
        this.id = id;
    }

    public RelatedTo(Integer id, int issueId, int relatedTo, String type) {
        this.id = id;
        this.issueId = issueId;
        this.relatedTo = relatedTo;
        this.type = type;
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

    public int getRelatedTo() {
        return relatedTo;
    }

    public void setRelatedTo(int relatedTo) {
        this.relatedTo = relatedTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RelatedTo)) {
            return false;
        }
        RelatedTo other = (RelatedTo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.edu.utfpr.cm.minerador.model.issue.RelatedTo[ id=" + id + " ]";
    }

}
