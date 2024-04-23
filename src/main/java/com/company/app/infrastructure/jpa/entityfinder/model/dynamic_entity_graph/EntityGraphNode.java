package com.company.app.infrastructure.jpa.entityfinder.model.dynamic_entity_graph;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class EntityGraphNode {

    private String name;
    private EntityGraphNode child;
    private List<EntityGraphNode> nodeList = new ArrayList<>();

}
