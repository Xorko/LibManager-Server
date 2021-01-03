package org.libmanager.server.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.libmanager.server.entity.User;
import org.libmanager.server.repository.UserRepository;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.impl.UserServiceImpl;
import org.libmanager.server.specification.UserSpecification;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private final UserService userService = new UserServiceImpl();

    private static User user;
    private static List<User> userIterable;

    @BeforeAll
    public static void setUp() {
        user = new User();
        user.setUsername("Foo");
        user.setFirstName("Foo");
        user.setLastName("Foo");
        user.setEmail("Foo");
        user.setPassword("Foo");
        user.setAddress("Foo");
        user.setBirthday(LocalDate.EPOCH);
        user.setAdmin(false);

        userIterable = Arrays.asList(user, user, user);
    }

    @Nested
    class Getters {
        @Nested
        class get {

            @Test
            @DisplayName("Returns the user if found")
            public void get_shouldReturnTheUser_whenFound() {
                when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));

                User result = userService.get(user.getUsername());

                assertThat(result).isEqualTo(result);
            }

            @Test
            @DisplayName("Returns the null if not found")
            public void get_shouldReturnNull_whenNotFound() {
                when(userRepository.findById(user.getUsername())).thenReturn(Optional.empty());

                User result = userService.get(user.getUsername());

                assertThat(result).isNull();
            }
        }

        @Nested
        class getAll {

            @Test
            @DisplayName("Returns the list of users")
            public void getAll_shouldReturnTheListOfUsers() {
                when(userRepository.findAll()).thenReturn(userIterable);

                Iterable<User> result = userService.getAll();

                assertThat(result).isEqualTo(userIterable);
            }

        }

    }

    @Nested
    class search {

        @Test
        @DisplayName("Returns matching users")
        public void search_shouldReturnMatchingUsers() {
            when(userRepository.findAll(any(UserSpecification.class))).thenReturn(userIterable);

            Iterable<User> result = userService.search(
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAddress(),
                    user.getEmail(),
                    user.getBirthday().toString(),
                    user.getRegistrationDate().toString()
            );

            assertThat(result).isEqualTo(userIterable);
        }

    }

    @Nested
    class Add {

        @Test
        @DisplayName("Returns true if user is added and user limit is not reached")
        public void add_shouldReturnTrue_whenUserIsAddedAndLimitIsNotReached() {
            when(userRepository.count()).thenReturn(1999L);

            boolean result = userService.add(
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAddress(),
                    user.getEmail(),
                    user.getBirthday().toString(),
                    user.getPassword()
            );

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Returns false if user limit is reached")
        public void add_shouldReturnFalse_whenUserLimitIsReached() {
            when(userRepository.count()).thenReturn(2000L);

            boolean result = userService.add(
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAddress(),
                    user.getEmail(),
                    user.getBirthday().toString(),
                    user.getPassword()
            );

            assertThat(result).isFalse();
        }

    }

    @Nested
    class Edit {

        @Test
        @DisplayName("Returns true if user is found and edited")
        public void edit_shouldReturnTrue_whenUserIsEdited() {
            when(userRepository.findById("Foo")).thenReturn(Optional.of(user));

            boolean result = userService.edit(
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAddress(),
                    user.getEmail(),
                    user.getBirthday().toString()
            );

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Returns false if user is not found")
        public void edit_shouldReturnFalse_whenUserIsNotFound() {
            when(userRepository.findById("Foo")).thenReturn(Optional.empty());

            boolean result = userService.edit(
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAddress(),
                    user.getEmail(),
                    user.getBirthday().toString()
            );

            assertThat(result).isFalse();
        }

    }

    @Nested
    class Delete {

        @Test
        @DisplayName("Returns OK if user is found and deleted")
        public void delete_shouldReturnOK_whenUserIsDeleted() {
            when(userRepository.findById("Foo")).thenReturn(Optional.of(user));

            Response<Boolean> result = userService.delete(user.getUsername());

            assertThat(result.getCode()).isEqualTo(Response.Code.OK);
        }

        @Test
        @DisplayName("Returns FORBIDDEN if user is found and is admin and not deleted")
        public void delete_shouldReturnForbidden_whenUserIsAdmin() {
            User userSpy = spy(user);
            when(userRepository.findById("Foo")).thenReturn(Optional.of(userSpy));
            doReturn(true).when(userSpy).isAdmin();

            Response<Boolean> result = userService.delete(userSpy.getUsername());

            assertThat(result.getCode()).isEqualTo(Response.Code.FORBIDDEN);
        }

        @Test
        @DisplayName("Returns NOT_FOUND if user is not deleted")
        public void delete_shouldReturnFalse_whenUserIsNotFound() {
            when(userRepository.findById("Foo")).thenReturn(Optional.empty());

            Response<Boolean> result = userService.delete(user.getUsername());

            assertThat(result.getCode()).isEqualTo(Response.Code.NOT_FOUND);
        }

    }

    @Nested
    class UsernameIsAvailable {

        @Test
        @DisplayName("Returns true if available")
        public void usernameIsAvailable_shouldReturnTrue_whenUsernameExists() {
            when(userRepository.findById("Foo")).thenReturn(Optional.of(user));

            boolean result = userService.usernameIsUnique("Foo");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Returns false if not available")
        public void usernameIsAvailable_shouldReturnFalse_whenUsernameDoesNotExists() {
            when(userRepository.findById("Foo")).thenReturn(Optional.empty());

            boolean result = userService.usernameIsUnique("Foo");

            assertThat(result).isTrue();
        }

    }

}
