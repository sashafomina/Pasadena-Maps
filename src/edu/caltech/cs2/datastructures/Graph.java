package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IGraph;
import edu.caltech.cs2.interfaces.ISet;

public class Graph<V, E> extends IGraph<V, E> {

    private ChainingHashDictionary<V, ChainingHashDictionary<V, E>> verts;

    public Graph(){
        this.verts = new ChainingHashDictionary(MoveToFrontDictionary::new);
    }

    @Override
    public boolean addVertex(V vertex) {
        if (verts.containsKey(vertex)) {
            return false;
        }
        this.verts.put(vertex, new ChainingHashDictionary(MoveToFrontDictionary::new));
        return true;
    }

    @Override
    public boolean addEdge(V src, V dest, E e) {
        if (!verts.containsKey(src)) {
            throw new IllegalArgumentException("source does not exist");
        }

        if (!verts.containsKey(dest)) {
            throw new IllegalArgumentException("destination does not exist");
        }

        if (verts.get(src).containsKey(dest)) {
            this.verts.get(src).put(dest, e);
            return false;
        }

        this.verts.get(src).put(dest, e);
        return true;
    }

    @Override
    public boolean addUndirectedEdge(V src, V dest, E e) {
        if (!verts.containsKey(src) && !verts.containsKey(dest)) {
            return false;
        }

        this.verts.get(src).put(dest, e);
        this.verts.get(dest).put(src, e);
        return true;
    }

    @Override
    public boolean removeEdge(V src, V dest) {
        if (!verts.containsKey(src) || !this.verts.get(src).containsKey(dest)) {
            return false;
        }

        ChainingHashDictionary temp = this.verts.get(src);
        temp.remove(dest);

        this.verts.put(src, temp);
        return true;
    }

    @Override
    public ISet<V> vertices() {
        return verts.keySet();
    }

    @Override
    public E adjacent(V i, V j) {
        if (!verts.containsKey(i)) {
            return null;
        }

        return verts.get(i).get(j);
    }

    @Override
    public ISet<V> neighbors(V vertex) {
        return verts.get(vertex).keySet();
    }
}