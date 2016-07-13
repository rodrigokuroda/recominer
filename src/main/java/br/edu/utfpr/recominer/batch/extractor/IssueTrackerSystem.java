package br.edu.utfpr.recominer.batch.extractor;

/**
 *
 * @author Rodrigo T. Kuroda
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
