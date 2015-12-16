package br.edu.utfpr.recominer.model.issue;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.MultitenantType;
import org.eclipse.persistence.annotations.TenantTableDiscriminator;
import org.eclipse.persistence.annotations.TenantTableDiscriminatorType;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 *
 * @author Douglas
 */
@Entity
@Table(name = "people")
@Multitenant(value = MultitenantType.TABLE_PER_TENANT, includeCriteria = true)
@TenantTableDiscriminator(type = TenantTableDiscriminatorType.SCHEMA, contextProperty = PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT)
public class PeopleIssue implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @Column(name = "user_id")
    private String userId;

    public PeopleIssue() {
    }

    public PeopleIssue(Integer id) {
        this.id = id;
    }

    public PeopleIssue(Integer id, String userId) {
        this.id = id;
        this.userId = userId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        if (!(object instanceof PeopleIssue)) {
            return false;
        }
        PeopleIssue other = (PeopleIssue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.edu.utfpr.cm.minerador.model.issue.People[ id=" + id + " ]";
    }

}
