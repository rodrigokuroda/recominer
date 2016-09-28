package br.edu.utfpr.recominer.core.model;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class CochangeAprioriTest {

    private CochangeApriori instance;

    @Before
    public void setUp() {
        instance = new CochangeApriori(new File(1, "A"), new File(2, "B"), 2, 4, 2, 4);
    }

    @Test
    public void testGetIssues() {
    }

    @Test
    public void testGetAllIssues() {
    }

    @Test
    public void testGetSupportFile() {
        assertEquals(0.5, instance.getSupportFile(), 0.001);
    }

    @Test
    public void testGetSupportFile2() {
        assertEquals(1, instance.getSupportFile2(), 0.001);
    }

    @Test
    public void testGetSupportFilePair() {
        assertEquals(0.5, instance.getSupportFilePair(), 0.001);
    }

    @Test
    public void testConfidenceFile1() {
        assertEquals(1, instance.getConfidence(), 0.001);
    }

    @Test
    public void testConfidenceFile2() {
        assertEquals(0.5, instance.getConfidence2(), 0.001);
    }

    @Test
    public void testLift() {
        assertEquals(1, instance.getLift(), 0.001);
    }

    @Test
    public void testConviction() {
        assertEquals(0, instance.getConviction(), 0.001);
    }

    @Test
    public void testConviction2() {
        assertEquals(0, instance.getConviction2(), 0.0001);
    }

    @Test
    public void testToString() {
        assertEquals("A;B;2;4;2;4;0.5;1.0;0.5;1.0;0.5;1.0;0.0;0.0;", instance.toString());
    }

    /**
     * File metrics with high confidence should be printed first (left column).
     */
    @Test
    public void testToStringInverted() {
        instance = new CochangeApriori(new File(1, "A"), new File(2, "B"), 4, 2, 2, 4);
        assertEquals("B;A;2;4;2;4;0.5;1.0;0.5;1.0;0.5;1.0;0.0;0.0;", instance.toStringHighConfidenceOnLeft());
    }

    @Test
    public void testFitsFilter() {
        CochangeApriori apriori1 = new CochangeApriori(new File(3, "C"), new File(4, "D"), 2, 4, 2, 4);
        Assert.assertTrue(apriori1.fits(new FilterByApriori(null, null, null, null, 2, 2)));
        Assert.assertTrue(apriori1.fits(new FilterByApriori(null, null, null, null, 2, 3)));
        Assert.assertFalse(apriori1.fits(new FilterByApriori(null, null, null, null, 1, 1)));
        Assert.assertFalse(apriori1.fits(new FilterByApriori(null, null, null, null, 3, 4)));

        Assert.assertTrue(apriori1.fits(new FilterByApriori(null, null, 0.5, null, 2, 2)));
        Assert.assertTrue(apriori1.fits(new FilterByApriori(null, null, 0.9, null, 2, 3)));
        Assert.assertTrue(apriori1.fits(new FilterByApriori(null, null, 1.0, null, 2, 3)));
        Assert.assertFalse(apriori1.fits(new FilterByApriori(null, null, 0.5, 0.7, 1, 1)));
        Assert.assertFalse(apriori1.fits(new FilterByApriori(null, null, 0.7, 0.9, 3, 4)));
    }

    @Test
    public void testGetFileWithHighestConfidence() {
        assertEquals(new File(1, "A"), instance.getFile());
    }

    @Test
    public void testGetHighestConfidence() {
        assertEquals(1.0, instance.getHighestConfidence(), 0.0001);
    }

    @Test
    public void testHasMinIssues() {
        assertEquals(1.0, instance.getHighestConfidence(), 0.0001);
    }

    @Test
    public void testHasIssues() {
    }

    @Test
    public void testHasMaxIssues() {
    }

    @Test
    public void testHasMinSupport() {
    }

    @Test
    public void testHasMaxSupport() {
    }

    @Test
    public void testHasMinConfidence() {
    }

    @Test
    public void testHasMaxConfidence() {
    }

    @Test
    public void testHasMinMaxConfidence() {
    }

    @Test
    public void testHasMinMaxSupport() {
    }

    @Test
    public void testHasMinMaxIssues() {
    }
}
