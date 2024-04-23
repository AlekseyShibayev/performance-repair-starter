package com.company.app.infrastructure.jpa.entityfinder.model;

import com.company.app.infrastructure.jpa.entityfinder.model.dynamic_entity_graph.DynamicEntityGraph;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class CommonQuery<E> {

    private final Class<E> classGenericType;
    private Specification<E> specification;
    private Pageable pageable;
    private boolean readOnly;
    private DynamicEntityGraph dynamicEntityGraph = new DynamicEntityGraph();

    public CommonQuery<E> with(String... path) {
        dynamicEntityGraph.with(path);
        return this;
    }

}
