package com.company.app.infrastructure.jpa.entityfinder.model.dynamic_entity_graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public void add(EntityGraphNode child) {
        nodeList.add(child);
    }

    public Optional<EntityGraphNode> getChildIfExist(EntityGraphNode child) {
        return nodeList.stream()
            .filter(node -> node.getName().equals(child.getName()))
            .findFirst();
    }

}
