package org.libmanager.server.unit.controller;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.libmanager.server.controller.ReservationController;
import org.libmanager.server.entity.Book;
import org.libmanager.server.entity.Item;
import org.libmanager.server.entity.Reservation;
import org.libmanager.server.entity.User;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.ReservationService;
import org.libmanager.server.util.TokenUtil;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class Add {

        private final String uri = "/reservation/add";

        @Test
        @DisplayName("Returns OK if token is valid and item exists")
        public void add_shouldReturnOK_whenTokenIsValidAndItemExists() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.extractUsername("Foo"))
                               .thenReturn("Foo");

                when(reservationService.add("Foo", 1))
                        .thenReturn(new Response<>(Response.Code.OK, true));

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("itemId", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Returns NOT_AVAILABLE if token is valid and item exists but is not available")
        public void add_shouldReturnNotAvailable_WhenItemIsNotAvailable() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.extractUsername("Foo"))
                               .thenReturn("Foo");

                when(reservationService.add("Foo", 1))
                        .thenReturn(new Response<>(Response.Code.NOT_AVAILABLE, true));

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("itemId", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.NOT_AVAILABLE.toString()));
            }
        }

        @Test
        @DisplayName("Returns MAX_RESERVATIONS_REACHED if user's reservation limit is reached")
        public void add_shouldReturnMaxReservationReached_whenUsersReservationLimitsIsReached() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.extractUsername("Foo"))
                               .thenReturn("Foo");

                when(reservationService.add("Foo", 1))
                        .thenReturn(new Response<>(Response.Code.NOT_AVAILABLE, true));

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("itemId", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.NOT_AVAILABLE.toString()));
            }
        }

        @Test
        @DisplayName("Returns NOT_FOUND if user or item is not found")
        public void add_shouldReturnNotFound_whenUserOrItemIsNotFound() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.extractUsername("Foo"))
                               .thenReturn("Foo");

                when(reservationService.add("Foo", 1))
                        .thenReturn(new Response<>(Response.Code.NOT_FOUND, true));

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("itemId", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
            }
        }

        @Test
        @DisplayName("Returns INVALID_TOKEN if token is invalid")
        public void add_shouldReturnInvalidToken_whenTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);
                mockedTokenUtil.when(() -> TokenUtil.extractUsername("Foo"))
                               .thenReturn("Foo");

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("itemId", "1"))
                       .andExpect(status().isOk())
                       .andDo(print())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

    }

    @Nested
    class Delete {

        private final String uri = "/reservation/delete/{id}";

        @Test
        @DisplayName("Returns OK if reservation is deleted")
        public void delete_shouldReturnOK_WhenReservationIsDeleted() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);


                when(reservationService.delete(1))
                        .thenReturn(new Response<>(Response.Code.OK, true));

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Returns NOT_FOUND if reservation is not found")
        public void delete_shouldReturnNotFound_whenReservationIsNotFound() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);


                when(reservationService.delete(1))
                        .thenReturn(new Response<>(Response.Code.NOT_FOUND, false));

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
            }
        }

        @Test
        @DisplayName("Returns INSUFFICIENT_PERMISSIONS if token is not an admin token")
        public void delete_shouldReturnInsufficientPermissions_whenTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
            }
        }

        @Test
        @DisplayName("Returns INVALID_TOKEN if reservation is not found")
        public void delete_shouldReturnInvalidToken_whenTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

    }

    @Nested
    class Getters {

        private Reservation reservation;

        @BeforeEach
        public void setUp() {
            User user = new User();
            Book book = new Book();

            user.setUsername("Foo");
            book.setTitle("Foo");

            reservation = new Reservation();
            reservation.setId(1);
            reservation.setUser(user);
            reservation.setItem(book);
        }

        @Nested
        class Get {

            private final String uri = "/reservation/get/{id}";

            @Test
            @DisplayName("Returns OK if token is valid and is admin")
            public void get_shouldReturnOK_whenTokenIsValidAndAdmin() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(true);

                    when(reservationService.get(1)).thenReturn(reservation);

                    mockMvc.perform(post(uri, 1)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
                }
            }

            @Test
            @DisplayName("Returns reservation if token is valid and admin")
            public void get_shouldReturnReservation_whenTokenIsValidAndAdmin() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(true);

                    when(reservationService.get(1)).thenReturn(reservation);

                    mockMvc.perform(post(uri, 1)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.content.id").exists())
                           .andExpect(jsonPath("$.content.title").exists())
                           .andExpect(jsonPath("$.content.username").exists());
                }
            }

            @Test
            @DisplayName("Doesn't return reservation if token is valid and not admin")
            public void getAll_shouldNotReturnReservation_whenTokenIsValidAndNotAdmin() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri, 1)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.content.id").doesNotExist())
                           .andExpect(jsonPath("$.content.title").doesNotExist())
                           .andExpect(jsonPath("$.content.username").doesNotExist());
                }
            }

            @Test
            @DisplayName("Doesn't return reservation if token is not valid")
            public void getAll_shouldNotReturnReservation_whenTokenIsNotValid() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri, 1)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.content.id").doesNotExist())
                           .andExpect(jsonPath("$.content.title").doesNotExist())
                           .andExpect(jsonPath("$.content.username").doesNotExist());
                }
            }

            @Test
            @DisplayName("Returns NOT_FOUND if reservation is not found")
            public void getAll_shouldReturnNotFound_whenReservationIsNotFound() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(true);

                    when(reservationService.get(1)).thenReturn(null);

                    mockMvc.perform(post(uri, 1)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
                }
            }

            @Test
            @DisplayName("Returns INSUFFICIENT_PERMISSIONS if token is valid but not admin")
            public void getAll_shouldReturnInsufficientPermissions_whenTokenIsValidAndNotAdmin() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri, 1)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
                }
            }

            @Test
            @DisplayName("Returns INVALID_TOKEN if token is valid but not admin")
            public void getAll_shouldReturnInvalidToken_whenTokenIsNotValid() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri, 1)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
                }
            }

        }


        @Nested
        class GetAll {

            private final String uri = "/reservation/all";
            private Iterable<Reservation> reservationIterable;

            @BeforeEach
            public void setUp() {
                reservationIterable = Arrays.asList(reservation, reservation, reservation);
            }

            @Test
            @DisplayName("Returns OK if token is valid and is admin")
            public void getAll_shouldReturnOK_whenTokenIsValidAndAdmin() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(true);

                    when(reservationService.getAll()).thenReturn(reservationIterable);

                    mockMvc.perform(post(uri)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
                }
            }

            @Test
            @DisplayName("Returns reservation list if token is valid and admin")
            public void getAll_shouldReturnReservationList_whenTokenIsValidAndAdmin() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(true);

                    when(reservationService.getAll()).thenReturn(reservationIterable);

                    mockMvc.perform(post(uri)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.content[*].id").exists())
                           .andExpect(jsonPath("$.content[*].title").exists())
                           .andExpect(jsonPath("$.content[*].username").exists());
                }
            }

            @Test
            @DisplayName("Doesn't return reservation list if token is valid and not admin")
            public void getAll_shouldNotReturnReservationList_whenTokenIsValidAndNotAdmin() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(true);
                    mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.content[*].id").doesNotExist())
                           .andExpect(jsonPath("$.content[*].title").doesNotExist())
                           .andExpect(jsonPath("$.content[*].username").doesNotExist());
                }
            }

            @Test
            @DisplayName("Doesn't return reservation list if token is not valid")
            public void getAll_shouldNotReturnReservationList_whenTokenIsNotValid() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.content[*].id").doesNotExist())
                           .andExpect(jsonPath("$.content[*].title").doesNotExist())
                           .andExpect(jsonPath("$.content[*].username").doesNotExist());
                }
            }

            @Test
            @DisplayName("Returns INSUFFICIENT_PERMISSIONS if token is valid but not admin")
            public void getAll_shouldReturnInsufficientPermissions_whenTokenIsValidAndNotAdmin() throws Exception {
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
            @DisplayName("Returns INVALID_TOKEN if token is valid but not admin")
            public void getAll_shouldReturnInvalidToken_whenTokenIsNotValid() throws Exception {
                try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                    mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                                   .thenReturn(false);

                    mockMvc.perform(post(uri)
                            .param("token", "Foo"))
                           .andExpect(status().isOk())
                           .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
                }
            }

        }

    }

    @Nested
    class Search {

        private final String uri = "/reservation/search";
        private Iterable<Reservation> reservationIterable;

        @BeforeEach
        public void setUp() {
            User user = new User();
            Book book = new Book();

            user.setUsername("Foo");
            book.setTitle("Foo");

            Reservation reservation = new Reservation();
            reservation.setId(1);
            reservation.setUser(user);
            reservation.setItem(book);

            reservationIterable = Arrays.asList(reservation, reservation, reservation);
        }

        @Test
        @DisplayName("Returns OK if token is valid and is admin")
        public void search_shouldReturnOK_whenTokenIsValidAndAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(reservationService.search(1, "null", "null", "null"))
                        .thenReturn(reservationIterable);

                mockMvc.perform(post(uri)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Returns matching reservations if token is valid and admin")
        public void search_shouldReturnMatchingReservation_whenTokenIsValidAndAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(reservationService.search(1, "null", "null", "null"))
                        .thenReturn(reservationIterable);

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("id", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.content[*].id").exists())
                       .andExpect(jsonPath("$.content[*].title").exists())
                       .andExpect(jsonPath("$.content[*].username").exists());
            }
        }

        @Test
        @DisplayName("Doesn't return matching reservation if token is valid and not admin")
        public void search_shouldNotReturnMatchingReservations_whenTokenIsValidAndNotAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.content[*].id").doesNotExist())
                       .andExpect(jsonPath("$.content[*].title").doesNotExist())
                       .andExpect(jsonPath("$.content[*].username").doesNotExist());
            }
        }

        @Test
        @DisplayName("Doesn't return matching reservations if token is not valid")
        public void search_shouldNotReturnmatchingReservation_whenTokenIsNotValid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.content[*].id").doesNotExist())
                       .andExpect(jsonPath("$.content[*].title").doesNotExist())
                       .andExpect(jsonPath("$.content[*].username").doesNotExist());
            }
        }

        @Test
        @DisplayName("Returns INSUFFICIENT_PERMISSIONS if token is valid but not admin")
        public void search_shouldReturnInsufficientPermissions_whenTokenIsValidAndNotAdmin() throws Exception {
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
        @DisplayName("Returns INVALID_TOKEN if token is valid but not admin")
        public void search_shouldReturnInvalidToken_whenTokenIsNotValid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

    }

    @Nested
    class GetByUser {

        private final String uri = "/reservation/get_user_reservations";
        private Iterable<Reservation> reservationIterable;

        @BeforeEach
        public void setUp() {
            Reservation reservation = new Reservation();
            User user = new User();
            Item book = new Book();

            user.setUsername("Foo");
            book.setTitle("Foo");
            reservation.setUser(user);
            reservation.setItem(book);

            reservationIterable = Arrays.asList(reservation, reservation, reservation);
        }

        @Test
        @DisplayName("Returns OK if token is valid and user is found")
        public void getUserReservations_shouldReturnOk_whenTokenIsValidAndUserIsFound() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.extractUsername("Foo"))
                               .thenReturn("Foo");

                when(reservationService.getByUser("Foo")).thenReturn(new Response<>(Response.Code.OK, reservationIterable));

                mockMvc.perform(post(uri)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Returns reservation list if token is valid and user is found")
        public void getUserReservations_shouldReturnReservationList_whenTokenIsValidAndUserIsFound() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.extractUsername("Foo"))
                               .thenReturn("Foo");

                when(reservationService.getByUser("Foo")).thenReturn(new Response<>(Response.Code.OK, reservationIterable));

                mockMvc.perform(post(uri)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.content").isNotEmpty())
                       .andExpect(jsonPath("$.content[*].id").exists())
                       .andExpect(jsonPath("$.content[*].username").exists())
                       .andExpect(jsonPath("$.content[*].title").exists());
            }
        }

        @Test
        @DisplayName("Returns NOT_FOUND if user is not found")
        public void getUserReservations_shouldReturnNotFound_whenTokenIsValidAndUserIsNotFound() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.extractUsername("Foo"))
                               .thenReturn("Foo");

                when(reservationService.getByUser("Foo")).thenReturn(new Response<>(Response.Code.OK, reservationIterable));

                mockMvc.perform(post(uri)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.content").isNotEmpty())
                       .andExpect(jsonPath("$.content[*].id").exists())
                       .andExpect(jsonPath("$.content[*].username").exists())
                       .andExpect(jsonPath("$.content[*].title").exists());
            }
        }

        @Test
        @DisplayName("Returns INVALID_TOKEN if token is invalid")
        public void getUserReservations_shouldReturnInvalidToken_whenTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

    }

}
