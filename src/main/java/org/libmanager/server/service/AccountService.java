package org.libmanager.server.service;

import org.libmanager.server.model.AuthenticatedUser;
import org.libmanager.server.model.Response;

public interface AccountService {

    /**
     * Log in a user
     * @param username  The username
     * @param password  The password
     * @return          A JSON response with OK c
     */
    Response<AuthenticatedUser> login(String username, String password);

    /**
     * Reset the password of the user
     * @param token     User's token received by email
     * @param password  New password of the user
     * @return          A JSON response with OK code if the password was modified, ERROR otherwise
     */
    Response<Boolean> resetPassword(String token, String password);

    /**
     * Send an email to the user with the token to reset his password
     * @param username  The user who requested a password reset
     * @return          A JSON response with OK code if the mail was sent, ERROR otherwise
     */
    Response<Boolean> sendResetPasswordMail(String username);

}
