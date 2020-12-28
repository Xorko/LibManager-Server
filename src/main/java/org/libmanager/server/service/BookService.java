package org.libmanager.server.service;

import org.libmanager.server.entity.Book;
import org.libmanager.server.response.Response;

public interface BookService {

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
    Response<Boolean> add(
            String title,
            String author,
            String publisher,
            String genre,
            String isbn,
            String releaseDate,
            int totalCopies
    );

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
    Response<Boolean> edit(
            long id,
            String title,
            String author,
            String publisher,
            String genre,
            String isbn,
            String releaseDate,
            int totalCopies
    );

    /**
     * Get all books
     * @return  All books in an iterable object
     */
    Iterable<Book> getAll();

    /**
     * Get a book by its id
     * @param id    The id of the book to get
     * @return      The book
     */
    Book get(long id);

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
    Iterable<Book> search(
            String title,
            String author,
            String publisher,
            String genre,
            String isbn,
            String releaseDate,
            String status
    );

}
