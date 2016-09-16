package br.edu.utfpr.recominer.web.dto;

import br.edu.utfpr.recominer.core.model.Commit;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class CommitDTO {

    private Integer id;
    private ProjectDTO project;
    private IssueDTO issue;
    private String revision;

    public CommitDTO() {
    }

    public CommitDTO(Commit commit) {
        this.id = commit.getId();
        this.revision = commit.getRevision();
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

    public IssueDTO getIssue() {
        return issue;
    }

    public void setIssue(IssueDTO issue) {
        this.issue = issue;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Commit getCommit() {
        return new Commit(id);
    }

}
