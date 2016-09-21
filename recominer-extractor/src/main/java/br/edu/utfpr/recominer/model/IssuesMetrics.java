package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;
import java.sql.Timestamp;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class IssuesMetrics implements Persistable<Integer> {

    public static final String HEADER
            = "id;"
            + "issueId;"
            + "commitId;"
            + "issueKey;"
            + "issueType;"
            + "issuePriority;"
            + "issueAssignedTo;"
            + "issueSubmittedBy;"
            + "commenters;"
            + "devCommenters;"
            + "issueAge;"
            + "wordinessBody;"
            + "wordinessComments;"
            + "comments;"
            ;

    private Integer id;
    private Issue issue;
    private Commit commit;
    private String issueKey;
    private String issueType;
    private String priority;
    private String assignedTo;
    private String submittedBy;
    private Integer commenters;
    private Integer devCommenters;
    private Timestamp updatedOn;
    private Timestamp commentsUpdatedOn;
    private Integer age;
    private Long wordnessBody;
    private Long wordnessComments;
    private Integer comments;

    public IssuesMetrics(Issue issue, String issueKey,
            String issueType, String priority,
            String assignedTo, String submittedBy,
            Integer commenters, Integer devCommenters,
            Timestamp updatedOn, Timestamp commentsUpdatedOn,
            Integer issueAge, Long wordnessBody,
            Long wordnessComments, Integer numberOfComments, Commit commit) {
        this.id = null;
        this.issue = issue;
        this.issueKey = issueKey;
        this.issueType = issueType;
        this.priority = priority;
        this.assignedTo = assignedTo;
        this.submittedBy = submittedBy;
        this.commenters = commenters;
        this.devCommenters = devCommenters;
        this.updatedOn = updatedOn;
        this.age = issueAge;
        this.wordnessBody = wordnessBody;
        this.wordnessComments = wordnessComments;
        this.comments = numberOfComments;
        this.commentsUpdatedOn = commentsUpdatedOn;
        this.commit = commit;
    }

    public IssuesMetrics(Integer id, Issue issue, String issueKey,
            String issueType, String priority,
            String assignedTo, String submittedBy,
            Integer commenters, Integer devCommenters,
            Timestamp updatedOn, Timestamp commentsUpdatedOn,
            Integer issueAge, Long wordnessBody,
            Long wordnessComments, Integer numberOfComments, Commit commit) {
        this.id = id;
        this.issue = issue;
        this.issueKey = issueKey;
        this.issueType = issueType;
        this.priority = priority;
        this.assignedTo = assignedTo;
        this.submittedBy = submittedBy;
        this.commenters = commenters;
        this.devCommenters = devCommenters;
        this.updatedOn = updatedOn;
        this.age = issueAge;
        this.wordnessBody = wordnessBody;
        this.wordnessComments = wordnessComments;
        this.comments = numberOfComments;
        this.commentsUpdatedOn = commentsUpdatedOn;
        this.commit = commit;
    }

    public IssuesMetrics(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return id == null || id == 0;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Integer getCommenters() {
        return commenters;
    }

    public void setCommenters(Integer commenters) {
        this.commenters = commenters;
    }

    public Integer getDevCommenters() {
        return devCommenters;
    }

    public void setDevCommenters(Integer devCommenters) {
        this.devCommenters = devCommenters;
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Timestamp updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Timestamp getCommentsUpdatedOn() {
        return commentsUpdatedOn;
    }

    public void setCommentsUpdatedOn(Timestamp commentsUpdatedOn) {
        this.commentsUpdatedOn = commentsUpdatedOn;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getWordnessBody() {
        return wordnessBody;
    }

    public void setWordnessBody(Long wordnessBody) {
        this.wordnessBody = wordnessBody;
    }

    public Long getWordnessComments() {
        return wordnessComments;
    }

    public void setWordnessComments(Long wordnessComments) {
        this.wordnessComments = wordnessComments;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IssuesMetrics other = (IssuesMetrics) obj;

        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(";")
                .append(issue.getId()).append(";")
                .append(commit.getId()).append(";")
                .append(issueKey).append(";")
                .append(issueType).append(";")
                .append(priority).append(";")
                .append(assignedTo).append(";")
                .append(submittedBy).append(";")
                .append(commenters).append(";")
                .append(devCommenters).append(";")
                .append(age).append(";")
                .append(wordnessBody).append(";")
                .append(wordnessComments).append(";")
                .append(comments).append(";");
        
        return sb.toString();
    }

}
