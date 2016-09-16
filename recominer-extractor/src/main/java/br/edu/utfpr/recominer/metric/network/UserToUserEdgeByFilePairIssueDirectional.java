package br.edu.utfpr.recominer.metric.network;

import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class UserToUserEdgeByFilePairIssueDirectional extends UserToUserEdgeByFilePairDirectional {

    private final Integer issue;

    public UserToUserEdgeByFilePairIssueDirectional(String user, String fileName, String fileName2, String user2, Integer issue, int weight) {
        super(user, fileName, fileName2, user2, weight);
        this.issue = issue;
    }

    public UserToUserEdgeByFilePairIssueDirectional(String login, String email, String login2, String email2, String fileName, String fileName2, Integer issue, int weight) {
        this(login == null || login.isEmpty() ? email : login,
                login2 == null || login2.isEmpty() ? email2 : login2,
                fileName,
                fileName2,
                issue,
                weight);
    }

    public Integer getIssue() {
        return issue;
    }

    @Override
    public String toString() {
        return super.toString() + ";" + issue;
    }

    @Override
    public int hashCode() {
        int hash = 7 + super.hashCode();
        hash = 11 * hash + Objects.hashCode(this.issue);
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
        final UserToUserEdgeByFilePairIssueDirectional other = (UserToUserEdgeByFilePairIssueDirectional) obj;
        if (!Objects.equals(this.issue, other.issue)) {
            return false;
        }
        return super.equals(obj);
    }

}
