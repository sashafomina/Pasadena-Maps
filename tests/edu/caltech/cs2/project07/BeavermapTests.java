package edu.caltech.cs2.project07;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.caltech.cs2.datastructures.BeaverMapsGraph;
import edu.caltech.cs2.datastructures.Location;
import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ISet;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO: note to self, need to test remaining methods of graph -- adjacent & outgoing neighbors
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeavermapTests {
    private static String BEAVERMAP_GRAPH_SOURCE = "src/edu/caltech/cs2/datastructures/BeaverMapsGraph.java";
    private static JsonParser JSON_PARSER = new JsonParser();

    private static JsonElement fromFile(String filename) {
        try {
            return JSON_PARSER.parse(
                    new FileReader(
                            new File(filename)
                    )
            );
        } catch (IOException e) {
            return null;
        }
    }

    @Tag("C")
    @DisplayName("Does not use or import disallowed classes")
    @Test
    public void testForInvalidClasses() {
        List<String> graphDisallow = List.of("java.util.(?!Iterator)", "java.lang.reflect");
        Inspection.assertNoImportsOf(BEAVERMAP_GRAPH_SOURCE, graphDisallow);
        Inspection.assertNoUsageOf(BEAVERMAP_GRAPH_SOURCE, graphDisallow);
    }


    // Only use Caltech map and buildings to test for correctness
    @Tag("C")
    @Test
    public void testGetLocationByID() {
        BeaverMapsGraph bmg = new BeaverMapsGraph(
                "./data/caltech.buildings", "./data/caltech.waypoints", "./data/caltech.roads");
        JsonElement bs = fromFile("./data/caltech.buildings");
        for (JsonElement b : bs.getAsJsonArray()) {
            Location loc = new Location(b.getAsJsonObject());
            assertNotNull(bmg.getLocationByID(loc.id), "Location id " + loc.id + " not found by id");
        }
    }

    @Tag("C")
    @Test
    public void testGetLocationByName() {
        BeaverMapsGraph bmg = new BeaverMapsGraph(
                "./data/caltech.buildings", "./data/caltech.waypoints", "./data/caltech.roads");
        JsonElement bs = fromFile("./data/caltech.buildings");
        for (JsonElement b : bs.getAsJsonArray()) {
            Location loc = new Location(b.getAsJsonObject());
            if (loc.name != null) {
                assertNotNull(bmg.getLocationByName(loc.name), "Location " + loc.name + " not found by name");
            }
        }
    }

    @Tag("C")
    @DisplayName("Test getBuildings()")
    @ParameterizedTest(name = "Test getBuildings() on {0}")
    @CsvSource({
            "caltech.buildings, caltech.waypoints, caltech.roads",
            "pasadena.buildings, pasadena.waypoints, pasadena.roads",
    })
    public void testGetBuildings(String buildingsFile, String waypointsFile, String roadsFile) {
        BeaverMapsGraph bmg = new BeaverMapsGraph(
                "./data/" + buildingsFile, "./data/" + waypointsFile, "./data/" + roadsFile);
        Set<Location> buildings = new HashSet<>();
        JsonElement bs = fromFile("./data/" + buildingsFile);
        for (JsonElement b : bs.getAsJsonArray()) {
            Location loc = new Location(b.getAsJsonObject());
            buildings.add(loc);
        }
        MatcherAssert.assertThat(bmg.getBuildings(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(buildings.toArray()));
    }

    @Tag("C")
    @DisplayName("Test getClosestBuilding()")
    @ParameterizedTest(name = "Test getClosestBuilding() on {0}")
    @CsvSource({
            "caltech.buildings, caltech.waypoints, caltech.roads, caltech_closest.trace",
    })
    public void testGetClosestLocation(String buildingsFile, String waypointsFile, String roadsFile, String traceFile) {
        BeaverMapsGraph bmg = new BeaverMapsGraph(
                "./data/" + buildingsFile, "./data/" + waypointsFile, "./data/" + roadsFile);
        JsonElement bs = fromFile("./data/" + traceFile);
        for (JsonElement b : bs.getAsJsonArray()) {
            JsonObject curr = b.getAsJsonObject();
            Location center = bmg.getLocationByID(curr.get("center").getAsLong());
            Location closestExpected = bmg.getLocationByID(curr.get("closest").getAsLong());
            assertEquals(closestExpected, bmg.getClosestBuilding(center.lat, center.lon));
        }
    }

    @Tag("C")
    @DisplayName("BeaverMapsGraph implements required public methods")
    @Test
    public void testMethodsBeaverMapsGraph() {
        SortedSet<String> expected = new TreeSet<>(List.of(
                "getLocationByName", "getBuildings", "getClosestBuilding", "dfs", "dijkstra", "addVertex"
        ));
        SortedSet<String> actual = new TreeSet<>(
                Stream.of(BeaverMapsGraph.class.getDeclaredMethods())
                        .filter(Reflection.hasModifier("public"))
                        .map(x -> x.getName())
                        .collect(Collectors.toList()));
        MatcherAssert.assertThat(new ArrayList<>(actual),
                IsCollectionContaining.hasItems((expected.toArray())));
    }


    // Note: Pasadena map is WAY TOO LARGE to test all edges, don't try
    @Tag("C")
    @DisplayName("Tests nodes and edges in map for filename")
    @ParameterizedTest(name = "Test nodes in file {0}")
    @CsvSource({
            "caltech.buildings, caltech.waypoints, caltech.roads, caltech_edges.trace"
    })
    public void testNodesEdgesInMap(String bFile, String wFile, String roadsFile, String traceFile) {
        BeaverMapsGraph bmg = new BeaverMapsGraph("./data/" + bFile, "./data/" + wFile, "./data/" + roadsFile);

        List<Long> actualNodeIDs = new ArrayList<>();
        for (long nid : bmg.vertices()) {
            actualNodeIDs.add(nid);
        }

        JsonElement s = fromFile("./data/" + traceFile);
        for (JsonElement b : s.getAsJsonArray()) {
            JsonObject curr = b.getAsJsonObject();
            long locID = curr.get("id").getAsLong();
            Location loc = bmg.getLocationByID(locID);
            assertNotNull(loc, locID + " should be in graph, but is not");
            actualNodeIDs.remove(locID);

            JsonArray neighbors = curr.get("neighbors").getAsJsonArray();
            ISet<Long> actualNeighbors = bmg.neighbors(locID);
            List<Long> missingNeighbors = new ArrayList<>();
            for (JsonElement e : neighbors) {
                long neighborID = e.getAsLong();
                if (!actualNeighbors.remove(neighborID)) {
                    missingNeighbors.add(neighborID);
                }
            }

            // Use this instead of MatcherAssert to provide better errors (though I doubt they'll be needed)
            if (missingNeighbors.size() > 0) {
                fail(locID + " missing neighbors " + missingNeighbors);
            } else if (actualNeighbors.size() != 0) {
                fail(locID + " has extra neighbors " + actualNeighbors);
            }
        }

        assertEquals(0, actualNodeIDs.size(), "Graph has extra nodes: " + actualNodeIDs);
    }

    @Tag("B")
    @DisplayName("Test DFS radius search")
    @ParameterizedTest(name = "Test DFS on graph {0}")
    @CsvSource({
            "caltech.buildings, caltech.waypoints, caltech.roads, caltech_radius.trace",
            "pasadena.buildings, pasadena.waypoints, pasadena.roads, pasadena_radius.trace",
    })
    public void testDFSRadius(String buildingsFile, String waypointsFile, String roadsFile, String traceFile) {

        BeaverMapsGraph bmg = new BeaverMapsGraph(
                "./data/" + buildingsFile, "./data/" + waypointsFile, "./data/" + roadsFile);

        JsonElement s = fromFile("./data/" + traceFile);
        for (JsonElement b : s.getAsJsonArray()) {
            JsonObject curr = b.getAsJsonObject();
            long locID = curr.get("center").getAsLong();
            Location loc = bmg.getLocationByID(locID);
            double dist = curr.get("radius").getAsDouble();

            // Build expected list
            JsonArray locList = curr.get("locations").getAsJsonArray();
            Set<Location> expectedLoc = new HashSet<>();
            for (JsonElement e : locList) {
                expectedLoc.add(new Location(e.getAsJsonObject()));
            }
            ISet<Location> actualLoc = bmg.dfs(loc, dist);
            MatcherAssert.assertThat(actualLoc,
                    IsIterableContainingInAnyOrder.containsInAnyOrder(expectedLoc.toArray()));
        }
    }


    @Tag("A")
    @DisplayName("Test Dijkstra")
    @ParameterizedTest(name = "Test Dijkstra on graph {0}")
    @CsvSource({
            "caltech.buildings, caltech.waypoints, caltech.roads, caltech_paths.trace",
            "pasadena.buildings, pasadena.waypoints, pasadena.roads, pasadena_paths.trace",
    })
    public void testDijkstraBeaverMap(String buildingsFile, String waypointsFile, String roadsFile, String traceFile) throws FileNotFoundException {
        BeaverMapsGraph bmg = new BeaverMapsGraph(
                "./data/" + buildingsFile, "./data/" + waypointsFile, "./data/" + roadsFile);
        JsonElement s = fromFile("./data/" + traceFile);
        for (JsonElement b : s.getAsJsonArray()) {
            JsonObject curr = b.getAsJsonObject();
            Location start = bmg.getLocationByID(curr.get("start").getAsLong());
            Location target = bmg.getLocationByID(curr.get("target").getAsLong());

            // Build expected list
            JsonArray pathList = curr.get("path").getAsJsonArray();
            List<Long> expectedPathIDs = new ArrayList<>();
            for (JsonElement e : pathList) {
                expectedPathIDs.add(e.getAsLong());
            }

            IDeque<Location> actualPath = bmg.dijkstra(start, target);
            List<Long> actualPathIDs = new ArrayList<>();
            if (actualPath != null) {
                for (Location l : actualPath) {
                    actualPathIDs.add(l.id);
                }
                // Check that path is *exactly* equivalent
                MatcherAssert.assertThat(actualPathIDs,
                        IsIterableContainingInOrder.contains(expectedPathIDs.toArray()));
            }
            else if (expectedPathIDs.size() != 0) {
                fail("Found path from " + start.id + " to " + target.id + " where there is none.");
            }

        }
    }
}
