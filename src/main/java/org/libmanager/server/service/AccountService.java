package org.libmanager.server.service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.libmanager.server.entity.User;
import org.libmanager.server.model.AuthenticatedUser;
import org.libmanager.server.model.Response;
import org.libmanager.server.repository.UserRepository;
import org.libmanager.server.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Log in a user
     * @param username  The username
     * @param password  The password
     * @return          A JSON response with OK c
     */
    public Response<AuthenticatedUser> login(String username, String password) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        Optional<User> foundUser = userRepository.findUserByUsernameAndPassword(username, password);
        if (foundUser.isPresent()) {
            User user = foundUser.get();
            authenticatedUser.setValid(true);
            authenticatedUser.setUsername(user.getUsername());
            authenticatedUser.setToken(TokenUtil.generateToken(user.getUsername(), user.isAdmin()));
            authenticatedUser.setAdmin(user.isAdmin());
            return new Response<>(Response.Code.OK, authenticatedUser);
        }
        return new Response<>(Response.Code.ERROR, authenticatedUser);

    }

    /**
     * Reset the password of the user
     * @param token     User's token received by email
     * @param password  New password of the user
     * @return          A JSON response with OK code if the password was modified, ERROR otherwise
     */
    public Response<Boolean> resetPassword(String token, String password) {
        if (TokenUtil.isMailToken(token)) {
            if (TokenUtil.isValid(token)) {
                String username = TokenUtil.extractUsername(token);
                if (username != null) {
                    Optional<User> foundUser = userRepository.findById(username);
                    if (foundUser.isPresent()) {
                        User user = foundUser.get();
                        user.setPassword(password);
                        userRepository.save(user);
                        return new Response<>(Response.Code.OK, Boolean.TRUE);
                    }
                }
            }
        }
        return new Response<>(Response.Code.INVALID_TOKEN, Boolean.FALSE);
    }

    /**
     * Send an email to the user with the token to reset his password
     * @param username  The user who requested a password reset
     * @return          A JSON response with OK code if the mail was sent, ERROR otherwise
     */
    public Response<Boolean> sendResetPasswordMail(String username) {
        Optional<User> foundUser = userRepository.findById(username);

        if (foundUser.isPresent()) {
            User user = foundUser.get();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@libmanager.org");
            message.setTo(user.getEmail());
            message.setSubject("Password reset");
            message.setText("Please enter the following token in the token field: " + TokenUtil.generateMailToken(username));

            mailSender.send(message);
            return new Response<>(Response.Code.OK, true);
        }
        return new Response<>(Response.Code.NOT_FOUND, false);
    }

}
