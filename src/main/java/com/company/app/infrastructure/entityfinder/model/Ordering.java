package com.company.app.infrastructure.entityfinder.model;

import org.springframework.data.jpa.domain.Specification;


@FunctionalInterface
public interface Ordering<E> extends Specification<E> {

    /**
     * When Pageable.Sort not enough - tell how you want sort result set.
     * For example when column for ordering missing in result set.
     * Or when relation one to many and result set have many rows for one result of your original @Entity.
     * <p>
     * Return null, if not need add predicate.
     */

}
