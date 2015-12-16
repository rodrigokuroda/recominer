package br.edu.utfpr.recominer.batch.bicho;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class IssueTracker {

    private final String project;
    private final String url;
    private final String user;
    private final String password;
    private final String token;
    private final IssueTrackerSystem issueTrackerSystem;

    public IssueTracker(String project, String url, String user, String password, final IssueTrackerSystem issueTrackerSystem) {
        this.project = project;
        this.url = url;
        this.user = user;
        this.password = password;
        this.token = null;
        this.issueTrackerSystem = issueTrackerSystem;
    }

    public IssueTracker(String project, String url, String token, final IssueTrackerSystem issueTrackerSystem) {
        this.project = project;
        this.url = url;
        this.user = null;
        this.password = null;
        this.token = token;
        this.issueTrackerSystem = issueTrackerSystem;
    }

    public String getProject() {
        return project;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public IssueTrackerSystem getIssueTrackerSystem() {
        return issueTrackerSystem;
    }

}
