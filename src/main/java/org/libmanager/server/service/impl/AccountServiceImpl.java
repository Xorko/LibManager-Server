package org.libmanager.server.service.impl;

import java.util.Optional;

import org.libmanager.server.entity.User;
import org.libmanager.server.response.AuthenticatedUser;
import org.libmanager.server.response.Response;
import org.libmanager.server.repository.UserRepository;
import org.libmanager.server.service.AccountService;
import org.libmanager.server.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
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
