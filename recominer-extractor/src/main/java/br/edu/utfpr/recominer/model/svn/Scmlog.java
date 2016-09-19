package br.edu.utfpr.recominer.model.svn;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class Scmlog implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String rev;
    private Integer committerId;
    private Integer authorId;
    private Date date;
    private Integer dateTz;
    private Date authorDate;
    private Integer authorDateTz;
    private String message;
    private Boolean composedRev;
    private Integer repositoryId;

    public Scmlog() {
    }

    public Scmlog(Integer id) {
        this.id = id;
    }

    public Scmlog(Integer id, Date date, String message) {
        this.id = id;
        this.date = date;
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public Integer getCommitterId() {
        return committerId;
    }

    public void setCommitterId(Integer committerId) {
        this.committerId = committerId;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getDateTz() {
        return dateTz;
    }

    public void setDateTz(Integer dateTz) {
        this.dateTz = dateTz;
    }

    public Date getAuthorDate() {
        return authorDate;
    }

    public void setAuthorDate(Date authorDate) {
        this.authorDate = authorDate;
    }

    public Integer getAuthorDateTz() {
        return authorDateTz;
    }

    public void setAuthorDateTz(Integer authorDateTz) {
        this.authorDateTz = authorDateTz;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getComposedRev() {
        return composedRev;
    }

    public void setComposedRev(Boolean composedRev) {
        this.composedRev = composedRev;
    }

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Scmlog)) {
            return false;
        }
        Scmlog other = (Scmlog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "commit: " + id + " message: " + message;
    }

}
