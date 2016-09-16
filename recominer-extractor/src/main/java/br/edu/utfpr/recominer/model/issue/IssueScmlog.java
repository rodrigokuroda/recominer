package br.edu.utfpr.recominer.model.issue;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class IssueScmlog implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer issue;
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
