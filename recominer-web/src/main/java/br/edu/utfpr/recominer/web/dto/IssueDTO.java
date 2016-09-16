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

    public IssueDTO() {
    }

    public IssueDTO(Issue issue) {
        this.id = issue.getId();
        this.key = issue.getKey();
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
}
