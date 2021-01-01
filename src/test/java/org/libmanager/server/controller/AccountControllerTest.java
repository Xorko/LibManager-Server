package org.libmanager.server.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.libmanager.server.response.AuthenticatedUser;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.AccountService;
import org.libmanager.server.util.TokenUtil;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class Login {

        private final String uri = "/account/login";
        private Response<AuthenticatedUser> response;

        @BeforeEach
        public void init() {
            AuthenticatedUser authenticatedUser = new AuthenticatedUser();
            authenticatedUser.setUsername("Foo");
            authenticatedUser.setToken("Foo");
            authenticatedUser.setBirthday(LocalDate.EPOCH.toString());
            authenticatedUser.setRegistrationDate(LocalDate.EPOCH.toString());
            authenticatedUser.setAdmin(false);

            response = new Response<>(Response.Code.OK, authenticatedUser);

            when(accountService.login("Foo", "Foo")).thenReturn(response);
        }

        @Test
        @DisplayName("Returns OK if credentials are valid")
        public void login_ShouldReturnOK_whenCredentialsAreValid() throws Exception {
            mockMvc.perform(post(uri)
                    .param("username", "Foo")
                    .param("password", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
        }

        @Test
        @DisplayName("Returns INVALID_PASSWORD if password is invalid")
        public void login_ShouldReturnInvalidPassword_whenPasswordIsInvalid() throws Exception {
            response.setCode(Response.Code.INVALID_PASSWORD);

            mockMvc.perform(post(uri)
                    .param("username", "Foo")
                    .param("password", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value(Response.Code.INVALID_PASSWORD.toString()));
        }

        @Test
        @DisplayName("Returns NOT_FOUND if username doesn't exists")
        public void login_ShouldReturnNotFound_whenUsernameDoesNotExist() throws Exception {
            response.setCode(Response.Code.NOT_FOUND);

            mockMvc.perform(post(uri)
                    .param("username", "Foo")
                    .param("password", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
        }

    }

    @Nested
    class PasswordResetMail {

        private final String uri = "/account/reset_password";

        @Test
        @DisplayName("Returns OK if mail is sent")
        public void passwordResetMail_ShouldReturnOK_whenMailIsSent() throws Exception {
            when (accountService.sendResetPasswordMail("Foo")).thenReturn(true);

            mockMvc.perform(get(uri)
                    .param("username", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
        }

        @Test
        @DisplayName("Returns NOT_FOUND if user is not found")
        public void passwordResetMail_ShouldReturnNotFound_whenUserNotFound() throws Exception {
            when (accountService.sendResetPasswordMail("Foo")).thenReturn(false);

            mockMvc.perform(get(uri)
                    .param("username", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
        }

    }

    @Nested
    class PasswordResetEdit {

        private final String uri = "/account/reset_password";

        @Test
        @DisplayName("Returns OK if the mail token is valid")
        public void passwordResetEdit_ShouldReturnOK_whenMailTokenIsValid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isMailToken("Foo")).thenReturn(true);
                when(accountService.resetPassword("Foo", "Foo")).thenReturn(true);

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("password", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Returns INVALID_TOKEN if the mail token is invalid")
        public void passwordResetEdit_ShouldReturnInvalidMailToken_whenMailTokenIsInValid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isMailToken("Foo")).thenReturn(false);
                when(accountService.resetPassword("Foo", "Foo")).thenReturn(false);

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("password", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_MAIL_TOKEN.toString()));
            }
        }

    }

}
