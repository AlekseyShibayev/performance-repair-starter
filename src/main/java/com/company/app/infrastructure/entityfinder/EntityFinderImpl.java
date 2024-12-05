package com.company.app.infrastructure.entityfinder;

import com.company.app.infrastructure.entityfinder.model.CommonQuery;
import com.company.app.infrastructure.entityfinder.model.DynamicEntityGraph;
import com.company.app.infrastructure.entityfinder.model.Ordering;
import com.company.app.infrastructure.entityfinder.model.ReturnType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.jpa.QueryHints;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;

import static com.company.app.infrastructure.entityfinder.model.ReturnType.LIST;
import static com.company.app.infrastructure.entityfinder.model.ReturnType.SLICE;


@Slf4j
@Repository
@RequiredArgsConstructor
public class EntityFinderImpl implements EntityFinder {

    private final EntityManager entityManager;

    public <E> List<E> findAllAsList(CommonQuery<E> commonQuery) {
        return findAllInner(commonQuery, LIST);
    }

    public <E> Slice<E> findAllAsSlice(CommonQuery<E> commonQuery) {
        doSliceCheck(commonQuery);

        List<E> resultList = findAllInner(commonQuery, SLICE);

        Pageable pageable = commonQuery.getPageable();
        int pageSize = pageable.getPageSize();
        boolean hasNext = pageable.isPaged() && resultList.size() > pageSize;
        return new SliceImpl<>(hasNext ? resultList.subList(0, pageSize) : resultList, pageable, hasNext);
    }

    private <E> void doSliceCheck(CommonQuery<E> commonQuery) {
        if (commonQuery.getPageable() == null) {
            throw new IllegalArgumentException("pageable must be");
        }
    }

    private <E> List<E> findAllInner(CommonQuery<E> commonQuery, ReturnType returnType) {
        log.debug("try to prepare query from: [{}]", commonQuery);

        Class<E> entityClass = commonQuery.getClassGenericType();
        Pageable pageable = commonQuery.getPageable();
        DynamicEntityGraph dynamicEntityGraph = commonQuery.getDynamicEntityGraph();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);

        Specification<E> specification = addNullSafePredicate(commonQuery.getSpecification());

        Ordering<E> ordering = commonQuery.getOrdering();
        if (ordering != null) {
            specification = specification.and(ordering);
        } else if (pageable != null && pageable.getSort().isSorted()) {
            List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, criteriaBuilder);
            criteriaQuery.orderBy(orders);
        }

        criteriaQuery.select(root)
                .where(specification.toPredicate(root, criteriaQuery, criteriaBuilder));

        TypedQuery<E> typedQuery = entityManager.createQuery(criteriaQuery);

        if (dynamicEntityGraph.exist()) {
            EntityGraph<E> entityGraph = entityManager.createEntityGraph(entityClass);
            dynamicEntityGraph.prepareGraph(entityGraph);
            typedQuery.setHint(QueryHints.HINT_LOADGRAPH, entityGraph);
        }

        if (pageable != null) {
            switch (returnType) {
                case SLICE -> setOffsetAndLimit(pageable, typedQuery);
                case LIST -> setLimit(pageable, typedQuery);
                default -> throw new UnsupportedOperationException("[%s] not supported yet".formatted(returnType));
            }
        }

        if (commonQuery.isReadOnly()) {
            typedQuery.setHint(QueryHints.HINT_READONLY, true);
        }

        Integer maximumExecutionTime = commonQuery.getMaximumExecutionTime();
        if (maximumExecutionTime != null && maximumExecutionTime > 0) {
            typedQuery.setHint(QueryHints.SPEC_HINT_TIMEOUT, maximumExecutionTime);
        }

        return typedQuery.getResultList();
    }

    private <E> void setLimit(Pageable pageable, TypedQuery<E> typedQuery) {
        int pageSize = pageable.getPageSize();
        typedQuery.setMaxResults(pageSize);
    }

    private <E> void setOffsetAndLimit(Pageable pageable, TypedQuery<E> typedQuery) {
        int pageSize = pageable.getPageSize();
        int offset = pageable.getPageNumber() > 0 ? pageable.getPageNumber() * pageSize : 0;
        typedQuery.setMaxResults(pageSize + 1);
        typedQuery.setFirstResult(offset);
    }

    private <E> Specification<E> addNullSafePredicate(Specification<E> specification) {
        Specification<E> nullSafePredicate = (root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.literal(2), 2);
        return nullSafePredicate.and(specification);
    }

    @Override
    public <E> List<E> findByIds(List<Long> ids, Class<E> entityClass) {
        try (var session = entityManager.unwrap(Session.class)) {
            return session.byMultipleIds(entityClass)
                    .enableSessionCheck(true)
                    .withBatchSize(1024) // because hibernate.query.in_clause_parameter_padding=true
                    .multiLoad(ids);
        }
    }

}