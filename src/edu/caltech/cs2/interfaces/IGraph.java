package edu.caltech.cs2.interfaces;

public abstract class IGraph<V, E> {
    /**
     * Add a vertex to the graph.
     * @param vertex The vertex to add
     * @return true if vertex was not present already.
     */
    public abstract boolean addVertex(V vertex);

    /**
     * Adds edge e to the graph.
     *
     * @param e The edge to add.
     * @throws IllegalArgumentException
     *             If e is not a valid edge (eg. refers to vertices not in the graph).
     * @return true If e was not already present; false if it was (in which case the graph is still updated).
     */
    public abstract boolean addEdge(V src, V dest, E e);

    /**
     * Adds edge e to the graph in both directionns.
     *
     * @param e The edge to add.
     * @throws IllegalArgumentException
     *    If e is not a valid edge (eg. refers to vertices not in the graph).
     * @return true If e was not already present in either direction; false if it was (in which case the graph is still updated).
     */
    public abstract boolean addUndirectedEdge(V src, V dest, E e);

    /**
     * Remove an edge from src to dest from the graph.
     *
     * @throws IllegalArgumentException if src or dest is not in the graph.
     * @return true if an edge from src to dest edge was present.
     */
    public abstract boolean removeEdge(V src, V dest);

    /**
     * Returns the set of vertices in the graph.
     * @return The set of all vertices in the graph.
     */
    public abstract ISet<V> vertices();

    /**
     * Tests if vertices i and j are adjacent, returning the edge between
     * them if so.
     *
     * @throws IllegalArgumentException if i or j are not vertices in the graph.
     * @return The edge from i to j if it exists in the graph;
     * 		   null otherwise.
     */
    public abstract E adjacent(V i, V j);

    /**
     * Return the neighbours of a given vertex when this graph is treated as
     * DIRECTED; that is, vertices to which vertex has an outgoing edge.
     *
     * @param vertex The vertex the neighbours of which to return.
     * @throws IllegalArgumentException if vertex is not in the graph.
     * @return The set of neighbors of vertex.
     */
    public abstract ISet<V> neighbors(V vertex);
}