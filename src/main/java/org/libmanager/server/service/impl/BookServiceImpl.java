package org.libmanager.server.service.impl;

import java.util.Optional;

import org.libmanager.server.entity.Book;
import org.libmanager.server.response.Response;
import org.libmanager.server.repository.BookRepository;
import org.libmanager.server.repository.ItemRepository;
import org.libmanager.server.service.BookService;
import org.libmanager.server.specification.BookSpecification;
import org.libmanager.server.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public Iterable<Book> getAll() {
        return bookRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public Book get(long id) {
        Optional<Book> foundBook = bookRepository.findById(id);
        return foundBook.orElse(null);
    }

    /**
     * {@inheritDoc}
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
