package com.company.app.infrastructure.entityfinder.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@RequiredArgsConstructor
public class CommonQuery<E> {

    private final Class<E> classGenericType;
    private Specification<E> specification;

    /**
     * If ordering != null pageable.sort will be ignored.
     */
    private Ordering<E> ordering;
    private Pageable pageable;

    private boolean readOnly;
    private Integer maximumExecutionTime; // Time in ms and must be 500 ms or more
    private DynamicEntityGraph dynamicEntityGraph = new DynamicEntityGraph();


    /**
     * This method need for modify CommonQuery by parameters for entity graph.
     * Entity graph need for eager loading fields for entity <E>.
     * This method takes graph branches as field names.
     * <p>
     *        E
     *      / |
     *     A  B
     *   / |
     *  C  D
     * <p>
     * For load E with A, C, B use:
     * .with(A, C)
     * .with(B)
     */
    public CommonQuery<E> with(String... path) {
        dynamicEntityGraph.with(path);
        return this;
    }

    /**
     * Encapsulate boilerplate.
     */
    public CommonQuery<E> readOnly() {
        this.readOnly = true;
        return this;
    }

}
