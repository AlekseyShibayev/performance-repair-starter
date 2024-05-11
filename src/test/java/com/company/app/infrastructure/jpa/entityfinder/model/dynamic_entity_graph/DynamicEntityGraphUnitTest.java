package com.company.app.infrastructure.jpa.entityfinder.model.dynamic_entity_graph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class DynamicEntityGraphUnitTest {

    @Test
    void test_success_create() {
        DynamicEntityGraph dynamicEntityGraph = new DynamicEntityGraph()
            .with("type")
            .with("status")
            .with("children", "type")
            .with("children", "status");

        Assertions.assertEquals(4, dynamicEntityGraph.getEntityGraphNodes().size());
    }

    @Test
    void test_negative_create() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new DynamicEntityGraph().with());
    }

    @Test
    void test_create_root_1() {
        DynamicEntityGraph dynamicEntityGraph = new DynamicEntityGraph()
            .with("1", "1.1", "1.1.1", "1.1.1.1")
            .with("1", "1.1", "1.1.1", "1.1.1.2")
            .with("1", "1.1", "1.1.2")
            .with("1", "1.2", "1.2.1")
            .with("1", "1.2", "1.2.2")
            .with("2", "2.1", "2.1.1")
            .with("2", "2.1", "2.1.2")
            .with("2", "2.1", "2.1.3")
            .with("2", "2.2", "2.2.1")
            .with("2", "2.2", "2.2.2");

        EntityGraphNode root = dynamicEntityGraph.createTreeAndGetRoot();

        Assertions.assertEquals("root", root.getName());
        Assertions.assertEquals(2, root.getNodeList().size());

        EntityGraphNode one = root.getNodeList().get(0);
        Assertions.assertEquals("1", one.getName());
        Assertions.assertEquals(2, one.getNodeList().size());

        EntityGraphNode oneOne = one.getNodeList().get(0);
        Assertions.assertEquals("1.1", oneOne.getName());
        Assertions.assertEquals(2, oneOne.getNodeList().size());

        EntityGraphNode oneOneOne = oneOne.getNodeList().get(0);
        Assertions.assertEquals("1.1.1", oneOneOne.getName());
        Assertions.assertEquals(2, oneOneOne.getNodeList().size());
    }

    @Test
    void test_create_root() {
        DynamicEntityGraph dynamicEntityGraph = new DynamicEntityGraph()
            .with("1")
            .with("2")
            .with("3")
            .with("1", "1.1")
            .with("2", "2.1")
            .with("3", "3.1")
            .with("1", "1.1", "1.1.1")
            .with("1", "1.1", "1.1.2")
            .with("1", "1.2", "1.2.1")
            .with("1", "1.2", "1.2.2")
            .with("2", "2.1", "2.1.1")
            .with("2", "2.1", "2.1.2")
            .with("2", "2.1", "2.1.3")
            .with("3", "3.1", "3.1.1");

        EntityGraphNode root = dynamicEntityGraph.createTreeAndGetRoot();
        Assertions.assertEquals("root", root.getName());
        Assertions.assertEquals(3, root.getNodeList().size());

        EntityGraphNode one = root.getNodeList().get(0);
        Assertions.assertEquals("1", one.getName());
        Assertions.assertEquals(2, one.getNodeList().size());
    }

}