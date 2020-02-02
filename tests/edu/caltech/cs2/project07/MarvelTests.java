package edu.caltech.cs2.project07;

import edu.caltech.cs2.datastructures.Graph;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("C")
public class MarvelTests {
    private static Graph<String, Integer> MARVEL_GRAPH;

    @BeforeAll
    public static void populateMarvelGraph() {
        MARVEL_GRAPH = new Graph();

        Set<String> characters = new HashSet<>();
        Map<String, List<String>> books = new HashMap<>();

        try {
            MarvelParser.parseData("data/marvel.tsv", characters, books);
        } catch (Exception e) {
            fail("Could not read input file.");
        }

        for (String c : characters) {
            MARVEL_GRAPH.addVertex(c);
        }

        for (String book : books.keySet()) {
            for (String c1 : books.get(book)) {
                for (String c2 : books.get(book)) {
                    if (!c1.equals(c2)) {
                        Integer num = MARVEL_GRAPH.adjacent(c1, c2);
                        if (num == null) {
                            num = 0;
                        }
                        MARVEL_GRAPH.addEdge(c1, c2, num + 1);
                    }
                }
            }
        }
    }

    @Test
    @Order(0)
    public void testSingletons() {
        List<String> singletons = new ArrayList<>();
        for (String c : MARVEL_GRAPH.vertices()) {
            if (MARVEL_GRAPH.neighbors(c).isEmpty()) {
                singletons.add(c);
            }
        }
        loadAndMatch(singletons, "tests/data/singletons_output");
    }

    @Test
    @Order(1)
    public void testPopular() {
        List<String> populars = new ArrayList<>();
        for (String c : MARVEL_GRAPH.vertices()) {
            if (MARVEL_GRAPH.neighbors(c).size() > 20) {
                populars.add(c);
            }
        }
        loadAndMatch(populars, "tests/data/popular_output");
    }

    @Test
    @Order(2)
    public void testNoLoops() {
        for (String c : MARVEL_GRAPH.vertices()) {
            assertEquals(null, MARVEL_GRAPH.adjacent(c, c), "There is a loop (self-edge) in the graph");
        }
    }


    @Test
    @Order(3)
    public void testCommon() {
        Assertions.assertTimeout(Duration.ofMillis(1500), this::runCommon);
    }

    public void runCommon() {
        List<String> common = new ArrayList<>();
        for (String c1 : MARVEL_GRAPH.vertices()) {
            for (String c2 : MARVEL_GRAPH.neighbors(c1)) {
                Integer edge = MARVEL_GRAPH.adjacent(c1, c2);
                Integer symedge = MARVEL_GRAPH.adjacent(c2, c1);
                assertNotEquals(null, edge, "An existing edge is null");
                assertNotEquals(null, symedge, "An existing edge is not symmetric in the graph");
                MARVEL_GRAPH.removeEdge(c1, c2);
                assertEquals(null, MARVEL_GRAPH.adjacent(c1, c2), "An edge is non-null after removal");
                assertEquals(symedge, MARVEL_GRAPH.adjacent(c2, c1), "An edge is null after its symmetric edge is removed");
                MARVEL_GRAPH.removeEdge(c2, c1);
                assertEquals(null, MARVEL_GRAPH.adjacent(c2, c1), "An edge is non-null after removal");
                if (edge > 80) {
                    if (c1.compareTo(c2) < 0) {
                        common.add(c1.strip() + " --" + edge + "-- " + c2.strip());
                    }
                    else {
                        common.add(c2.strip() + " --" + edge + "-- " + c1.strip());
                    }
                }
            }
            assertTrue(MARVEL_GRAPH.neighbors(c1).isEmpty(), "After removing all of a vertex's neighbors, neighbors() is non-empty");
        }

        loadAndMatch(common, "tests/data/common_output");


    }

    private void loadAndMatch(List<String> actual, String filename) {
        List<String> expected = new ArrayList<>();
        Scanner fr = null;
        try {
            fr = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            fail("Could not open test results file.");
        }

        while (fr.hasNextLine()) {
            String l = fr.nextLine();
            // is edge - sort to remove dependence on directionality for correctness
            if (l.contains("--")) {
                String[] spl = l.split("--");
                String v1 = spl[0].strip();
                String v2 = spl[2].strip();
                String e = spl[1];
                if (v1.compareTo(v2) < 0) {
                    l = v1 + " --" + e + "-- " + v2;
                }
                else {
                    l = v2 + " --" + e + "-- " + v1;
                }
            }
            expected.add(l);
        }

        MatcherAssert.assertThat(actual,
                IsIterableContainingInAnyOrder.containsInAnyOrder(expected.toArray()));
    }
}