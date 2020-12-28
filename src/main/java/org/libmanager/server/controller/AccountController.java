package org.libmanager.server.controller;

import org.libmanager.server.response.AuthenticatedUser;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * Log in a user
     * @param username  The username of the user who is logging in
     * @param password  The password of the user who is logging in
     * @return          A JSON response with OK code and the authenticated user if given credentials are valid,
     *                  ERROR code and a null AuthenticatedUser otherwise
     */
    @PostMapping(path = "/login")
    public @ResponseBody
    Response<AuthenticatedUser> login(String username, String password) {
        return accountService.login(username, password);
    }

    /**
     * Send an email with a token to reset its password to the user who requested a password reset
     * @param username  The username of the user who requested a password reset
     * @return          A JSON response with OK code and true if the mail was sent successfully, NOT_FOUND code and false
     *                  otherwise
     */
    @GetMapping(path = "/reset_password")
    public @ResponseBody
    Response<Boolean> passwordResetMail(
            @RequestParam String username
    ) {
        return accountService.sendResetPasswordMail(username);
    }

    /**
     * Change the password of the owner of the given token
     * @param token         The token sent by mail to the user
     * @param password      The new password of the user
     * @return              A JSON response with OK code and true if the password was successfully changed, INVALID_TOKEN
     *                      and false otherwise
     */
    @PostMapping(path = "/reset_password")
    public @ResponseBody
    Response<Boolean> passwordResetEdit(
            @RequestParam String token,
            @RequestParam String password
    ) {
        return accountService.resetPassword(token, password);
    }
}
