package br.edu.utfpr.recominer.model.svn;

import java.io.Serializable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class FileCopy implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer toId;
    private Integer fromId;
    private Integer fromCommitId;
    private String newFileName;
    private Integer actionId;

    public FileCopy() {
    }

    public FileCopy(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getFromCommitId() {
        return fromCommitId;
    }

    public void setFromCommitId(Integer fromCommitId) {
        this.fromCommitId = fromCommitId;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    public Integer getActionId() {
        return actionId;
    }

    public void setActionId(Integer actionId) {
        this.actionId = actionId;
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
        if (!(object instanceof FileCopy)) {
            return false;
        }
        FileCopy other = (FileCopy) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.edu.utfpr.cm.minerador.model.issue.FileCopies[ id=" + id + " ]";
    }
    
}
