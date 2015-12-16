package br.edu.utfpr.recominer.metric.network.centrality;

import br.edu.utfpr.recominer.metric.network.centrality.ClosenessCalculator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class ClosenessCalculatorTest {

    public ClosenessCalculatorTest() {
    }

    /**
     * Network: A-B-C-D-E
     */
    @Test
    public void testCalculeGraph() {
        final Map<String, Double> degree = ClosenessCalculator.calcule(
                new NetworkFactory<String, String>()
                .createUndirectedSparseGraph()
                .addEdge("AB", "A", "B")
                .addEdge("BC", "B", "C")
                .addEdge("CD", "C", "D")
                .addEdge("DE", "D", "E")
                .getInstance());

        Assert.assertEquals(0.4, degree.get("A"), 0.001);
        Assert.assertEquals(0.571, degree.get("B"), 0.001);
        Assert.assertEquals(0.666, degree.get("C"), 0.001);
        Assert.assertEquals(0.571, degree.get("D"), 0.001);
        Assert.assertEquals(0.4, degree.get("E"), 0.001);
    }

    @Test
    public void testCalculeGraphWithEdgeWeight() {
        // TODO search calculation examples to create test
    }

}
