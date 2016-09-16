package br.edu.utfpr.recominer.model.issue;

import java.io.Serializable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class SupportedTracker implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String version;

    public SupportedTracker() {
    }

    public SupportedTracker(Integer id) {
        this.id = id;
    }

    public SupportedTracker(Integer id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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
        if (!(object instanceof SupportedTracker)) {
            return false;
        }
        SupportedTracker other = (SupportedTracker) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.edu.utfpr.cm.minerador.model.issue.SupportedTrackers[ id=" + id + " ]";
    }

}
