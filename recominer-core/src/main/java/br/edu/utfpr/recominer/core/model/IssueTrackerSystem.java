package br.edu.utfpr.recominer.core.model;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public enum IssueTrackerSystem {
    BUGZILLA("bg"), JIRA("jira"), GITHUB("github");

    private final String code;

    private IssueTrackerSystem(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
