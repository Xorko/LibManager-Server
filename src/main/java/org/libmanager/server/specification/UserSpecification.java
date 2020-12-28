package org.libmanager.server.specification;

import org.libmanager.server.entity.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class UserSpecification implements Specification<User> {

    private final User filter;

    public UserSpecification(User filter) {
        super();
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate p = criteriaBuilder.disjunction();

        if (filter.getUsername() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("username"), filter.getUsername()));

        if (filter.getFirstName() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("firstName"), filter.getFirstName()));

        if (filter.getLastName() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("lastName"), filter.getLastName()));

        if (filter.getEmail() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("email"), filter.getEmail()));

        if (filter.getAddress() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("address"), filter.getAddress()));

        if (filter.getBirthday() != null)
            p.getExpressions().add(criteriaBuilder.equal(root.get("birthday"), filter.getBirthday()));

        if (filter.getRegistrationDate() != null)
            p.getExpressions().add(criteriaBuilder.equal(root.get("registrationDate"), filter.getRegistrationDate()));

        return p;
    }
}
