package org.libmanager.server.service;

import java.util.Optional;

import org.libmanager.server.entity.Book;
import org.libmanager.server.model.Response;
import org.libmanager.server.repository.BookRepository;
import org.libmanager.server.repository.ItemRepository;
import org.libmanager.server.specification.BookSpecification;
import org.libmanager.server.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Add a book
     * @param title         The title of the book
     * @param author        The author of the book
     * @param publisher     The publisher of the book
     * @param genre         The genre of the book
     * @param isbn          The isbn of the book
     * @param releaseDate   The release date of the book
     * @param totalCopies   The number of total copies of the book
     * @return              A response with OK code and true if the add was successful, the error and false otherwise
     */
    public Response<Boolean> add(
            String title,
            String author,
            String publisher,
            String genre,
            String isbn,
            String releaseDate,
            int totalCopies
    ) {
        Long sumTotalCopies = itemRepository.sumTotalCopies();
        // If the item table is empty, sumTotalCopies will be null
        if (sumTotalCopies == null)
            sumTotalCopies = 0L;
        if ((sumTotalCopies + totalCopies <= 100_000)) {
            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setPublisher(publisher);
            book.setGenre(genre.toUpperCase());
            book.setIsbn(isbn);
            book.setReleaseDate(DateUtil.parseDB(releaseDate));
            book.setTotalCopies(totalCopies);
            book.setAvailableCopies(totalCopies);
            bookRepository.save(book);
            return new Response<>(Response.Code.OK, true);
        }
        return new Response<>(Response.Code.MAX_ITEMS_REACHED, false);
    }

    /**
     * Edit the given book
     * @param id            The id of the book to edit
     * @param title         The new title of the book
     * @param author        The new author of the book
     * @param publisher     The new publisher of the book
     * @param genre         The new genre of the book
     * @param isbn          The new isbn of the book
     * @param releaseDate   The new release date of the book
     * @param totalCopies   The new number of total copies of the book
     * @return              A response with OK code and true if the edit was successful, the error and false otherwise
     */
    public Response<Boolean> edit(
            long id,
            String title,
            String author,
            String publisher,
            String genre,
            String isbn,
            String releaseDate,
            int totalCopies
    ) {
        Optional<Book> foundBook = bookRepository.findById(id);
        if (foundBook.isPresent()) {
            Book book = foundBook.get();
            // New sum of all total_copies rows value should not be higher than 100,000
            if (itemRepository.sumTotalCopies() - book.getTotalCopies() + totalCopies <= 100_000) {
                // New total_copies value should not be lower than the number of available copies
                if (book.getTotalCopies() - book.getAvailableCopies() <= totalCopies) {
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setPublisher(publisher);
                    book.setGenre(genre.toUpperCase());
                    book.setIsbn(isbn);
                    book.setReleaseDate(DateUtil.parseDB(releaseDate));
                    book.setTotalCopies(totalCopies);
                    book.setAvailableCopies(book.getAvailableCopies() + (totalCopies - book.getTotalCopies()));
                    bookRepository.save(book);
                    return new Response<>(Response.Code.OK, true);
                } else
                    return new Response<>(Response.Code.INVALID_TOTAL_COPIES, false);
            }
            return new Response<>(Response.Code.MAX_ITEMS_REACHED, false);
        }
        return new Response<>(Response.Code.NOT_FOUND, false);
    }

    /**
     * Get all books
     * @return  All books in an iterable object
     */
    public Iterable<Book> getAll() {
        return bookRepository.findAll();
    }

    /**
     * Get a book by its id
     * @param id    The id of the book to get
     * @return      The book
     */
    public Book get(long id) {
        Optional<Book> foundBook = bookRepository.findById(id);
        return foundBook.orElse(null);
    }

    /**
     * Search books
     * @param title         The title to find
     * @param author        The author to find
     * @param publisher     The publisher to find
     * @param genre         The genre to find
     * @param isbn          The isbn to find
     * @param releaseDate   The release date to find
     * @param status        The status to find (should be null to find any status)
     * @return              An iterable object with all found books
     */
    public Iterable<Book> search(
            String title,
            String author,
            String publisher,
            String genre,
            String isbn,
            String releaseDate,
            String status
    ) {
        Book filter = new Book();
        filter.setTitle('%' + title + '%');
        filter.setAuthor('%' + author + '%');
        filter.setPublisher('%' + publisher + '%');
        filter.setIsbn('%' + isbn + '%');
        filter.setGenre(genre);
        filter.setReleaseDate(DateUtil.parseDB(releaseDate));

        Specification<Book> spec = new BookSpecification(filter, status);

        return bookRepository.findAll(spec);
    }

}
