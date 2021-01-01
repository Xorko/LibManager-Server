package org.libmanager.server.service.impl;

import java.util.Optional;

import org.libmanager.server.entity.User;
import org.libmanager.server.response.AuthenticatedUser;
import org.libmanager.server.response.Response;
import org.libmanager.server.repository.UserRepository;
import org.libmanager.server.service.AccountService;
import org.libmanager.server.util.DateUtil;
import org.libmanager.server.util.TokenUtil;
import org.mindrot.jbcrypt.BCrypt;
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
        Optional<User> foundUser = userRepository.findById(username);
        if (foundUser.isPresent()) {
            User user = foundUser.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                authenticatedUser.setValid(true);
                authenticatedUser.setUsername(user.getUsername());
                authenticatedUser.setToken(TokenUtil.generateToken(user.getUsername(), user.isAdmin()));
                authenticatedUser.setAdmin(user.isAdmin());
                authenticatedUser.setBirthday(DateUtil.format(user.getBirthday()));
                authenticatedUser.setRegistrationDate(DateUtil.format(user.getRegistrationDate()));
                return new Response<>(Response.Code.OK, authenticatedUser);
            }
            return new Response<>(Response.Code.INVALID_PASSWORD, authenticatedUser);
        }
        return new Response<>(Response.Code.NOT_FOUND, authenticatedUser);

    }

    /**
     * {@inheritDoc}
     */
    public boolean resetPassword(String token, String password) {
        if (TokenUtil.isMailToken(token)) {
            if (TokenUtil.isValid(token)) {
                String username = TokenUtil.extractUsername(token);
                if (username != null) {
                    Optional<User> foundUser = userRepository.findById(username);
                    if (foundUser.isPresent()) {
                        User user = foundUser.get();
                        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                        userRepository.save(user);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean sendResetPasswordMail(String username) {
        Optional<User> foundUser = userRepository.findById(username);

        if (foundUser.isPresent()) {
            User user = foundUser.get();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@libmanager.org");
            message.setTo(user.getEmail());
            message.setSubject("Password reset");
            message.setText("Please enter the following token in the token field: " + TokenUtil.generateMailToken(username));

            mailSender.send(message);
            return true;
        }
        return false;
    }

}
