package br.edu.utfpr.recominer.metric.network;

import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;

/**
 * Measures some properties of the graph.
 * 
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class GlobalMeasureCalculator {

    /**
     * Calculates the size, ties, and diameter of the graph.
     * 
     * @param <V> Class of vertex
     * @param <E> Class of edge
     * @param graph Graph to measure
     * @return GlobalMeasure class, contains the result of each metric.
     */
    public static <V, E> GlobalMeasure calcule(
            final Graph<V, E> graph) {
        int size = graph == null ? 0 : graph.getVertexCount();
        int ties = graph == null ? 0 : graph.getEdgeCount();
        // the true parameter specifies to use the max value, even some distance is null (see javadoc)
        double diameter = graph == null ? 0 : DistanceStatistics.diameter(graph, new UnweightedShortestPath<>(graph), true);
        return new GlobalMeasure(size, ties, diameter);
    }
}
