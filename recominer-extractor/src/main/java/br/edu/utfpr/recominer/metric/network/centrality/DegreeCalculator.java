package br.edu.utfpr.recominer.metric.network.centrality;

import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.graph.Graph;
import java.util.HashMap;
import java.util.Map;

/**
 * Calculates the degree centrality measure for a graph <code>G</code>.
 *
 * @see edu.uci.ics.jung.algorithms.scoring.DegreeScorer
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class DegreeCalculator {

    /**
     * Calculates degree centrality measure for each vertex <code>V</code> of a
     * graph <code>G</code>. Returns a denormalized value.
     *
     * @param <V> Vertex of the Graph
     * @param <E> Edge of the Graph
     * @param graph The built Graph with vertices and edges.
     *
     * @return A Map where the the key is the vertex (V) and the score value of
     * degree centrality.
     */
    public static <V, E> Map<V, Integer> calcule(final Graph<V, E> graph) {

        if (graph == null) {
            return new HashMap<>(1);
        }

        DegreeScorer<V> ds = new DegreeScorer<>(graph);

        Map<V, Integer> result = new HashMap<>(graph.getVertexCount());
        for (V v : graph.getVertices()) {
            result.put(v, ds.getVertexScore(v));
        }

        return result;
    }

    /**
     * Calculates degree centrality measure for each vertex <code>V</code> of a
     * graph <code>G</code>. Returns a normalized value (i.e. value divided by
     * N-1 vertices).
     *
     * @param <V> Vertex of the Graph
     * @param <E> Edge of the Graph
     * @param graph The built Graph with vertices and edges.
     *
     * @return A Map where the the key is the vertex (V) and the score value of
     * degree centrality.
     */
    public static <V, E> Map<V, Double> calculeNormalized(final Graph<V, E> graph) {

        if (graph == null) {
            return new HashMap<>(1);
        }

        DegreeScorer<V> ds = new DegreeScorer<>(graph);

        final double maxPossibleNodes = graph.getVertexCount() - 1;

        Map<V, Double> result = new HashMap<>(graph.getVertexCount());
        for (V v : graph.getVertices()) {
            result.put(v, ds.getVertexScore(v) / maxPossibleNodes);
        }

        return result;
    }
}
