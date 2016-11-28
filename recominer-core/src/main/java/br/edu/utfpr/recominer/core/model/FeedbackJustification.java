package br.edu.utfpr.recominer.core.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class FeedbackJustification implements Persistable<Integer> {
    
    private Integer id;
    private Issue issue;
    private String justification;
    private List<Cochange> cochanges;
    private Date submitDate;

    public FeedbackJustification(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return id == null || id == 0;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public List<Cochange> getCochanges() {
        return cochanges;
    }

    public void setCochanges(List<Cochange> cochanges) {
        this.cochanges = cochanges;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FeedbackJustification other = (FeedbackJustification) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
}
