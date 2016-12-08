package br.edu.utfpr.recominer.core.model;

import java.util.Date;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class PredictionFeedback implements Persistable<Integer> {
    
    private Integer id;
    private Integer predictionId;
    private boolean changed;
    private String justification;
    private Date submitDate;

    public PredictionFeedback() {
    }

    public PredictionFeedback(Integer id) {
        this.id = id;
    }

    public PredictionFeedback(Integer id, Integer predictionId, boolean changed, String justification, Date submitDate) {
        this.id = id;
        this.predictionId = predictionId;
        this.changed = changed;
        this.justification = justification;
        this.submitDate = submitDate;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
    
    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPredictionId() {
        return predictionId;
    }

    public void setPredictionId(Integer predictionId) {
        this.predictionId = predictionId;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.id);
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
        final PredictionFeedback other = (PredictionFeedback) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
}
