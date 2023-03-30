package client.shared;

import javax.inject.Inject;

public class CredentialsStore {

    private String username = null;
    private String password = null;
    private String url = null;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return isAdmin() ? username + "_admin" : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void removePassword() {
        password = null;
    }

    public boolean isAdmin() {
        return password != null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Inject
    public CredentialsStore() {
    }

}
