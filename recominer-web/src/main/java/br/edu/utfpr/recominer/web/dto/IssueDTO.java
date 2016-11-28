package br.edu.utfpr.recominer.web.dto;

import br.edu.utfpr.recominer.core.model.Issue;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class IssueDTO {

    private Integer id;
    private ProjectDTO project;
    private String key;
    private String summary;
    private String description;
    private FeedbackJustificationDTO feedback;

    public IssueDTO() {
    }

    public IssueDTO(Issue issue) {
        this.id = issue.getId();
        this.key = issue.getKey();
        this.summary = issue.getSummary();
        this.description = issue.getDescription();
        this.feedback = FeedbackJustificationDTO.from(issue.getFeedbackJustification());
    }

    public Issue toEntity() {
        Issue issue = new Issue(id);
        return issue;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Issue getIssue() {
        return new Issue(id);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public FeedbackJustificationDTO getFeedback() {
        return feedback;
    }

    public void setFeedback(FeedbackJustificationDTO feedback) {
        this.feedback = feedback;
    }
    
}
