package com.company.app.infrastructure.jpa.entityfinder.model.dynamic_entity_graph;

import javax.persistence.EntityGraph;
import javax.persistence.Subgraph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;


/**
 * Human-readable api for preparing JPA entity graph.
 */
@NoArgsConstructor
public class DynamicEntityGraph {

    @Getter(value = AccessLevel.PACKAGE)
    private final List<EntityGraphNode> entityGraphNodes = new ArrayList<>();

    public DynamicEntityGraph with(String... path) {
        entityGraphNodes.add(createNode(path));
        return this;
    }

    public boolean exist() {
        return !CollectionUtils.isEmpty(entityGraphNodes);
    }

    public <E> void prepareGraph(EntityGraph<E> entityGraph) {
        EntityGraphNode root = createTreeAndGetRoot();
        fillEntityGraph(entityGraph, root);
    }

    /**
     * private methods
     */
    private EntityGraphNode createNode(String... path) {
        if (path.length < 1) {
            throw new IllegalArgumentException("For preparing entity graph need minimum one parameter.");
        }
        List<EntityGraphNode> tempNodeList = mapToTempNodeList(path);
        bindNodes(tempNodeList);
        return getMainNode(tempNodeList);
    }

    private List<EntityGraphNode> mapToTempNodeList(String[] path) {
        return Arrays.stream(path)
            .map(name -> new EntityGraphNode().setName(name))
            .toList();
    }

    private void bindNodes(List<EntityGraphNode> tempNodeList) {
        for (int i = 1; i < tempNodeList.size(); i++) {
            EntityGraphNode childNode = tempNodeList.get(i);
            EntityGraphNode parentNode = tempNodeList.get(i - 1);
            parentNode.setChild(childNode);
        }
    }

    private EntityGraphNode getMainNode(List<EntityGraphNode> tempNodeList) {
        return tempNodeList.get(0);
    }

    EntityGraphNode createTreeAndGetRoot() {
        EntityGraphNode root = new EntityGraphNode().setName("root");
        entityGraphNodes.forEach(node -> recursionCreate(root, node));
        return root;
    }

    private void recursionCreate(EntityGraphNode parent, EntityGraphNode child) {
        List<EntityGraphNode> parentNodeList = parent.getNodeList();
        if (parentNotContainsChild(parent, child)) {
            parentNodeList.add(child);
        } else {

            for (EntityGraphNode node : parentNodeList) {
                if (node.getName().equals(child.getName())) {
                    child.setNodeList(node.getNodeList());
                }
            }

        }

        if (child.getChild() != null) {
            recursionCreate(child, child.getChild());
        }
    }

    private boolean parentNotContainsChild(EntityGraphNode parent, EntityGraphNode child) {
        return parent.getNodeList().stream()
            .noneMatch(node -> node.getName().equals(child.getName()));
    }

    private <E> void fillEntityGraph(EntityGraph<E> entityGraph, EntityGraphNode root) {
        List<EntityGraphNode> rootNodeList = root.getNodeList();
        for (EntityGraphNode node : rootNodeList) {
            if (node.getNodeList().isEmpty()) {
                entityGraph.addAttributeNodes(node.getName());
            } else {
                fillSubgraph(entityGraph, node);
            }
        }
    }

    private <E> void fillSubgraph(EntityGraph<E> entityGraph, EntityGraphNode node) {
        Subgraph<E> subgraph = entityGraph.addSubgraph(node.getName());
        List<EntityGraphNode> nodeList = node.getNodeList();
        for (EntityGraphNode child : nodeList) {
            if (child.getNodeList().isEmpty()) {
                subgraph.addAttributeNodes(child.getName());
            } else {
                recursionFill(subgraph, child);
            }
        }
    }

    private <E> void recursionFill(Subgraph<E> subgraph, EntityGraphNode node) {
        List<EntityGraphNode> nodeList = node.getNodeList();
        if (nodeList.isEmpty()) {
            subgraph.addAttributeNodes(node.getName());
        } else {
            for (EntityGraphNode child : nodeList) {
                recursionFill(subgraph.addSubgraph(node.getName()), child);
            }
        }
    }

}