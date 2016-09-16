package br.edu.utfpr.recominer.web.dto;

import br.edu.utfpr.recominer.core.model.File;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class FileDTO {

    private Integer id;
    private ProjectDTO project;
    private CommitDTO commit;
    private String name;

    public FileDTO() {
    }

    public FileDTO(File file) {
        this.id = file.getId();
        this.name = file.getFileName();
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

    public CommitDTO getCommit() {
        return commit;
    }

    public void setCommit(CommitDTO commit) {
        this.commit = commit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
