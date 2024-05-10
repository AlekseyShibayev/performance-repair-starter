package com.company.app.infrastructure.jpa.entityfinder;

import com.company.app.infrastructure.jpa.entityfinder.model.CommonQuery;
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
     */
    <E> List<E> findAllAsList(CommonQuery<E> commonQuery);

    /**
     * When you want pagination without int total, use Slice instead of Page, because Slice work without additional count(*) select.
     * support:
     * 1. entityGraph: v2 experimental, any @Entity, infinity depth
     * 2. specification: if specification exist - then add null safe predicate 2=2
     * 3. pageable: if pageable exist - then add sorting and pagination logic
     * 4. readOnly: if readOnly true - add readOnly as hint
     */
    <E> Slice<E> findAllAsSlice(CommonQuery<E> commonQuery);

}