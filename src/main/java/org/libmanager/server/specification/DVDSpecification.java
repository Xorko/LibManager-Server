package org.libmanager.server.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.libmanager.server.entity.DVD;
import org.springframework.data.jpa.domain.Specification;

public class DVDSpecification implements Specification<DVD> {

    private final DVD filter;
    private final String status;

    public DVDSpecification(DVD filter, String status) {
        super();
        this.filter = filter;
        this.status = status;
    }

    @Override
    public Predicate toPredicate(Root<DVD> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate p = criteriaBuilder.disjunction();

        if (filter.getTitle() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("title"), filter.getTitle()));

        if (filter.getAuthor() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("author"), filter.getAuthor()));

        if (filter.getGenre() != null)
            p.getExpressions().add(criteriaBuilder.equal(root.get("genre"), filter.getGenre()));

        if (filter.getReleaseDate() != null)
            p.getExpressions().add(criteriaBuilder.equal(root.get("releaseDate"), filter.getReleaseDate()));

        if (status != null) {
            p.getExpressions().add(criteriaBuilder.equal(root.get("status"), Boolean.parseBoolean(status)));
        }

        return p;
    }

}
