package edu.caltech.cs2.project07;

import edu.caltech.cs2.datastructures.BeaverMapsGraph;
import edu.caltech.cs2.datastructures.Graph;
import edu.caltech.cs2.datastructures.Location;
import edu.caltech.cs2.interfaces.IGraph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraphMaker {
    public static BeaverMapsGraph transformToLocations(IGraph<Integer, Integer> g) {
        BeaverMapsGraph ng = new BeaverMapsGraph();
        for (Integer v : g.vertices()) {
            ng.addVertex(new Location((long)v));
        }

        for (Integer v : g.vertices()) {
            for (Integer v2 : g.neighbors(v)) {
                ng.addEdge((long)v, (long)v2, (double)g.adjacent(v, v2));
            }
        }

        return ng;
    }

    // A very simple graph.
    public static IGraph<Integer, Integer> graph1() {
        IGraph<Integer, Integer> g = new Graph<>();
        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);
        g.addEdge(1, 2, 10);
        g.addEdge(2, 3, 20);

        return g;
    }

    // A linear graph on n vertices.
    public static IGraph<Integer, Integer> graph2(int n) {
        IGraph<Integer, Integer> g = new Graph<>();
        for (int i = 0; i < n; i++) {
            assertTrue(g.addVertex(i), "Adding a new vertex should return true");
        }

        for (int i = 0; i < n - 1; i++) {
            assertTrue(g.addEdge(i, i + 1, i),
                    "Adding a new edge should return true"
            );
        }
        return g;
    }

    public static void vertexTest(IGraph<Integer, Integer> g, int n) {
        for (int i = 0; i < n; i++)
            assertEquals(true, g.vertices().contains(i), "Graphs should contain added vertices");
    }

    // Verify that a graph we're given looks reasonably like graph 2
    public static void graph2Test(IGraph<Integer, Integer> g, int n) {
        vertexTest(g, n);
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1; j++) {
                Integer e = g.adjacent(i, j);
                if (j == i + 1)
                    assertEquals(i, e, "Retrieved edge weight is not correct");
                else
                    assertEquals(null, e, "An edge that was never added returned a non-null weight");
            }
        }
    }

    // A tournament on n vertices; has an edge from i to j iff j<i
    public static IGraph<Integer, Integer> graph3(int n) {
        IGraph<Integer, Integer> g = new Graph<>();
        for (int i = 0; i < n; i++) {
            assertEquals(true, g.addVertex(i), "Adding a new vertex should return true");
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                assertEquals(true, g.addEdge(i, j, i), "Adding a new edge should return true");
            }
        }
        return g;
    }

    // Verify that a graph we're given looks reasonably like graph 3
    public static void graph3Test(IGraph<Integer, Integer> g, int n) {
        vertexTest(g, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Integer e = g.adjacent(i, j);
                if (j < i) {
                    assertEquals(i, e, "Retrieved edge weight is not correct");
                }
                else {
                    assertEquals(null, e, "An edge that was never added returned a non-null weight");
                }
            }
        }
    }

    // A complete graph on n vertices
    public static IGraph<Integer, Integer> graph4(int n) {
        IGraph<Integer, Integer> g = new Graph<>();
        for (int i = 0; i < n; i++) {
            assertTrue(g.addVertex(i), "Adding a new vertex should return true");
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j == i) continue;
                assertTrue(g.addEdge(i, j, i),
                        "Adding a new edge should return true"
                );
            }
        }
        return g;
    }

    // Verify that a graph we're given looks reasonably like graph 4
    public static void graph4Test(IGraph<Integer, Integer> g, int n) {
        vertexTest(g, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Integer e = g.adjacent(i,
                        j);
                if (i != j) {
                    assertEquals(i, e, "Retrieved edge weight is not correct");
                }
                else {
                    assertEquals(null, e, "An edge that was never added returned a non-null weight");
                }
            }
        }
    }

    // Two disjoint complete graphs.
    public static IGraph<Integer, Integer> graph5(int n) {
        IGraph<Integer, Integer> g = new Graph<>();
        for (int i = 0; i < n; i++) {
            assertTrue(g.addVertex(i), "Adding a new vertex should return true");
        }
        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < n / 2; j++) {
                if (i != j) {
                    assertTrue(g.addEdge(i, j, i),
                            "Adding a new edge should return true"
                    );
                }
            }
        }
        for (int i = n / 2; i < n; i++) {
            for (int j = n / 2; j < n; j++) {
                if (i != j) {
                    assertTrue(g.addEdge(i, j, i),
                            "Adding a new edge should return true"
                    );
                }
            }
        }

        return g;
    }

    // Verify that a graph we're given looks reasonably like graph 5
    public static void graph5Test(IGraph<Integer, Integer> g, int n) {
        vertexTest(g, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Integer e = g.adjacent(i, j);
                if ((i < n / 2 && j < n / 2 || i >= n / 2 && j >= n / 2) && i != j) {
                    assertEquals(i, e, "Retrieved edge weight is not correct");
                }
                else {
                    assertEquals(null, e, "An edge that was never added returned a non-null weight");
                }
            }
        }
    }

    /* A graph on which Dijkstra's should fail on a search from
     * vertex 0 to vertex 2. Optimal path is 0->3->2 (length 1), but Dijkstra's
     * should give us 0->1->2 (length 2).
     */
    public static IGraph<Integer, Integer> dijkstraFail() {
        IGraph<Integer, Integer> g = new Graph<>();
        g.addVertex(0);
        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);

        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(0, 3, 5);
        g.addEdge(3, 2, -4);
        return g;
    }
}