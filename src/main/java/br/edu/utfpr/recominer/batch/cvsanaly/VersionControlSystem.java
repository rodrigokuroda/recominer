package br.edu.utfpr.recominer.batch.cvsanaly;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class VersionControlSystem {

    private final String name;
    private final String url;
    private final String user;
    private final String password;

    public VersionControlSystem(String name, String url, String user, String password) {
        this.name = name;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getName() {
        return name;
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

}
