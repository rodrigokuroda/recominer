package br.edu.utfpr.recominer.model.matrix;

import br.edu.utfpr.recominer.model.InterfaceEntity;
import br.edu.utfpr.recominer.model.Startable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author douglas
 */
@Entity
@Table(name = "matrix")
@NamedQueries({
    @NamedQuery(name = "Matrix.findAllTheLatest", query = "SELECT m FROM EntityMatrix m ORDER BY m.id DESC")
})
public class EntityMatrix implements InterfaceEntity, Startable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date started;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date stoped;
    private boolean complete;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String log;
    private Map<Object, Object> params;
    private String classServicesName;
    private String repository;
    @OrderColumn(name = "index")
    @OneToMany(mappedBy = "matrix", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EntityMatrixNode> nodes;
    @Transient
    private String additionalFilename;

    public EntityMatrix() {
        started = new Date();
        complete = false;
        nodes = new ArrayList<>();
        params = new HashMap<>();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getStoped() {
        return stoped;
    }

    public void setStoped(Date stoped) {
        this.stoped = stoped;
    }

    public Map<Object, Object> getParams() {
        return params;
    }

    @Override
    public String getLog() {
        return log;
    }

    @Override
    public void setLog(String log) {
        this.log = log;
    }

    public List<EntityMatrixNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<EntityMatrixNode> nodes) {
        this.nodes = nodes;
    }

    public String getClassServicesName() {
        return classServicesName;
    }

    public String getClassServicesSingleName() {
        if (classServicesName != null) {
            String[] tokens = classServicesName.split("\\.");
            if (tokens.length > 0) {
                return tokens[tokens.length - 1];
            }
        }
        return classServicesName;
    }

    public void setClassServicesName(String classServices) {
        this.classServicesName = classServices;
    }

    public String getAdditionalFilename() {
        return additionalFilename;
    }

    public void setAdditionalFilename(String additionalFilename) {
        this.additionalFilename = additionalFilename;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EntityMatrix)) {
            return false;
        }
        EntityMatrix other = (EntityMatrix) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final Object filename = params.get("filename");
        if (filename != null) {
            final Object additionalFilename = params.get("additionalFilename");
            if (additionalFilename != null) {
                return StringUtils.capitalize(repository) + " " + filename + additionalFilename;
            }
            return StringUtils.capitalize(repository) + " " + filename;
        } else {
            return "(" + id + ") " + repository + " - " + getClassServicesSingleName();
        }
    }

    @Override
    public String getDownloadFileName() {
        final Object filename = params.get("filename");
        if (filename != null) {

            final Object additionalFilename = params.get("additionalFilename");
            if (additionalFilename != null) {
                return StringUtils.capitalize(repository) + " " + filename + additionalFilename;
            }
            return StringUtils.capitalize(repository) + " " + filename;
        } else {
            return this.repository + "-" + this.started;
        }
    }

    public void addAllParams(Map<Object, Object> params) {
        this.params.putAll(params);
    }

}
