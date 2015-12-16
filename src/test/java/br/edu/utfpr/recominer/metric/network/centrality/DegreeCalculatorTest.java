package br.edu.utfpr.recominer.metric.network.centrality;

import br.edu.utfpr.recominer.metric.network.centrality.DegreeCalculator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for Degree Centrality calculator.
 *
 * @author Rodrigo T. Kuroda
 */
public class DegreeCalculatorTest {

    /**
     * Network: A-B-C-D-E
     */
    @Test
    public void testCalculeDenormalized() {
        final Map<String, Integer> degree = DegreeCalculator.calcule(
                new NetworkFactory<String, String>()
                .createUndirectedSparseGraph()
                .addEdge("AB", "A", "B")
                .addEdge("BC", "B", "C")
                .addEdge("CD", "C", "D")
                .addEdge("DE", "D", "E")
                .getInstance());

        Assert.assertEquals(1, degree.get("A"), 0.0);
        Assert.assertEquals(2, degree.get("B"), 0.0);
        Assert.assertEquals(2, degree.get("C"), 0.0);
        Assert.assertEquals(2, degree.get("D"), 0.0);
        Assert.assertEquals(1, degree.get("E"), 0.0);

    }

    /**
     * Network: A-B-C-D-E
     */
    @Test
    public void testCalculeNormalized() {
        final Map<String, Double> degree = DegreeCalculator.calculeNormalized(
                new NetworkFactory<String, String>()
                .createUndirectedSparseGraph()
                .addEdge("AB", "A", "B")
                .addEdge("BC", "B", "C")
                .addEdge("CD", "C", "D")
                .addEdge("DE", "D", "E")
                .getInstance());

        Assert.assertEquals(0.25, degree.get("A"), 0.001);
        Assert.assertEquals(0.5, degree.get("B"), 0.001);
        Assert.assertEquals(0.5, degree.get("C"), 0.001);
        Assert.assertEquals(0.5, degree.get("D"), 0.001);
        Assert.assertEquals(0.25, degree.get("E"), 0.001);
    }

}
