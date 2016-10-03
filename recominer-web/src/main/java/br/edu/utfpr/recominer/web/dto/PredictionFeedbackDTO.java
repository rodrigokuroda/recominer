package br.edu.utfpr.recominer.web.dto;

import br.edu.utfpr.recominer.core.model.PredictionFeedback;
import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class PredictionFeedbackDTO {
   
    private Integer id;
    private boolean changed;
    private String justification;
    private CochangeDTO cochange;

    public PredictionFeedbackDTO() {
    }

    public PredictionFeedbackDTO(Integer id, CochangeDTO cochange, boolean changed, String justification) {
        this.id = id;
        this.cochange = cochange;
        this.changed = changed;
        this.justification = justification;
    }

    public PredictionFeedback toEntity() {
        return new PredictionFeedback(id, cochange.getId(), changed, justification);
    }
    
    public static PredictionFeedbackDTO from(PredictionFeedback feedback) {
        return new PredictionFeedbackDTO(feedback.getId(), 
                new CochangeDTO(feedback.getPredictionId()), 
                feedback.isChanged(), 
                feedback.getJustification());
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public CochangeDTO getCochange() {
        return cochange;
    }

    public void setCochange(CochangeDTO cochange) {
        this.cochange = cochange;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final PredictionFeedbackDTO other = (PredictionFeedbackDTO) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
}
