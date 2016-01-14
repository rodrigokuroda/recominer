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
    private final Integer delay;
    private final IssueTrackerSystem issueTrackerSystem;

    public IssueTracker(String project, String url, Integer delay, String user, String password, final IssueTrackerSystem issueTrackerSystem) {
        this.project = project;
        this.url = url;
        this.delay = delay;
        this.user = user;
        this.password = password;
        this.token = null;
        this.issueTrackerSystem = issueTrackerSystem;
    }

    public IssueTracker(String project, String url, Integer delay, String token, final IssueTrackerSystem issueTrackerSystem) {
        this.project = project;
        this.url = url;
        this.delay = delay;
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

    public Integer getDelay() {
        return delay;
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
