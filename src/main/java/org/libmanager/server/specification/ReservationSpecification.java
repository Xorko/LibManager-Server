package org.libmanager.server.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.libmanager.server.entity.Reservation;
import org.springframework.data.jpa.domain.Specification;

public class ReservationSpecification implements Specification<Reservation> {

    private final Reservation filter;
    private final String type;

    public ReservationSpecification(Reservation filter, String type) {
        super();
        this.filter = filter;
        this.type = type;
    }

    @Override
    public Predicate toPredicate(Root<Reservation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate p = criteriaBuilder.disjunction();

        if (filter.getId() != 0)
            p.getExpressions().add(criteriaBuilder.equal(root.get("id"), filter.getId()));
        if (filter.getUser() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("user").get("username"), filter.getUser().getUsername()));
        if (filter.getItem() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("item").get("title"), filter.getItem().getTitle()));
        if (filter.getReservationDate() != null)
            p.getExpressions().add(criteriaBuilder.equal(root.get("reservationDate"), filter.getReservationDate()));
        if (type != null)
            p.getExpressions().add(criteriaBuilder.equal(root.get("item").get("itemType"), type));

        return p;
    }

}
