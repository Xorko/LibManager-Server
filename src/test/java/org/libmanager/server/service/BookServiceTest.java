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
import org.libmanager.server.entity.Book;
import org.libmanager.server.repository.BookRepository;
import org.libmanager.server.repository.ItemRepository;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.impl.BookServiceImpl;
import org.libmanager.server.specification.BookSpecification;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private final BookService bookService = new BookServiceImpl();

    private static Book book;
    private static List<Book> bookList;

    @BeforeAll
    public static void setUp() {
        book = new Book();
        book.setTitle("Foo");
        book.setAuthor("Foo");
        book.setPublisher("Foo");
        book.setGenre("Foo");
        book.setIsbn("Foo");
        book.setReleaseDate(LocalDate.EPOCH);
        book.setTotalCopies(1);
        book.setAvailableCopies(1);

        bookList = Arrays.asList(book, book, book);
    }

    @Nested
    class Add {

        @Test
        @DisplayName("Returns Response with OK code if added successfully")
        public void add_shouldReturnResponseWithOKCode_whenAddedSuccessfully() {
            when(itemRepository.sumTotalCopies()).thenReturn(99_999L);

            Response<Boolean> result = bookService.add(
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getGenre(),
                    book.getIsbn(),
                    book.getReleaseDate().toString(),
                    book.getTotalCopies()
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.OK);
        }

        @Test
        @DisplayName("Returns Response with OK code if item table is empty and book added successfully")
        public void add_shouldReturnResponseWithOKCode_whenItemTableIsEmptyAndBookAddedSuccessfully() {
            when(itemRepository.sumTotalCopies()).thenReturn(null);

            Response<Boolean> result = bookService.add(
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getGenre(),
                    book.getIsbn(),
                    book.getReleaseDate().toString(),
                    book.getTotalCopies()
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.OK);
        }

        @Test
        @DisplayName("Returns Response with MAX_ITEMS_REACHED code if item limit is reached")
        public void add_shouldReturnResponseWithMaxItemsReached_whenLimitIsReached() {
            when(itemRepository.sumTotalCopies()).thenReturn(100_000L);

            Response<Boolean> result = bookService.add(
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getGenre(),
                    book.getIsbn(),
                    book.getReleaseDate().toString(),
                    book.getTotalCopies()
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.MAX_ITEMS_REACHED);
        }

    }

    @Nested
    class Edit {

        @Test
        @DisplayName("Returns Response with OK code if edited successfully")
        public void edit_shouldReturnResponseWithOK_whenEditedSuccessfully() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(itemRepository.sumTotalCopies()).thenReturn(100_000L);

            Response<Boolean> result = bookService.edit(
                    1L,
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getGenre(),
                    book.getIsbn(),
                    book.getReleaseDate().toString(),
                    book.getTotalCopies()
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.OK);
        }

        @Test
        @DisplayName("Returns Response with MAX_ITEMS_REACHED code if the new totalCopies value make sumTotalCopies more than 100,000")
        public void edit_shouldReturnResponseWithMaxItemsReached_whenLimitIsReached() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(itemRepository.sumTotalCopies()).thenReturn(100_000L);

            Response<Boolean> result = bookService.edit(
                    1L,
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getGenre(),
                    book.getIsbn(),
                    book.getReleaseDate().toString(),
                    book.getTotalCopies() + 1
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.MAX_ITEMS_REACHED);
        }

        @Test
        @DisplayName("Returns INVALID_TOTAL_COPIES if the new totalCopies value is lower than the availableCopies value")
        public void edit_shouldReturnInvalidTotalCopies_whenTotalCopiesIsLowerThanAvailableCopies() {
            Book book = new Book();
            book.setTitle("Foo");
            book.setAuthor("Foo");
            book.setPublisher("Foo");
            book.setGenre("Foo");
            book.setIsbn("Foo");
            book.setReleaseDate(LocalDate.EPOCH);
            book.setAvailableCopies(2);
            book.setTotalCopies(8);

            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(itemRepository.sumTotalCopies()).thenReturn(100L);

            Response<Boolean> result = bookService.edit(
                    1L,
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getGenre(),
                    book.getIsbn(),
                    book.getReleaseDate().toString(),
                    1
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.INVALID_TOTAL_COPIES);
        }

        @Test
        @DisplayName("Returns NOT_FOUND if book is not found")
        public void edit_shouldReturnNotFound_whenBookIsNotFound() {
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());

            Response<Boolean> result = bookService.edit(
                    1L,
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getGenre(),
                    book.getIsbn(),
                    book.getReleaseDate().toString(),
                    1
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.NOT_FOUND);
        }

    }

    @Nested
    class Getters {

        @Nested
        class GetAll {

            @Test
            @DisplayName("Returns the book list")
            public void getAll_shouldReturnBookList() {
                when(bookRepository.findAll()).thenReturn(bookList);

                Iterable<Book> result = bookService.getAll();

                assertThat(result).isEqualTo(bookList);
            }

        }

        @Nested
        class Get {

            @Test
            @DisplayName("Returns the book if found")
            public void get_shouldReturnBook_whenFound() {
                when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

                Book result = bookService.get(1L);

                assertThat(result).isEqualTo(book);
            }

            @Test
            @DisplayName("Returns null if not found")
            public void get_shouldReturnNull_whenNotFound() {
                when(bookRepository.findById(1L)).thenReturn(Optional.empty());

                Book result = bookService.get(1L);

                assertThat(result).isNull();
            }

        }

    }

    @Nested
    class Search {

        @Test
        @DisplayName("Returns matching books")
        public void search_shouldReturnMatchingBooks() {
            when(bookRepository.findAll(any(BookSpecification.class))).thenReturn(bookList);

            Iterable<Book> result = bookService.search(
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getGenre(),
                    book.getIsbn(),
                    book.getReleaseDate().toString(),
                    Boolean.toString(book.getStatus())
            );

            assertThat(result).isEqualTo(bookList);
        }

    }

}
