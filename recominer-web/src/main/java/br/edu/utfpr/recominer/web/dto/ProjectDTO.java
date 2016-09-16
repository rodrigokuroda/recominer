package br.edu.utfpr.recominer.web.dto;

import br.edu.utfpr.recominer.core.model.Project;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class ProjectDTO {

    private Integer id;
    private String name;

    public ProjectDTO() {
    }

    public ProjectDTO(Project project) {
        this.id = project.getId();
        this.name = project.getProjectName();
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
}
