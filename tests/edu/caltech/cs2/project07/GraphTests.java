package edu.caltech.cs2.project07;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.caltech.cs2.datastructures.Graph;
import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.interfaces.IGraph;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("C")
public class GraphTests {
    private static final String GRAPH_SOURCE = "src/edu/caltech/cs2/datastructures/Graph.java";

    @DisplayName("Does not use or import disallowed classes")
    @Test
    public void testForInvalidClasses() {
        List<String> graphDisallow = List.of("java.util.(?!Iterator)", "java.io", "java.lang.reflect");
        Inspection.assertNoImportsOf(GRAPH_SOURCE, graphDisallow);
        Inspection.assertNoUsageOf(GRAPH_SOURCE, graphDisallow);
    }

    @Test
    public void emptyGraphTest() {
        IGraph<String, String> g = new Graph<>();
        assertTimeout(Duration.ofMillis(300), () ->
                assertEquals(0, g.vertices().size(), "Empty graph should have no vertices")
        );
    }

    /**
     * Ensure that we can create a small graph with some edges.
     */
    @Test
    public void secondCreateTest() {
        IGraph<String, Integer> g = new Graph<>();

        assertTimeout(Duration.ofMillis(300), () -> {
            assertTrue(g.addVertex("0"), "Should be able to add a vertex");
            assertTrue(g.addVertex("1"), "Should be able to add a vertex");
            assertTrue(g.addVertex("2"), "Should be able to add a vertex");
            assertTrue(g.addVertex("3"), "Should be able to add a vertex");

            assertTrue(g.addEdge("0", "1", 2), "Should be able to add an edge");
            assertTrue(g.addEdge("2", "1", 4), "Should be able to add an edge");
            assertTrue(g.addEdge("1", "2", 3), "Should be able to add an edge");
            assertTrue(g.addEdge("1", "3", 1), "Should be able to add an edge");
            assertTrue(g.addEdge("3", "1", -1), "Should be able to add an edge");


            assertEquals(g.vertices().size(), 4, "Graph should have correct number of vertices");

            assertEquals(2, (int) g.adjacent("0", "1"), "Edges added should all be present");
            assertEquals(4, (int) g.adjacent("2", "1"), "Edges added should all be present");
            assertEquals(3, (int) g.adjacent("1", "2"), "Edges added should all be present");
            assertEquals(1, (int) g.adjacent("1", "3"), "Edges added should all be present");
            assertEquals((int) g.adjacent("3", "1"), -1, "Edges added should all be present");
        });
    }

    @Test
    public void addIllegalEdgeTest() {
        IGraph<String, Integer> g = new Graph<>();
        assertThrows(IllegalArgumentException.class, () -> g.addEdge("", "", 1));
    }

    @Test
    public void addIllegalEdgeTest2() {
        IGraph<String, Integer> g = new Graph<>();
        assertTrue(g.addVertex("0"));
        assertThrows(IllegalArgumentException.class, () -> g.addEdge("0", "", 1));
    }

    @Test
    public void addIllegalEdgeTest3() {
        IGraph<String, Integer> g = new Graph<>();
        assertTrue(g.addVertex("0"));
        assertThrows(IllegalArgumentException.class, () -> g.addEdge("", "0", 1));
    }

    @Test
    public void simpleAddTest() {
        assertTimeout(Duration.ofMillis(300), () -> {
            IGraph<String, Integer> g = new Graph<>();

            assertTrue(g.addVertex("1"), "Should be able to add a vertex");
            assertEquals(1, g.vertices().size(), "Should have correct number of vertices");
            assertTrue(g.addVertex("2"), "Should be able to add a vertex");
            assertEquals(2, g.vertices().size(), "Should have correct number of vertices");
            assertTrue(g.addVertex("3"), "Should be able to add a vertex");
            assertEquals(3, g.vertices().size(), "Should have correct number of vertices");

            assertTrue(g.addEdge("1", "2", 5), "Should be able to add new edge");
            assertFalse(g.addEdge("1", "2", 5), "Should not be able to add an existing edge");
            assertFalse(g.addEdge("1", "2", 3), "Should not be able to add an existing edge");

            assertTrue(g.addEdge("2", "1", 5), "Should be able to add new edge");
            assertFalse(g.addEdge("2", "1", 5), "Should not be able to add an existing edge");
            assertFalse(g.addEdge("2", "1", 3), "Should not be able to add an existing edge");

            assertTrue(g.addEdge("1", "3", 5), "Should be able to add new edge");
            assertFalse(g.addEdge("1", "3", 5), "Should not be able to add an existing edge");
            assertFalse(g.addEdge("1", "3", 3), "Should not be able to add an existing edge");

            assertNotNull(g.adjacent("1", "2"), "Edge should exist in the graph");
            assertNotNull(g.adjacent("2", "1"), "Edge should exist in the graph");
            assertNotNull(g.adjacent("1", "3"), "Edge should exist in the graph");
            assertNull(g.adjacent("2", "3"), "Edge should not exist in graph");
            assertNull(g.adjacent("3", "1"), "Edge should not exist in graph");
        });
    }

    @Test
    public void simpleRemoveTest() {
        assertTimeout(Duration.ofMillis(300), () -> {
            IGraph<String, Integer> g = new Graph<>();

            assertTrue(g.addVertex("1"), "Should be able to add vertex");
            assertTrue(g.addVertex("2"), "Should be able to add vertex");
            assertTrue(g.addEdge("1", "2", 5), "Should be able to add edge");
            assertEquals(5, (int) g.adjacent("1", "2"), "Added edge should be present in graph");
            assertTrue(g.removeEdge("1", "2"), "Should be able to remove an edge from the graph");
            assertFalse(g.removeEdge("1", "2"), "Should not be able to remove already-removed edge");
        });
    }

    @Test
    public void creationTest() {
        assertTimeout(Duration.ofMillis(500), () -> {
            GraphMaker.graph1();
            GraphMaker.graph2Test(GraphMaker.graph2(100), 100);
            GraphMaker.graph3Test(GraphMaker.graph3(100), 100);
            GraphMaker.graph4Test(GraphMaker.graph4(100), 100);
            GraphMaker.graph5Test(GraphMaker.graph5(100), 100);
        });
    }

    /* Make sure that they test .equals, rather than ==, to determine
     * if an edge is already there. */
    @Test
    public void equalsTest() {
        IGraph<Integer, Integer> g = new Graph<>();
        Integer i1 = 0;
        Integer i2 = 0;
        assertTrue(g.addVertex(i1));
        assertFalse(g.addVertex(i2));
    }

    @Test
    public void adjacentStressTest() {
        assertTimeout(Duration.ofMillis(2200), () -> {
            IGraph<Integer, Integer> g = GraphMaker.graph4(400);
            for (int i = 0; i < 400; i++)
                for (int j = 0; j < 400; j++)
                    if (i != j)
                        assertNotNull(g.adjacent(i, j), "Edge should be present in the graph");
        });
    }

    @Test
    public void testNeighbors() {
        assertTimeout(Duration.ofMillis(300), () -> {
            IGraph<Integer, Integer> g = GraphMaker.graph3(10);
            Set<Integer> vertices = new HashSet<Integer>();

            for (int i = 0; i < 10; i++) {
                MatcherAssert.assertThat(g.neighbors(i),
                IsIterableContainingInAnyOrder.containsInAnyOrder(vertices.toArray()));
                vertices.add(i);
            }
        });
    }

    @Test
    public void stringEdgeTest() {
        assertTimeout(Duration.ofMillis(300), () -> {
            IGraph<Integer, Integer> g = new Graph<>();

            assertTrue(g.addVertex(1), "Should be able to add vertex");
            assertTrue(g.addVertex(2), "Should be able to add vertex");
            assertTrue(g.addVertex(3), "Should be able to add vertex");
            assertTrue(g.addEdge(1, 2, 0), "Should be able to add edge");
            assertTrue(g.addEdge(1, 3, 0), "Should be able to add edge");
            assertTrue(g.addEdge(2, 3, 0), "Should be able to add edge");
            assertNotNull(g.adjacent(1, 2), "Added edge should be present in graph");
            assertNotNull(g.adjacent(1, 3), "Added edge should be present in graph");
            assertNotNull(g.adjacent(2, 3), "Added edge should be present in graph");
            assertTrue(g.removeEdge(1, 2), "Should be able to remove an edge from the graph");
            assertFalse(g.removeEdge(1, 2), "Should not be able to remove already-removed edge");
            assertTrue(g.removeEdge(1, 3), "Should be able to remove an edge from the graph");
            assertFalse(g.removeEdge(1, 3), "Should not be able to remove already-removed edge");
            assertTrue(g.removeEdge(2, 3), "Should be able to remove an edge from the graph");
            assertFalse(g.removeEdge(2, 3), "Should not be able to remove already-removed edge");
        });
    }
}