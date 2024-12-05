package com.company.app.infrastructure.entityfinder;

import com.company.app.infrastructure.entityfinder.model.CommonQuery;
import org.springframework.data.domain.Slice;

import java.util.List;

/**
 * This class have methods for select any @Entity.
 * Superstructure over Criteria API and Spring Data.
 */
public interface EntityFinder {

    /**
     * Base method, analog of Spring Data findAll(), but expanding it.
     * support:
     * 1. entityGraph: v2 experimental, any @Entity, infinity depth
     * 2. specification: if specification exist - then add null safe predicate 2=2
     * 3. pageable: if pageable exist - then add sorting and pagination logic, pagination as limit only
     * 4. readOnly: if readOnly true - add readOnly as hint
     * 5. timeout: if maximumExecutionTime > 0 - add timeout hint. Time in ms and must be 500 ms or more
     */
    <E> List<E> findAllAsList(CommonQuery<E> commonQuery);

    /**
     * When you want pagination without int total, use Slice instead of Page, because Slice work without additional count(*) select.
     * support:
     * 1. entityGraph: v2 experimental, any @Entity, infinity depth
     * 2. specification: if specification exist - then add null safe predicate 2=2
     * 3. pageable: if pageable exist - then add sorting and pagination logic
     * 4. readOnly: if readOnly true - add readOnly as hint
     * 5. timeout: if maximumExecutionTime > 0 - add timeout hint. Time in ms and must be 500 ms or more
     */
    <E> Slice<E> findAllAsSlice(CommonQuery<E> commonQuery);

    /**
     * Use this method if part of entities loaded before and contains in first level cache.
     * Hibernate execute IN query only with absent entities.
     * Return naked Entity, need load LAZY fields additionally.
     */
    <E> List<E> findByIds(List<Long> ids, Class<E> entityClass);

}