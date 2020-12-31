package org.libmanager.server.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("UserController_AddUser")
    class UserController_AddUser {

        @Test
        @DisplayName("Returns OK if params are correct and token is valid and is admin")
        public void addUser_shouldReturnOk_ifParamsAreCorrectAndTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(userService.add("Foo", "Foo", "Foo", "Foo", "Foo", "1970-01-01", "Foo")).thenReturn(true);

                mockMvc.perform(post("/user/add")
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
        public void addUser_shouldReturnMaxUsersReached_ifCorrectValuesAndLimitReached() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(userService.add("Foo", "Foo", "Foo", "Foo", "Foo", "1970-01-01", "Foo")).thenReturn(false);

                mockMvc.perform(post("/user/add")
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
        public void addUser_shouldReturnInvalidToken_ifTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post("/user/add")
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
        public void addUser_shouldReturnInsufficientPermissions_IfTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post("/user/add")
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
    @DisplayName("UserController_EditUser")
    class UserController_EditUser {

        @Test
        @DisplayName("Returns OK if params are correct and token is valid and is admin")
        public void editUser_shouldReturnOk_ifCorrectParamsAndTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(userService.edit("Foo", "Foo", "Foo", "Foo", "Foo", "1970-01-01")).thenReturn(true);

                mockMvc.perform(post("/user/edit/{username}", "Foo")
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
        public void editUser_shouldReturnNotFound_ifUnknownUsername() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(userService.edit("Foo", "Foo", "Foo", "Foo", "Foo", "1970-01-01")).thenReturn(false);

                mockMvc.perform(post("/user/edit/{username}", "Foo")
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
        public void editUser_shouldReturnInvalidToken_ifTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post("/user/edit/{username}", "Foo")
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
        public void editUser_shouldReturnInsufficientPermissions_IfTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post("/user/edit/{username}", "Foo")
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
    @DisplayName("UserController_DeleteUser")
    class UserController_DeleteUser {

        @Test
        @DisplayName("Returns OK if params are correct and token is valid and is admin")
        public void deleteUser_shouldReturnOk_ifCorrectParamsAndTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(userService.delete("Foo")).thenReturn(true);

                mockMvc.perform(post("/user/delete/{username}", "Foo")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Returns NOT_FOUND if unknown username")
        public void deleteUser_shouldReturnNotFound_ifUnknownUsername() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(userService.delete("Foo")).thenReturn(false);

                mockMvc.perform(post("/user/delete/{username}", "Foo")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
            }
        }

        @Test
        @DisplayName("Returns INVALID_TOKEN if token is invalid")
        public void deleteUser_shouldReturnInvalidToken_ifTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post("/user/delete/{username}", "Foo")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

        @Test
        @DisplayName("Returns INSUFFICIENT_PERMISSIONS if token is not an admin token")
        public void deleteUser_shouldReturnInsufficientPermissions_IfTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post("/user/delete/{username}", "Foo")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
            }
        }

    }

    @Nested
    @DisplayName("UserController_GetUsers")
    class UserController_GetUsers {

        @BeforeEach
        public void init() {
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

            Iterable<User> userIterable = Arrays.asList(user, user, user);

            when(userService.get("Foo")).thenReturn(user);
            when(userService.get("Bar")).thenReturn(null);
            when(userService.getAll()).thenReturn(userIterable);
        }

        @Test
        @DisplayName("GetUser returns OK if token is valid and is an admin token")
        public void getUser_shouldReturnOK_ifCorrectParamsAndTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                mockMvc.perform(post("/user/get/{username}", "Foo")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("GetUser returns INVALID_TOKEN if token is invalid")
        public void getUser_shouldReturnInvalidToken_IfTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post("/user/get/{username}", "Foo")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

        @Test
        @DisplayName("GetUser returns INSUFFICIENT_PERMISSIONS if token is not an admin token")
        public void getUser_shouldReturnInsufficientPermissions_IfTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post("/user/get/{username}", "Foo")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
            }
        }

        @Test
        @DisplayName("GetUser returns the user")
        public void getUser_shouldReturnUser() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                mockMvc.perform(post("/user/get/{username}", "Foo")
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
        @DisplayName("GetUser returns NOT_FOUND if not found")
        public void getUser_shouldReturnNotFound_ifNotFound() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                mockMvc.perform(post("/user/get/{username}", "Bar")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
            }
        }

        @Test
        @DisplayName("GetUser returns null if not found")
        public void getUser_shouldReturnNull_IfNotFound() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                mockMvc.perform(post("/user/get/{username}", "Bar")
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

        @Test
        @DisplayName("GetAllUsers returns OK if token is valid and is an admin token")
        public void getAllUsers_shouldReturnOk_ifCorrectParamsAndTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                mockMvc.perform(post("/user/all")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("GetAllUsers returns INVALID_TOKEN if token is invalid")
        public void getAllUsers_shouldReturnInvalidToken_IfTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post("/user/all")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

        @Test
        @DisplayName("GetAllUsers returns INSUFFICIENT_PERMISSIONS if token is not an admin token")
        public void getAllUsers_shouldReturnInsufficientPermissions_IfTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post("/user/all", "Foo")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
            }
        }

        @Test
        @DisplayName("GetAllUsers returns the user list if token is valid and is an admin token")
        public void getAllUsers_shouldReturnUser_ifCorrectParamsAndTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                mockMvc.perform(post("/user/all")
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

    @Nested
    class UserController_CheckUsernameAvailability {

        @Test
        @DisplayName("Always returns OK")
        public void checkUsernameAvailability_ShouldAlwaysReturnOK() throws Exception {
            when(userService.usernameIsAvailable("Foo")).thenReturn(true);
            mockMvc.perform(get("/user/check_username/{username}", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));

            when(userService.usernameIsAvailable("Foo")).thenReturn(false);
            mockMvc.perform(get("/user/check_username/{username}", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
        }

        @Test
        @DisplayName("Returns true if the username is available")
        public void checkUsernameAvailability_ShouldReturnTrue_IfTheUsernameIsAvailable() throws Exception {
            when(userService.usernameIsAvailable("Foo")).thenReturn(true);

            mockMvc.perform(get("/user/check_username/{username}", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.content").value(true));
        }

        @Test
        @DisplayName("Returns false if the username is not available")
        public void checkUsernameAvailability_ShouldReturnFalse_IfTheUsernameIsNotAvailable() throws Exception {
            when(userService.usernameIsAvailable("Foo")).thenReturn(false);

            mockMvc.perform(get("/user/check_username/{username}", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.content").value(false));
        }

    }

    @Nested
    class UserController_Search {

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


            when(userService.search("Foo", "null", "null", "null", "null", "null", "null")).thenReturn(Collections.singletonList(user));
        }

        @Test
        @DisplayName("Search returns OK if token is valid and is an admin token")
        public void search_shouldReturnOK_ifTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                mockMvc.perform(post("/user/search")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Search returns INSUFFICIENT_PERMISSIONS if token is not an admin token")
        public void Search_shouldReturnInsufficientPermissions_IfTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post("/user/search")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
            }
        }

        @Test
        @DisplayName("Search returns INVALID_TOKEN if token is invalid")
        public void Search_shouldReturnInvalidToken_IfTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post("/user/search")
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

        @Test
        @DisplayName("Search returns the list of users found")
        public void Search_shouldReturnMatchingUsers() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                mockMvc.perform(post("/user/search")
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

}
