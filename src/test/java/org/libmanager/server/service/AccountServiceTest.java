package org.libmanager.server.service;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.libmanager.server.entity.User;
import org.libmanager.server.repository.UserRepository;
import org.libmanager.server.response.AuthenticatedUser;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.impl.AccountServiceImpl;
import org.libmanager.server.util.TokenUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private final AccountService accountService = new AccountServiceImpl();

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("Foo");
        user.setPassword("Foo");
        user.setEmail("Foo");
        user.setFirstName("Foo");
        user.setLastName("Foo");
        user.setAddress("Foo");
        user.setRegistrationDate(LocalDate.EPOCH);
        user.setBirthday(LocalDate.EPOCH);
    }

    @Nested
    class Login {

        @Test
        @DisplayName("Returns OK when credentials are correct")
        public void login_shouldReturnOK_whenCredentialsAreCorrect() {
            try (MockedStatic<BCrypt> mockedBcrypt = mockStatic(BCrypt.class)) {
                mockedBcrypt.when(() -> BCrypt.checkpw("Foo", "Foo")).thenReturn(true);
                when(userRepository.findById("Foo")).thenReturn(Optional.of(user));

                Response<AuthenticatedUser> response = accountService.login("Foo", "Foo");

                assertThat(response.getCode()).isEqualTo(Response.Code.OK);
            }
        }

        @Test
        @DisplayName("Returns AuthenticatedUser with valid = true when credentials are correct")
        public void login_shouldReturnValidAsTrue_whenCredentialsAreCorrect() {
            try (MockedStatic<BCrypt> mockedBcrypt = mockStatic(BCrypt.class)) {
                mockedBcrypt.when(() -> BCrypt.checkpw("Foo", "Foo")).thenReturn(true);
                when(userRepository.findById("Foo")).thenReturn(Optional.of(user));

                Response<AuthenticatedUser> response = accountService.login("Foo", "Foo");

                assertThat(response.getContent().isValid()).isEqualTo(true);
            }
        }

        @Test
        @DisplayName("Returns AuthenticatedUser with completed fields when credentials are correct")
        public void login_shouldReturnAuthenticatedUserWithCompletedFields_whenCredentialsAreCorrect() {
            try (MockedStatic<BCrypt> mockedBcrypt = mockStatic(BCrypt.class)) {
                mockedBcrypt.when(() -> BCrypt.checkpw("Foo", "Foo")).thenReturn(true);
                when(userRepository.findById("Foo")).thenReturn(Optional.of(user));

                Response<AuthenticatedUser> response = accountService.login("Foo", "Foo");

                assertThat(response.getContent().isValid()).isNotNull();
                assertThat(response.getContent().getUsername()).isNotNull();
                assertThat(response.getContent().getToken()).isNotNull();
                assertThat(response.getContent().getBirthday()).isNotNull();
                assertThat(response.getContent().getRegistrationDate()).isNotNull();
            }
        }

        @Test
        @DisplayName("Returns INVALID_PASSWORD when password is incorrect")
        public void login_shouldReturnInvalidPassword_whenPasswordIsIncorrect() {
            try (MockedStatic<BCrypt> mockedBcrypt = mockStatic(BCrypt.class)) {
                mockedBcrypt.when(() -> BCrypt.checkpw("Foo", "Foo")).thenReturn(false);
                when(userRepository.findById("Foo")).thenReturn(Optional.of(user));

                Response<AuthenticatedUser> response = accountService.login("Foo", "Foo");

                assertThat(response.getCode()).isEqualTo(Response.Code.INVALID_PASSWORD);
            }
        }

        @Test
        @DisplayName("Returns NOT_FOUND when user is not found by its username")
        public void login_shouldReturnNotFound_whenUserIsNotFoundByItsUsername() {
            when(userRepository.findById("Foo")).thenReturn(Optional.empty());

            Response<AuthenticatedUser> response = accountService.login("Foo", "Foo");

            assertThat(response.getCode()).isEqualTo(Response.Code.NOT_FOUND);
        }

    }

    @Nested
    class ResetPassword {

        @Test
        @DisplayName("Returns true if token is correct and password is changed")
        public void resetPassword_shouldReturnTrue_whenPasswordWasChanged() {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isMailToken("Foo")).thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo")).thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.extractUsername("Foo")).thenReturn("Foo");
                when(userRepository.findById("Foo")).thenReturn(Optional.of(user));


                boolean result = accountService.resetPassword("Foo", "Foo");

                assertThat(result).isTrue();
            }
        }

        @Test
        @DisplayName("Returns false if token is not mail token")
        public void resetPassword_shouldReturnFalse_whenTokenIsNotMailToken() {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isMailToken("Foo")).thenReturn(false);

                boolean result = accountService.resetPassword("Foo", "Foo");

                assertThat(result).isFalse();
            }
        }

        @Test
        @DisplayName("Returns false if token is invalid")
        public void resetPassword_shouldReturnFalse_whenTokenIsInvalid() {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isMailToken("Foo")).thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo")).thenReturn(false);

                boolean result = accountService.resetPassword("Foo", "Foo");

                assertThat(result).isFalse();
            }
        }

        @Test
        @DisplayName("Returns false if user is not found")
        public void resetPassword_shouldReturnFalse_whenUserIsNotFound() {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isMailToken("Foo")).thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo")).thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.extractUsername("Foo")).thenReturn("Foo");
                when(userRepository.findById("Foo")).thenReturn(Optional.empty());

                boolean result = accountService.resetPassword("Foo", "Foo");

                assertThat(result).isFalse();
            }
        }

    }

    @Nested
    class SendResetPasswordMail {

        @Test
        @DisplayName("Returns true if user is found and mail is sent")
        public void sendResetPasswordMail_shouldReturnTrue_whenUserIsFoundAndMailIsSent() {
            when(userRepository.findById("Foo")).thenReturn(Optional.of(user));

            boolean result = accountService.sendResetPasswordMail("Foo");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Returns false if user is not found")
        public void sendResetPasswordMail_shouldReturnFalse_whenUserIsNotFound() {
            when(userRepository.findById("Foo")).thenReturn(Optional.empty());

            boolean result = accountService.sendResetPasswordMail("Foo");

            assertThat(result).isFalse();
        }

    }

}
