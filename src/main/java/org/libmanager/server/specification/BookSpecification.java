package org.libmanager.server.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.libmanager.server.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification implements Specification<Book> {

    private final Book filter;
    private final String status;

    public BookSpecification(Book filter, String status) {
        super();
        this.filter = filter;
        this.status = status;
    }

    @Override
    public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate p = criteriaBuilder.disjunction();

        if (filter.getTitle() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("title"), filter.getTitle()));

        if (filter.getAuthor() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("author"), filter.getAuthor()));

        if (filter.getPublisher() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("publisher"), filter.getPublisher()));

        if (filter.getIsbn() != null)
            p.getExpressions().add(criteriaBuilder.like(root.get("isbn"), filter.getIsbn()));

        if (filter.getGenre() != null)
            p.getExpressions().add(criteriaBuilder.equal(root.get("genre"), filter.getGenre()));

        if (filter.getReleaseDate() != null)
            p.getExpressions().add(criteriaBuilder.equal(root.get("releaseDate"), filter.getReleaseDate()));

        if (status != null)
            p.getExpressions().add(criteriaBuilder.equal(root.get("status"), Boolean.parseBoolean(status)));

        return p;
    }
}
