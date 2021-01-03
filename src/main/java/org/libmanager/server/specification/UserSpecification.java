package org.libmanager.server.specification;

import java.time.LocalDate;

import org.libmanager.server.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> usernameLike(String username) {
        return ((root, query, criteriaBuilder) ->
                username.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("username"), '%' + username + '%'));
    }

    public static Specification<User> firstNameLike(String firstName) {
        return ((root, query, criteriaBuilder) ->
                firstName.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("firstName"), '%' + firstName + '%'));
    }

    public static Specification<User> lastNameLike(String lastName) {
        return ((root, query, criteriaBuilder) ->
                lastName.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("lastName"), '%' + lastName + '%'));
    }

    public static Specification<User> emailLike(String email) {
        return ((root, query, criteriaBuilder) ->
                email.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("email"), '%' + email + '%'));
    }

    public static Specification<User> addressLike(String address) {
        return ((root, query, criteriaBuilder) ->
                address.equals("null") ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.like(root.get("address"), '%' + address + '%'));
    }

    public static Specification<User> birthdayEquals(LocalDate birthday) {
        return ((root, query, criteriaBuilder) ->
                birthday == null ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("birthday"), birthday));
    }

    public static Specification<User> registrationDateEquals(LocalDate registrationDate) {
        return ((root, query, criteriaBuilder) ->
                registrationDate == null ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("registrationDate"), registrationDate));
    }

}
