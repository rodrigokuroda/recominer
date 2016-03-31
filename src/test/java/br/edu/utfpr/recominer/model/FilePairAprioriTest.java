package br.edu.utfpr.recominer.model;

import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FilePairAprioriTest {

    private FilePairApriori instance;
    private final FilePair filePair = new FilePair(1, new File(1, "A.java"), new File(2, "B.java"));

    @Before
    public void setUp() {
        instance = new FilePairApriori(filePair, 2, 4, 2, 4);
    }


    @After
    public void tearDown() {
        instance = null;
    }

    @Test
    public void testSupportFile1() {
        assertEquals(0.5, instance.getSupportFile(), 0.001);
    }

    @Test
    public void testSupportFile2() {
        assertEquals(1, instance.getSupportFile2(), 0.001);
    }

    @Test
    public void testSupportFilePair() {
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
    public void testFileHasGreaterConfidence() {
        assertEquals("A.java", instance.getFileWithHighestConfidence().getFileName());
    }

    @Test
    public void testToString() {
        assertEquals("A.java;B.java;2;4;2;4;0.5;1.0;0.5;1.0;0.5;1.0;0.0;0.0;", instance.toString());
    }

    @Test
    public void testFitsFilter() {
        FilePairApriori apriori1 = new FilePairApriori(filePair, 2, 4, 2, 4);
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
}
