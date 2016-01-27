package br.edu.utfpr.recominer.model.issue;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(name = "issues_scmlog",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"scmlog_id", "issue_id"}, name = "unq_issue_scmlog")
        },
        indexes = {
            @Index(columnList = "scmlog_id,issue_id", unique = true, name = "unq_issue_scmlog")
        }
)
@Multitenant(value = MultitenantType.TABLE_PER_TENANT, includeCriteria = true)
@TenantTableDiscriminator(type = TenantTableDiscriminatorType.SCHEMA, contextProperty = PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT)
public class IssueScmlog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

//    @ManyToOne
//    @JoinColumn(name = "issue_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Column(name = "issue_id")
    private Integer issue;

//    @ManyToOne
//    @JoinColumn(name = "scmlog_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Column(name = "scmlog_id")
    private Integer scmlog;

    public IssueScmlog() {
    }

    public IssueScmlog(Integer scmlog, Integer issue) {
        setIssue(issue);
        setScmlog(scmlog);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIssue() {
        return issue;
    }

    public void setIssue(Integer issue) {
        this.issue = issue;
    }

    public Integer getScmlog() {
        return scmlog;
    }

    public void setScmlog(Integer scmlog) {
        this.scmlog = scmlog;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.scmlog);
        hash = 13 * hash + Objects.hashCode(this.issue);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IssueScmlog other = (IssueScmlog) obj;
        if (!Objects.equals(this.scmlog, other.scmlog)) {
            return false;
        }
        if (!Objects.equals(this.issue, other.issue)) {
            return false;
        }
        return true;
    }

}
