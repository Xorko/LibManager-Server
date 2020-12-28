package org.libmanager.server.controller;

import org.libmanager.server.entity.Book;
import org.libmanager.server.model.Response;
import org.libmanager.server.service.BookService;
import org.libmanager.server.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/item/book")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * Add a book
     * @param token         The token of the user (must be admin)
     * @param title         The title of the book
     * @param author        The author of the book
     * @param publisher     The publisher of the book
     * @param genre         The genre of the book
     * @param isbn          The isbn of the book
     * @param releaseDate   The release date of the book
     * @param totalCopies   The number of total copies of the book
     * @return              A response with OK code and true if the add was successful, the error and false otherwise
     */
    @PostMapping(path = "/add")
    public @ResponseBody
    Response<Boolean> add(
            @RequestParam String token,
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam String publisher,
            @RequestParam String genre,
            @RequestParam String isbn,
            @RequestParam String releaseDate,
            @RequestParam int totalCopies
    ) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token))
                return bookService.add(title, author, publisher, genre, isbn, releaseDate, totalCopies);
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, false);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, false);
    }

    /**
     * Edit the given book
     * @param token         The token of the user (must be admin)
     * @param id            The id of the book to edit
     * @param title         The new title of the book
     * @param author        The new author of the book
     * @param publisher     The new publisher of the book
     * @param genre         The new genre of the book
     * @param isbn          The new isbn of the book
     * @param releaseDate   The new release date of the book
     * @param totalCopies   The new total number of total copies of the book
     * @return              A JSON response with OK code and true if the edit was successful, the error and false otherwise
     */
    @PostMapping(path = "/edit/{id}")
    public @ResponseBody
    Response<Boolean> edit(
            @RequestParam String token,
            @PathVariable long id,
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam String publisher,
            @RequestParam String genre,
            @RequestParam String isbn,
            @RequestParam String releaseDate,
            @RequestParam int totalCopies
    ) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token)) {
                return bookService.edit(id, title, author, publisher, genre, isbn, releaseDate, totalCopies);
            }
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, false);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, false);
    }

    /**
     * Get all books
     * @return  A JSON response with OK code and an array with all books
     */
    @GetMapping(path = "/all")
    public @ResponseBody
    Response<Iterable<Book>> getAll() {
        return new Response<>(Response.Code.OK, bookService.getAll());
    }

    /**
     *
     * @param id    The id of the book to get
     * @return      A JSON response with the book or null if the book is not found
     */
    @GetMapping(path = "/get/{id}")
    public @ResponseBody
    Response<Book> get(@PathVariable long id) {
        Book book = bookService.get(id);
        if (book != null)
            return new Response<>(Response.Code.OK, book);
        return new Response<>(Response.Code.NOT_FOUND, null);
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
     * @return              A JSON response with an OK code and all books found
     */
    @GetMapping(path = "/search")
    public @ResponseBody
    Response<Iterable<Book>> search(
            @RequestParam(defaultValue = "null") String title,
            @RequestParam(defaultValue = "null") String author,
            @RequestParam(defaultValue = "null") String publisher,
            @RequestParam(defaultValue = "null") String genre,
            @RequestParam(defaultValue = "null") String isbn,
            @RequestParam(defaultValue = "null") String releaseDate,
            @RequestParam(defaultValue = "null") String status
    ) {
        // If no argument is given, return all books
        Iterable<Book> bookIterable;
        if (title.equals("null") && author.equals("null") && publisher.equals("null") && genre.equals("null") &&
                isbn.equals("null") && releaseDate.equals("null") && status.equals("null"))
            bookIterable = bookService.getAll();
        else
            bookIterable = bookService.search(title, author, publisher, genre, isbn, releaseDate, status);
        return new Response<>(Response.Code.OK, bookIterable);
    }

}
