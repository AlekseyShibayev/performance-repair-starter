package com.company.app.infrastructure.jpa.entityfinder.model.dynamic_entity_graph;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class EntityGraphNode {

    private String name;
    private EntityGraphNode child;
    private List<EntityGraphNode> nodeList = new ArrayList<>();

}
