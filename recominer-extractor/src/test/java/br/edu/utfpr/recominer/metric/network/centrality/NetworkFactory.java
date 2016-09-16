package br.edu.utfpr.recominer.metric.network.centrality;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Useful helper to create graph (a.k.a. network) easily, using fluent methods.
 *
 * @see edu.uci.ics.jung.graph.Graph
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 *
 * @param <V> The type of graph's vertices
 * @param <E> The type of graph's edges
 */
public class NetworkFactory<V, E> {

    private Graph<V, E> graph;
    private EdgeType edgeType;

    /**
     * Create a graph that doesn't supports directed edge.
     *
     * @return this
     */
    public NetworkFactory<V, E> createUndirectedSparseGraph() {
        graph = new UndirectedSparseGraph<>();
        edgeType = EdgeType.UNDIRECTED;
        return this;
    }

    /**
     * Create a graph that supports directed edge.
     *
     * @return this
     */
    public NetworkFactory<V, E> createDirectedSparseGraph() {
        graph = new DirectedSparseGraph<>();
        edgeType = EdgeType.DIRECTED;
        return this;
    }

    public NetworkFactory<V, E> addVertex(V vertex) {
        graph.addVertex(vertex);
        return this;
    }

    /**
     * Adds and edge between two vertex.
     *
     * @param edge A unique name for new edge
     * @param vertex1 The name of 1st vertex. If it doesn't exist, it is
     * created.
     * @param vertex2 The name of 2nd vertex. If it doesn't exist, it is
     * created.
     * @return this
     */
    public NetworkFactory<V, E> addEdge(E edge, V vertex1, V vertex2) {
        graph.addEdge(edge, vertex1, vertex2, edgeType);
        return this;
    }

    /**
     * Get instance of created graph.
     *
     * @return The built graph
     */
    public Graph<V, E> getInstance() {
        return graph;
    }
}
