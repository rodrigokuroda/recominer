package br.edu.utfpr.recominer.metric.network.centrality;

import br.edu.utfpr.recominer.metric.network.centrality.BetweennessCalculator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class BetweennessCalculatorTest {

    public BetweennessCalculatorTest() {
    }

    /**
     * Network: A-B-C-D-E
     */
    @Test
    public void testCalculeGraph() {
        final Map<String, Double> degree = BetweennessCalculator.calcule(
                new NetworkFactory<String, String>()
                .createUndirectedSparseGraph()
                .addEdge("AB", "A", "B")
                .addEdge("BC", "B", "C")
                .addEdge("CD", "C", "D")
                .addEdge("DE", "D", "E")
                .getInstance());

        Assert.assertEquals(0, degree.get("A"), 0.0);
        Assert.assertEquals(3, degree.get("B"), 0.0);
        Assert.assertEquals(4, degree.get("C"), 0.0);
        Assert.assertEquals(3, degree.get("D"), 0.0);
        Assert.assertEquals(0, degree.get("E"), 0.0);
    }

    @Test
    public void testCalculeGraphWithEdgeWeight() {
        // TODO search calculation examples to create test
    }

}
