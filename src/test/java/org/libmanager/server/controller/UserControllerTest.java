package org.libmanager.server.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.libmanager.server.entity.User;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.UserService;
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

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class AddUser {

        private final String uri = "/user/add";

        @Test
        @DisplayName("Returns OK if params are correct and token is valid and is admin")
        public void addUser_shouldReturnOk_whenParamsAreCorrectAndTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(userService.add("Foo", "Foo", "Foo", "Foo", "Foo", "1970-01-01", "Foo")).thenReturn(true);

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("username", "Foo")
                        .param("firstName", "Foo")
                        .param("lastName", "Foo")
                        .param("address", "Foo")
                        .param("email", "Foo")
                        .param("birthday", "1970-01-01")
                        .param("password", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Returns MAX_USERS_LIMIT if limit is reached")
        public void addUser_shouldReturnMaxUsersReached_whenCorrectParamsAndLimitReached() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(userService.add("Foo", "Foo", "Foo", "Foo", "Foo", "1970-01-01", "Foo")).thenReturn(false);

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("username", "Foo")
                        .param("firstName", "Foo")
                        .param("lastName", "Foo")
                        .param("address", "Foo")
                        .param("email", "Foo")
                        .param("birthday", "1970-01-01")
                        .param("password", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.MAX_USERS_REACHED.toString()));
            }
        }

        @Test
        @DisplayName("Returns INVALID_TOKEN if token is invalid")
        public void addUser_shouldReturnInvalidToken_whenTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("username", "Foo")
                        .param("firstName", "Foo")
                        .param("lastName", "Foo")
                        .param("address", "Foo")
                        .param("email", "Foo")
                        .param("birthday", "1970-01-01")
                        .param("password", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

        @Test
        @DisplayName("Returns INSUFFICIENT_PERMISSIONS if token is not an admin token")
        public void addUser_shouldReturnInsufficientPermissions_whenTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("username", "Foo")
                        .param("firstName", "Foo")
                        .param("lastName", "Foo")
                        .param("address", "Foo")
                        .param("email", "Foo")
                        .param("birthday", "1970-01-01")
                        .param("password", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
            }
        }

    }

    @Nested
    class EditUser {

        private final String uri = "/user/edit/{username}";

        @Test
        @DisplayName("Returns OK if params are correct and token is valid and is admin")
        public void editUser_shouldReturnOk_whenCorrectParamsAndTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(userService.edit("Foo", "Foo", "Foo", "Foo", "Foo", "1970-01-01")).thenReturn(true);

                mockMvc.perform(post(uri, "Foo")
                        .param("token", "Foo")
                        .param("username", "Foo")
                        .param("firstName", "Foo")
                        .param("lastName", "Foo")
                        .param("address", "Foo")
                        .param("email", "Foo")
                        .param("birthday", "1970-01-01")
                        .param("password", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Returns NOT_FOUND if unknown username")
        public void editUser_shouldReturnNotFound_whenUnknownUsername() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(userService.edit("Foo", "Foo", "Foo", "Foo", "Foo", "1970-01-01")).thenReturn(false);

                mockMvc.perform(post(uri, "Foo")
                        .param("token", "Foo")
                        .param("username", "Foo")
                        .param("firstName", "Foo")
                        .param("lastName", "Foo")
                        .param("address", "Foo")
                        .param("email", "Foo")
                        .param("birthday", "1970-01-01")
                        .param("password", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
            }
        }

        @Test
        @DisplayName("Returns INVALID_TOKEN if token is invalid")
        public void editUser_shouldReturnInvalidToken_whenTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri, "Foo")
                        .param("token", "Foo")
                        .param("firstName", "Foo")
                        .param("lastName", "Foo")
                        .param("address", "Foo")
                        .param("email", "Foo")
                        .param("birthday", "1970-01-01")
                        .param("password", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

        @Test
        @DisplayName("Returns INSUFFICIENT_PERMISSIONS if token is not an admin token")
        public void editUser_shouldReturnInsufficientPermissions_whenTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri, "Foo")
                        .param("token", "Foo")
                        .param("firstName", "Foo")
                        .param("lastName", "Foo")
                        .param("address", "Foo")
                        .param("email", "Foo")
                        .param("birthday", "1970-01-01")
                        .param("password", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
            }
        }

    }


    @Nested
    class DeleteUser {

        private final String uri = "/user/delete/{username}";

        @Test
        @DisplayName("Returns OK if params are correct and token is valid and is admin")
        public void deleteUser_shouldReturnOk_whenCorrectParamsAndTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(userService.delete("Foo")).thenReturn(true);

                mockMvc.perform(post(uri, "Foo")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Returns NOT_FOUND if unknown username")
        public void deleteUser_shouldReturnNotFound_whenUnknownUsername() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(userService.delete("Foo")).thenReturn(false);

                mockMvc.perform(post(uri, "Foo")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
            }
        }

        @Test
        @DisplayName("Returns INVALID_TOKEN if token is invalid")
        public void deleteUser_shouldReturnInvalidToken_whenTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri, "Foo")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

        @Test
        @DisplayName("Returns INSUFFICIENT_PERMISSIONS if token is not an admin token")
        public void deleteUser_shouldReturnInsufficientPermissions_whenTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri, "Foo")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
            }
        }

    }

    @Nested
    class Getters {

        private User user;

        @BeforeEach
        public void setUp() {
            user = new User();
            user.setAdmin(false);
            user.setUsername("Foo");
            user.setFirstName("Foo");
            user.setLastName("Foo");
            user.setEmail("Foo");
            user.setPassword("Foo");
            user.setAddress("Foo");
            user.setBirthday(LocalDate.EPOCH);
            user.setRegistrationDate(LocalDate.EPOCH);
        }

        @Nested
        class GetUser {

            private final String uri = "/user/get/{username}";

            @BeforeEach
            public void setUp() {
                when(userService.get("Foo")).thenReturn(user);
                when(userService.get("Bar")).thenReturn(null);
            }

            @Test
            @DisplayName("Returns OK if token is valid and is an admin token")
            public void getUser_shouldReturnOK_whenCorrectParamsAndTokenIsValidAndIsAdmin() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(true);

                    mockMvc.perform(post(uri, "Foo")
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
                }
            }

            @Test
            @DisplayName("Returns INVALID_TOKEN if token is invalid")
            public void getUser_shouldReturnInvalidToken_whenTokenIsInvalid() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri, "Foo")
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
                }
            }

            @Test
            @DisplayName("Returns INSUFFICIENT_PERMISSIONS if token is not an admin token")
            public void getUser_shouldReturnInsufficientPermissions_whenTokenIsNotAdminToken() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri, "Foo")
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
                }
            }

            @Test
            @DisplayName("Returns the user")
            public void getUser_shouldReturnUser() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(true);

                    mockMvc.perform(post(uri, "Foo")
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.content").isNotEmpty())
                           .andExpect(jsonPath("$.content.username").exists())
                           .andExpect(jsonPath("$.content.firstName").exists())
                           .andExpect(jsonPath("$.content.lastName").exists())
                           .andExpect(jsonPath("$.content.email").exists())
                           .andExpect(jsonPath("$.content.birthday").exists())
                           .andExpect(jsonPath("$.content.registrationDate").exists())
                           .andExpect(jsonPath("$.content.admin").exists());
                }
            }

            @Test
            @DisplayName("Returns NOT_FOUND if not found")
            public void getUser_shouldReturnNotFound_whenNotFound() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(true);

                    mockMvc.perform(post(uri, "Bar")
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
                }
            }

            @Test
            @DisplayName("Content should be null if not found")
            public void getUserContent_shouldBeNull_whenNotFound() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(true);

                    mockMvc.perform(post(uri, "Bar")
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.content").isEmpty())
                           .andExpect(jsonPath("$.content.username").doesNotExist())
                           .andExpect(jsonPath("$.content.firstName").doesNotExist())
                           .andExpect(jsonPath("$.content.lastName").doesNotExist())
                           .andExpect(jsonPath("$.content.email").doesNotExist())
                           .andExpect(jsonPath("$.content.birthday").doesNotExist())
                           .andExpect(jsonPath("$.content.registrationDate").doesNotExist())
                           .andExpect(jsonPath("$.content.admin").doesNotExist());
                }
            }

        }

        @Nested
        class GetAllUsers {

            private final String uri = "/user/all";

            @BeforeEach
            public void setUp() {
                Iterable<User> userIterable = Arrays.asList(user, user, user);
                when(userService.getAll()).thenReturn(userIterable);
            }

            @Test
            @DisplayName("Returns OK if token is valid and is an admin token")
            public void getAllUsers_shouldReturnOk_whenCorrectParamsAndTokenIsValidAndIsAdmin() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(true);

                    mockMvc.perform(post(uri)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
                }
            }

            @Test
            @DisplayName("Returns INVALID_TOKEN if token is invalid")
            public void getAllUsers_shouldReturnInvalidToken_whenTokenIsInvalid() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
                }
            }

            @Test
            @DisplayName("Doesn't return the user list if token is invalid token")
            public void getAllUsers_shouldNotReturnUserList_whenTokenIsInvalid() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.content").isEmpty());
                }
            }

            @Test
            @DisplayName("Returns INSUFFICIENT_PERMISSIONS if token is not an admin token")
            public void getAllUsers_shouldNotReturnUserList_whenTokenIsNotAdminToken() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri, "Foo")
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.content").isEmpty());
                }
            }

            @Test
            @DisplayName("Doesn't return the user list if token is not an admin token")
            public void getAllUsers_shouldReturnInsufficientPermissions_whenTokenIsNotAdminToken() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri, "Foo")
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
                }
            }

            @Test
            @DisplayName("Returns the user list if token is valid and is an admin token")
            public void getAllUsers_shouldReturnUser_whenCorrectParamsAndTokenIsValidAndIsAdmin() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(true);

                    mockMvc.perform(post(uri)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.content").isNotEmpty())
                           .andExpect(jsonPath("$.content").isArray())
                           .andExpect(jsonPath("$.content[*].username").exists())
                           .andExpect(jsonPath("$.content[*].firstName").exists())
                           .andExpect(jsonPath("$.content[*].lastName").exists())
                           .andExpect(jsonPath("$.content[*].email").exists())
                           .andExpect(jsonPath("$.content[*].birthday").exists())
                           .andExpect(jsonPath("$.content[*].registrationDate").exists())
                           .andExpect(jsonPath("$.content[*].admin").exists());
                }
            }

        }

    }

    @Nested
    class Search {

        private final String uri = "/user/search";

        @BeforeEach
        public void setUp() {
            User user = new User();

            user.setAdmin(false);
            user.setUsername("Foo");
            user.setFirstName("Foo");
            user.setLastName("Foo");
            user.setEmail("Foo");
            user.setPassword("Foo");
            user.setAddress("Foo");
            user.setBirthday(LocalDate.EPOCH);
            user.setRegistrationDate(LocalDate.EPOCH);

            when(userService.search("Foo", "null", "null", "null", "null", "null", "null"))
                    .thenReturn(Collections.singletonList(user));
        }

        @Test
        @DisplayName("Returns OK if token is valid and is an admin token")
        public void search_shouldReturnOK_whenTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                mockMvc.perform(post(uri)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Returns INSUFFICIENT_PERMISSIONS if token is not an admin token")
        public void Search_shouldReturnInsufficientPermissions_whenTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
            }
        }

        @Test
        @DisplayName("Returns INVALID_TOKEN if token is invalid")
        public void Search_shouldReturnInvalidToken_whenTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

        @Test
        @DisplayName("Returns the list of matching users")
        public void Search_shouldReturnMatchingUsers() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("username", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.content").isNotEmpty())
                       .andExpect(jsonPath("$.content").isArray())
                       .andExpect(jsonPath("$.content[*].username").exists())
                       .andExpect(jsonPath("$.content[*].firstName").exists())
                       .andExpect(jsonPath("$.content[*].lastName").exists())
                       .andExpect(jsonPath("$.content[*].email").exists())
                       .andExpect(jsonPath("$.content[*].birthday").exists())
                       .andExpect(jsonPath("$.content[*].registrationDate").exists())
                       .andExpect(jsonPath("$.content[*].admin").exists());
            }
        }
    }

    @Nested
    class CheckUsernameAvailability {

        @Test
        @DisplayName("Always returns OK")
        public void checkUsernameAvailability_shouldAlwaysReturnOK() throws Exception {
            when(userService.usernameIsUnique("Foo")).thenReturn(true);
            mockMvc.perform(get("/user/check_username/{username}", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));

            when(userService.usernameIsUnique("Foo")).thenReturn(false);
            mockMvc.perform(get("/user/check_username/{username}", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
        }

        @Test
        @DisplayName("Returns true if the username is available")
        public void checkUsernameAvailability_shouldReturnTrue_whenTheUsernameIsAvailable() throws Exception {
            when(userService.usernameIsUnique("Foo")).thenReturn(true);

            mockMvc.perform(get("/user/check_username/{username}", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.content").value(true));
        }

        @Test
        @DisplayName("Returns false if the username is not available")
        public void checkUsernameAvailability_shouldReturnFalse_whenTheUsernameIsNotAvailable() throws Exception {
            when(userService.usernameIsUnique("Foo")).thenReturn(false);

            mockMvc.perform(get("/user/check_username/{username}", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.content").value(false));
        }

    }

}
