package org.libmanager.server.response;

/**
 * Used for login responses
 */
public class AuthenticatedUser {

    private boolean valid;
    private String username;
    private String token;
    private boolean admin;

    public AuthenticatedUser() { }

    public AuthenticatedUser(boolean valid, String username, String token, boolean admin) {
        this.valid = valid;
        this.username = username;
        this.token = token;
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
