package br.edu.utfpr.recominer.model.issue;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class Tracker implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String url;
    private int type;
    private Date retrievedOn;

    public Tracker() {
    }

    public Tracker(Integer id) {
        this.id = id;
    }

    public Tracker(Integer id, String url, int type, Date retrievedOn) {
        this.id = id;
        this.url = url;
        this.type = type;
        this.retrievedOn = retrievedOn;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getRetrievedOn() {
        return retrievedOn;
    }

    public void setRetrievedOn(Date retrievedOn) {
        this.retrievedOn = retrievedOn;
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
        if (!(object instanceof Tracker)) {
            return false;
        }
        Tracker other = (Tracker) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.edu.utfpr.cm.minerador.model.issue.Trackers[ id=" + id + " ]";
    }

}
