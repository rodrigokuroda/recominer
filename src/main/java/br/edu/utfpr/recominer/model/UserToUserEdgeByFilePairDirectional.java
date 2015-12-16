package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.util.Util;
import java.util.Objects;

/**
 *
 * @author douglas
 */
public class UserToUserEdgeByFilePairDirectional {

    private final String user;
    private final String user2;
    private final String fileName;
    private final String fileName2;
    private int weight;

    public UserToUserEdgeByFilePairDirectional(String user, String fileName, String fileName2, String user2, int weight) {
        this.user = user;
        this.user2 = user2;
        this.fileName = fileName;
        this.fileName2 = fileName2;
        this.weight = weight;
    }

    public UserToUserEdgeByFilePairDirectional(String login, String email, String login2, String email2, String fileName, String fileName2, int weight) {
        this(login == null || login.isEmpty() ? email : login,
                login2 == null || login2.isEmpty() ? email2 : login2,
                fileName,
                fileName2,
                weight);
    }

    public String getUser() {
        return user;
    }

    public String getUser2() {
        return user2;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileName2() {
        return fileName2;
    }

    public int inc() {
        return weight++;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof UserToUserEdgeByFilePairDirectional) {
            UserToUserEdgeByFilePairDirectional other = (UserToUserEdgeByFilePairDirectional) obj;
            if (userEquals(other) && fileEquals(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.user);
        hash = 23 * hash + (Objects.hashCode(this.fileName) + Objects.hashCode(this.fileName2));
        hash = 23 * hash + Objects.hashCode(this.user2);
        return hash;
    }

    @Override
    public String toString() {
        return user + ";" + fileName + ";" + fileName2 + ";" + user2 + ";" + weight;
    }

    public boolean fileEquals(UserToUserEdgeByFilePairDirectional other) {
        if ((Util.stringEquals(this.fileName, other.fileName) && Util.stringEquals(this.fileName2, other.fileName2))
                || (Util.stringEquals(this.fileName, other.fileName2) && Util.stringEquals(this.fileName2, other.fileName))) {
            return true;
        }
        return false;
    }

    public boolean userEquals(UserToUserEdgeByFilePairDirectional other) {
        if ((Util.stringEquals(this.user, other.user) && Util.stringEquals(this.user2, other.user2))) {
            return true;
        }
        return false;
    }
}
