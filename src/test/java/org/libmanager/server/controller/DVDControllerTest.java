package org.libmanager.server.unit.controller;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.libmanager.server.controller.DVDController;
import org.libmanager.server.entity.DVD;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.DVDService;
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

@WebMvcTest(DVDController.class)
public class DVDControllerTest {

    @MockBean
    private DVDService dvdService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class Add {

        private final String uri = "/item/dvd/add";

        @Test
        @DisplayName("Returns OK if params are correct and token is valid and is admin")
        public void add_ShouldReturnOk_whenParamsAreCorrectAndTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(dvdService.add("Foo", "Foo", "Foo", "Foo", LocalDate.EPOCH.toString(), 1))
                        .thenReturn(new Response<>(Response.Code.OK, true));

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("title", "Foo")
                        .param("director", "Foo")
                        .param("duration", "Foo")
                        .param("genre", "Foo")
                        .param("releaseDate", LocalDate.EPOCH.toString())
                        .param("totalCopies", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Returns MAX_ITEMS_REACHED if params are correct and token is valid and is admin and max books limit is reached")
        public void add_ShouldReturnOk_whenParamsAreCorrectAndTokenIsValidAndIsAdminAndMaxBooksLimitIsReached() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(dvdService.add("Foo", "Foo", "Foo", "Foo", LocalDate.EPOCH.toString(), 1))
                        .thenReturn(new Response<>(Response.Code.MAX_ITEMS_REACHED, false));

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("title", "Foo")
                        .param("director", "Foo")
                        .param("duration", "Foo")
                        .param("genre", "Foo")
                        .param("releaseDate", LocalDate.EPOCH.toString())
                        .param("totalCopies", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.MAX_ITEMS_REACHED.toString()));
            }
        }

        @Test
        @DisplayName("Returns INSUFFICIENT_PERMISSIONS if the token is not an admin token")
        public void add_shouldReturnInsufficientPermissions_whenTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("title", "Foo")
                        .param("director", "Foo")
                        .param("duration", "Foo")
                        .param("genre", "Foo")
                        .param("releaseDate", LocalDate.EPOCH.toString())
                        .param("totalCopies", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
            }
        }

        @Test
        @DisplayName("Returns INVALID_TOKEN if the token is invalid")
        public void add_shouldReturnInsufficientPermissions_whenInvalidToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri)
                        .param("token", "Foo")
                        .param("title", "Foo")
                        .param("director", "Foo")
                        .param("duration", "Foo")
                        .param("genre", "Foo")
                        .param("releaseDate", LocalDate.EPOCH.toString())
                        .param("totalCopies", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

    }

    @Nested
    class Edit {

        private final String uri = "/item/dvd/edit/{id}";

        @Test
        @DisplayName("Returns OK if params are correct and token is valid and is admin")
        public void edit_shouldReturnOk_whenParamsAreCorrectAndTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(dvdService.edit(1, "Foo", "Foo", "Foo", "Foo", LocalDate.EPOCH.toString(), 1))
                        .thenReturn(new Response<>(Response.Code.OK, true));

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo")
                        .param("title", "Foo")
                        .param("director", "Foo")
                        .param("duration", "Foo")
                        .param("genre", "Foo")
                        .param("releaseDate", LocalDate.EPOCH.toString())
                        .param("totalCopies", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Returns INVALID_TOTAL_COPIES if new totalCopies is lower then availableCopies")
        public void edit_shouldReturnInvalidTotalCopies_whenNewTotalCopiesIsLowerThanAvailableCopies() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(dvdService.edit(1, "Foo", "Foo", "Foo", "Foo", LocalDate.EPOCH.toString(), 1))
                        .thenReturn(new Response<>(Response.Code.INVALID_TOTAL_COPIES, false));

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo")
                        .param("title", "Foo")
                        .param("director", "Foo")
                        .param("duration", "Foo")
                        .param("genre", "Foo")
                        .param("releaseDate", LocalDate.EPOCH.toString())
                        .param("totalCopies", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOTAL_COPIES.toString()));
            }
        }

        @Test
        @DisplayName("Returns NOT_FOUND if the book is not found")
        public void edit_shouldReturnNotFound_whenTheBookIsNotFound() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(dvdService.edit(1, "Foo", "Foo", "Foo", "Foo", LocalDate.EPOCH.toString(), 1))
                        .thenReturn(new Response<>(Response.Code.NOT_FOUND, false));

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo")
                        .param("title", "Foo")
                        .param("director", "Foo")
                        .param("duration", "Foo")
                        .param("genre", "Foo")
                        .param("releaseDate", LocalDate.EPOCH.toString())
                        .param("totalCopies", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
            }
        }

        @Test
        @DisplayName("Returns INVALID_TOKEN if the token is invalid")
        public void edit_shouldReturnInvalidToken_whenTokenIsInvalid() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo")
                        .param("title", "Foo")
                        .param("director", "Foo")
                        .param("duration", "Foo")
                        .param("genre", "Foo")
                        .param("releaseDate", LocalDate.EPOCH.toString())
                        .param("totalCopies", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

        @Test
        @DisplayName("Returns INSUFFICIENT_PERMISSIONS if the token is not an admin token")
        public void edit_shouldReturnInsufficientPermissions_whenTokenIsNotAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo")
                        .param("title", "Foo")
                        .param("director", "Foo")
                        .param("duration", "Foo")
                        .param("genre", "Foo")
                        .param("releaseDate", LocalDate.EPOCH.toString())
                        .param("totalCopies", "1"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
            }
        }

    }

    @Nested
    class Getters {

        private DVD dvd;

        @BeforeEach
        public void setUp() {
            dvd = new DVD();
            dvd.setTitle("Foo");
            dvd.setAuthor("Foo");
            dvd.setGenre("Foo");
            dvd.setDuration("Foo");
            dvd.setReleaseDate(LocalDate.EPOCH);
            dvd.setTotalCopies(1);
            dvd.setAvailableCopies(1);
        }

        @Nested
        class Get {

            private final String uri = "/item/dvd/get/{id}";

            @BeforeEach
            public void setUp() {
                when(dvdService.get(1)).thenReturn(dvd);
                when(dvdService.get(2)).thenReturn(null);
            }

            @Test
            @DisplayName("Returns OK if the book is found")
            public void getBook_shouldReturnOk_whenFound() throws Exception {
                mockMvc.perform(get(uri, 1))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }

            @Test
            @DisplayName("Returns the book if found")
            public void getBook_shouldReturnBook_whenFound() throws Exception {
                mockMvc.perform(get(uri, 1))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.content.title").value("Foo"))
                       .andExpect(jsonPath("$.content.author").value("Foo"))
                       .andExpect(jsonPath("$.content.genre").value("Foo"))
                       .andExpect(jsonPath("$.content.duration").value("Foo"))
                       .andExpect(jsonPath("$.content.availableCopies").value(1))
                       .andExpect(jsonPath("$.content.totalCopies").value(1))
                       .andExpect(jsonPath("$.content.releaseDate").value(LocalDate.EPOCH.toString()));
            }

            @Test
            @DisplayName("Returns NOT_FOUND if the book is not found")
            public void getDVD_shouldReturnNotFound_whenNotFound() throws Exception {
                mockMvc.perform(get(uri, 2))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
            }

            @Test
            @DisplayName("Content should be null if the book is not found")
            public void getDVDContent_shouldBeNull_whenNotFound() throws Exception {
                mockMvc.perform(get(uri, 2))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.content.title").doesNotExist())
                       .andExpect(jsonPath("$.content.author").doesNotExist())
                       .andExpect(jsonPath("$.content.genre").doesNotExist())
                       .andExpect(jsonPath("$.content.duration").doesNotExist())
                       .andExpect(jsonPath("$.content.availableCopies").doesNotExist())
                       .andExpect(jsonPath("$.content.totalCopies").doesNotExist())
                       .andExpect(jsonPath("$.content.releaseDate").doesNotExist());
            }
        }

        @Nested
        class GetAll {

            private final String uri = "/item/dvd/all";

            @BeforeEach
            public void setUp() {
                Iterable<DVD> bookIterable = Arrays.asList(dvd, dvd, dvd);
                when(dvdService.getAll()).thenReturn(bookIterable);
            }

            @Test
            @DisplayName("Returns OK")
            public void getAllDVDs_shouldReturnOK() throws Exception {
                mockMvc.perform(get(uri))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }

            @Test
            @DisplayName("Returns the book list")
            public void getAllDVDs_shouldReturnBookList() throws Exception {
                mockMvc.perform(get(uri))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.content[*].title").exists())
                       .andExpect(jsonPath("$.content[*].author").exists())
                       .andExpect(jsonPath("$.content[*].genre").exists())
                       .andExpect(jsonPath("$.content[*].availableCopies").exists())
                       .andExpect(jsonPath("$.content[*].totalCopies").exists())
                       .andExpect(jsonPath("$.content[*].releaseDate").exists());
            }

            @Test
            @DisplayName("Returns not null content when no params are given")
            public void search_shouldReturnContent_whenNoParamsAreGiven() throws Exception {
                mockMvc.perform(get(uri))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.content").exists())
                       .andExpect(jsonPath("$.content").isArray());
            }

        }

    }

    @Nested
    class Search {

        private final String uri = "/item/dvd/search";

        @BeforeEach
        public void setUp() {
            DVD dvd = new DVD();
            dvd.setTitle("Foo");
            dvd.setAuthor("Foo");
            dvd.setGenre("Foo");
            dvd.setDuration("Foo");
            dvd.setReleaseDate(LocalDate.EPOCH);
            dvd.setTotalCopies(1);
            dvd.setAvailableCopies(1);

            Iterable<DVD> dvdIterable = Arrays.asList(dvd, dvd, dvd);

            when(dvdService.search("Foo", "null", "null", "null", "null"))
                    .thenReturn(dvdIterable);
        }

        @Test
        @DisplayName("Returns OK")
        public void search_shouldReturnOK() throws Exception {
            mockMvc.perform(get(uri)
                    .param("title", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
        }

        @Test
        @DisplayName("Returns the list of matching DVDs")
        public void search_shouldReturnMatchingDVDs() throws Exception {
            mockMvc.perform(get(uri)
                    .param("title", "Foo"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.content[*].title").exists())
                   .andExpect(jsonPath("$.content[*].author").exists())
                   .andExpect(jsonPath("$.content[*].genre").exists())
                   .andExpect(jsonPath("$.content[*].availableCopies").exists())
                   .andExpect(jsonPath("$.content[*].totalCopies").exists())
                   .andExpect(jsonPath("$.content[*].releaseDate").exists());
        }

        @Test
        @DisplayName("Returns not null content when no params are given")
        public void search_shouldReturnContent_whenNoParamsAreGiven() throws Exception {
            mockMvc.perform(get(uri))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.content").exists())
                   .andExpect(jsonPath("$.content").isArray());
        }

    }

}
