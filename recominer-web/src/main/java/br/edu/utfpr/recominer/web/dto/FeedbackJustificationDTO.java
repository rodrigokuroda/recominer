package br.edu.utfpr.recominer.web.dto;

import br.edu.utfpr.recominer.core.model.FeedbackJustification;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class FeedbackJustificationDTO {

    private Integer id;
    private String technique;
    private String justification;
    private List<CochangeDTO> cochanges;

    public static FeedbackJustificationDTO from(FeedbackJustification feedbackJustification) {
        FeedbackJustificationDTO dto = new FeedbackJustificationDTO();
        dto.setId(feedbackJustification.getId());
        dto.setJustification(feedbackJustification.getJustification());
        dto.setTechnique(feedbackJustification.getTechnique());
        return dto;
    }

    public FeedbackJustification toEntity(IssueDTO issue) {
        FeedbackJustification feedbackJustification = new FeedbackJustification(id, technique);
        feedbackJustification.setJustification(justification);
        feedbackJustification.setIssue(issue.toEntity());
        feedbackJustification.setSubmitDate(new Date());
        return feedbackJustification;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<CochangeDTO> getCochanges() {
        return cochanges;
    }

    public void setCochanges(List<CochangeDTO> cochanges) {
        this.cochanges = cochanges;
    }

    public String getTechnique() {
        return technique;
    }

    public void setTechnique(String technique) {
        this.technique = technique;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

}
