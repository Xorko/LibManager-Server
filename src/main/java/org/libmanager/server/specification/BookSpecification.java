package org.libmanager.server.specification;

import java.time.LocalDate;

import org.libmanager.server.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> titleLike(String title) {
        return ((root, query, criteriaBuilder) ->
                title.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("title"), '%' + title + '%'));
    }

    public static Specification<Book> authorLike(String author) {
        return ((root, query, criteriaBuilder) ->
                author.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("author"), '%' + author + '%'));
    }

    public static Specification<Book> publisherLike(String publisher) {
        return ((root, query, criteriaBuilder) ->
                publisher.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("author"), '%' + publisher + '%'));
    }

    public static Specification<Book> isbnLike(String isbn) {
        return ((root, query, criteriaBuilder) ->
                isbn.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("author"), '%' + isbn + '%'));
    }

    public static Specification<Book> genreEquals(String genre) {
        return ((root, query, criteriaBuilder) ->
                genre.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("genre"), genre));
    }

    public static Specification<Book> releaseDateEquals(LocalDate releaseDate) {
        return ((root, query, criteriaBuilder) ->
                releaseDate == null ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("releaseDate"), releaseDate));
    }

    public static Specification<Book> statusEquals(String status) {
        return ((root, query, criteriaBuilder) ->
                status.equals("null") ?
                        criteriaBuilder.conjunction() :
                        status.equals("0") ?
                                criteriaBuilder.equal(root.get("status"), false) :
                                criteriaBuilder.equal(root.get("status"), true));
    }
}
