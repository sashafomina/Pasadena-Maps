package edu.caltech.cs2.datastructures;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;
import edu.caltech.cs2.interfaces.ISet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class BeaverMapsGraph extends Graph<Long, Double> {
    private static JsonParser JSON_PARSER = new JsonParser();

    private IDictionary<Long, Location> ids;
    private ISet<Location> buildings;

    public BeaverMapsGraph() {
        super();
        this.buildings = new ChainingHashSet<>();
        this.ids = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
    }

    /**
     * Reads in buildings, waypoinnts, and roads file into this graph.
     * Populates the ids, buildings, vertices, and edges of the graph
     * @param buildingsFileName the buildings filename
     * @param waypointsFileName the waypoints filename
     * @param roadsFileName the roads filename
     */
    public BeaverMapsGraph(String buildingsFileName, String waypointsFileName, String roadsFileName) {
        this();

        JsonElement bs = fromFile(buildingsFileName);
        for (JsonElement b : bs.getAsJsonArray()) {
            Location loc = new Location(b.getAsJsonObject());
            this.ids.put(loc.id, loc);
            this.buildings.add(loc);
            this.addVertex(loc.id);
        }

        bs = fromFile(waypointsFileName);
        for (JsonElement b : bs.getAsJsonArray()) {
            Location loc = new Location(b.getAsJsonObject());
            this.ids.put(loc.id, loc);
            this.addVertex(loc.id);
        }

        long previous = -1;
        bs = fromFile(roadsFileName);
        Iterator bsIter = bs.getAsJsonArray().iterator();
        while (bsIter.hasNext()) {
            JsonElement b = (JsonElement) bsIter.next();
            Iterator bIter = b.getAsJsonArray().iterator();
            while (bIter.hasNext()) {
                long current = ((JsonElement) bIter.next()).getAsLong();

                if (previous != -1) {
                    Location prev = this.getLocationByID(previous);
                    Location curr = this.getLocationByID(current);

                    Double dist = Location.getDistance(prev.lat, prev.lon, curr.lat, curr.lon);

                    this.addUndirectedEdge(previous, current, dist);
                }
                previous = current;

            }
            previous = -1;
        }

    }

    /**
     * Returns a deque of all the locations with the name locName.
     * @param locName the name of the locations to return
     * @return a deque of all location with the name locName
     */
    public IDeque<Location> getLocationByName(String locName) {
        LinkedDeque<Location> locs = new LinkedDeque<>();

        for (Location loc : buildings) {
            if (loc.name != null && loc.name.equals(locName)) {
                locs.add(loc);
            }
        }

        return locs;
    }

    /**
     * Returns the Location object corresponding to the provided id
     * @param id the id of the object to return
     * @return the location identified by id
     */
    public Location getLocationByID(long id) {
        if (this.ids.get(id) == null) {
            System.out.println();
        }
        return this.ids.get(id);
    }

    /**
     * Adds the provided location to this map.
     * @param n the location to add
     * @return true if n is a new location and false otherwise
     */
    public boolean addVertex(Location n) {
        if (this.vertices().contains(n.id)){
            return false;
        }
        this.ids.put(n.id, n);
        this.addVertex(n.id);
        return true;
    }

    /**
     * Returns the closest building to the location (lat, lon)
     * @param lat the latitude of the location to search near
     * @param lon the longitute of the location to search near
     * @return the building closest to (lat, lon)
     */
    public Location getClosestBuilding(double lat, double lon) {
        MinFourHeap heap = new MinFourHeap();
        for (Location loc : buildings) {
            IPriorityQueue.PQElement temp = new IPriorityQueue.PQElement(loc, loc.getDistance(lat, lon));
            heap.enqueue(temp);
        }

        if (heap.size() == 0) {
            return null;
        }
        return (Location) heap.dequeue().data;
    }

    /**
     * Returns a set of locations which are no more than threshold feet
     * away from start.
     * @param start the location to search around
     * @param threshold the number of feet in the search radius
     * @return
     */
    public ISet<Location> dfs(Location start, double threshold) {
        ISet<Location> locs = new ChainingHashSet<Location>();
        LinkedDeque<Long>  worklist = new LinkedDeque<>();
        ChainingHashDictionary<Long,Boolean> visited = new ChainingHashDictionary<>(MoveToFrontDictionary::new);

        worklist.addFront(start.id);

        while (!worklist.isEmpty()){
            Long curr = worklist.removeFront();
            visited.put(curr, true);
            if (start.getDistance(ids.get(curr)) <= threshold){
                locs.add(ids.get(curr));
                ISet<Long>  neighbors = this.neighbors(curr);
                for (Long l : neighbors){
                    if (!visited.containsKey(l) ){
                        worklist.addFront(l);
                    }
                }
            }

        }
        return locs;
    }

    /**
     * Returns a list of Locations corresponding to
     * buildings in the current map.
     * @return a list of all building locations
     */
    public ISet<Location> getBuildings() {
        return this.buildings;
    }

    /**
     * Returns a shortest path (i.e., a deque of vertices) between the start
     * and target locations (including the start and target locations).
     * @param start the location to start the path from
     * @param target the location to end the path at
     * @return a shortest path between start and target
     */
    public IDeque<Location> dijkstra(Location start, Location target) {
        ChainingHashDictionary<Location, Double> shortestDist = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
        ChainingHashDictionary<Location, Location> previousNode = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
        ChainingHashDictionary<Location, Boolean> visited = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
        MinFourHeap<Long> worklist = new MinFourHeap<>(); //nodes, path length

        //initializing
        for (Long lID : ids) {
            if (!buildings.contains(ids.get(lID)) || (lID.equals(start.id) || lID.equals(target.id))) {
                if (lID.equals(start.id)) {
                    shortestDist.put(ids.get(lID), 0.0);
                } else {
                    shortestDist.put(ids.get(lID), Double.MAX_VALUE);
                }

//                worklist.enqueue(new IPriorityQueue.PQElement<>(lID, shortestDist.get(ids.get(lID))));
            }
        }

        worklist.enqueue(new IPriorityQueue.PQElement<>(start.id, 0));

        //going through
        while (worklist.size() != 0) {
            IPriorityQueue.PQElement<Long> v = worklist.dequeue();
            Location vLoc = ids.get(v.data);

//            if (vLoc.equals(target)) {
//                break;
//            }

            if (!visited.containsKey(vLoc)) {
                for (Long u : this.neighbors(v.data)) {
                    Location uLoc = ids.get(u);
                    if (!buildings.contains(uLoc)) {
                        Double newDistance = shortestDist.get(vLoc) + this.adjacent(v.data, u); //previous node + edge weight

                        if (newDistance < shortestDist.get(uLoc)) {
                            shortestDist.put(uLoc, newDistance);
                            try {
                                worklist.enqueue(new IPriorityQueue.PQElement<>(u, newDistance));
                            } catch (Exception e) {
                                worklist.decreaseKey(new IPriorityQueue.PQElement<>(u, newDistance));
                            }
                            previousNode.put(uLoc, vLoc);
                        }
                    }
                }
            }
            visited.put(vLoc, true);
        }

        Location curr = target;
        LinkedDeque<Location> path = new LinkedDeque<>();
        while (curr != null && !curr.equals(start)) {
            path.addFront(curr);
            curr = previousNode.get(curr);
        }

        if (curr != null && curr.equals(start)) {
            path.addFront(start);
            return path;
        }

        return null;

    }

    /**
     * Returns a JsonElement corresponding to the data in the file
     * with the filename filename
     * @param filename the name of the file to return the data from
     * @return the JSON data from filename
     */
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
}