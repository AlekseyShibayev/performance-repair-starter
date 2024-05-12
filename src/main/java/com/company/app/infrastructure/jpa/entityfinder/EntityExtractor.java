package com.company.app.infrastructure.jpa.entityfinder;

import java.util.List;

import com.company.app.infrastructure.jpa.entityfinder.model.CommonQuery;
import com.company.app.infrastructure.jpa.entityfinder.model.dynamic_entity_graph.DynamicEntityGraph;
import org.springframework.data.domain.Slice;


/**
 * This class have methods for select any @Entity.
 * Superstructure over Criteria API and Spring Data.
 */
public interface EntityExtractor {

    /**
     * Load one @Entity by EntityGraph.
     * If graph branch can be null - do 2 select. First - find by id, second - load by entity graph.
     */
    <E> E load(Class<E> entityClass, Object primaryKey, DynamicEntityGraph dynamicEntityGraph);

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