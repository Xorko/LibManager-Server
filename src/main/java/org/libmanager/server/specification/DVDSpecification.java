package org.libmanager.server.specification;

import java.time.LocalDate;

import org.libmanager.server.entity.DVD;
import org.springframework.data.jpa.domain.Specification;

public class DVDSpecification {

    public static Specification<DVD> titleLike(String title) {
        return ((root, query, criteriaBuilder) ->
                title.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("title"), '%' + title + '%'));
    }

    public static Specification<DVD> directorLike(String director) {
        return ((root, query, criteriaBuilder) ->
                director.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("author"), '%' + director + '%'));
    }

    public static Specification<DVD> genreEquals(String genre) {
        return ((root, query, criteriaBuilder) ->
                genre.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("genre"), genre));
    }

    public static Specification<DVD> releaseDateEquals(LocalDate releaseDate) {
        return ((root, query, criteriaBuilder) ->
                releaseDate == null ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("releaseDate"), releaseDate));
    }

    public static Specification<DVD> statusEquals(String status) {
        return ((root, query, criteriaBuilder) ->
                status.equals("null") ?
                        criteriaBuilder.conjunction() :
                        status.equals("0") ?
                                criteriaBuilder.equal(root.get("status"), false) :
                                criteriaBuilder.equal(root.get("status"), true));
    }

}
