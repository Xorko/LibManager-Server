package org.libmanager.server.specification;

import java.time.LocalDate;

import org.libmanager.server.entity.Reservation;
import org.springframework.data.jpa.domain.Specification;

public class ReservationSpecification {

    public static Specification<Reservation> idEquals(long id) {
        return ((root, query, criteriaBuilder) ->
                id == 0 ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("id"), id));
    }

    public static Specification<Reservation> usernameLike(String username) {
        return ((root, query, criteriaBuilder) ->
                username.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("user").get("username"), '%' + username + '%'));
    }

    public static Specification<Reservation> titleLike(String title) {
        return ((root, query, criteriaBuilder) ->
                title.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("item").get("title"), '%' + title + '%'));
    }

    public static Specification<Reservation> dateEquals(LocalDate date) {
        return ((root, query, criteriaBuilder) ->
                date == null ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("reservationDate"), date));
    }

    public static Specification<Reservation> typeEquals(String type) {
        return ((root, query, criteriaBuilder) ->
                type.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("item").get("itemType"), type));
    }

}
