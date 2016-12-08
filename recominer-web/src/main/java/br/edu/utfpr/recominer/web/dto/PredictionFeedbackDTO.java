package br.edu.utfpr.recominer.web.dto;

import br.edu.utfpr.recominer.core.model.PredictionFeedback;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class PredictionFeedbackDTO {
   
    private Integer id;
    private boolean changed;
    private String justification;

    public PredictionFeedbackDTO() {
    }

    public PredictionFeedbackDTO(Integer id, boolean changed, String justification) {
        this.id = id;
        this.changed = changed;
    }

    public PredictionFeedback toEntity(CochangeDTO dto, Date submitDate) {
        return new PredictionFeedback(id, dto.getId(), changed, justification, submitDate);
    }
    
    public static PredictionFeedbackDTO from(PredictionFeedback feedback) {
        return new PredictionFeedbackDTO(feedback.getId(), 
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
