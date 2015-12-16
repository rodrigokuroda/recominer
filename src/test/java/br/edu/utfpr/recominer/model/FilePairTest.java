package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.model.FilePairApriori;
import br.edu.utfpr.recominer.model.FilePair;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FilePairTest {

    private FilePair instance;

    @Before
    public void setUp() {
        instance = new FilePair("A", "B");
    }

    @After
    public void tearDown() {
        instance = null;
    }

    @Test
    public void testEquals() {
        FilePair ab = new FilePair("A", "B");
        FilePair ba = new FilePair("B", "A");
        assertTrue(instance.equals(ab));
        assertTrue(instance.equals(ba));

        assertEquals(instance.hashCode(), ab.hashCode());
        assertEquals(instance.hashCode(), ba.hashCode());
    }

    @Test
    public void testNotEquals() {
        FilePair ac = new FilePair("A", "C");
        FilePair bc = new FilePair("B", "C");
        assertFalse(instance.equals(ac));
        assertFalse(instance.equals(bc));

        assertNotEquals(instance.hashCode(), ac.hashCode());
        assertNotEquals(instance.hashCode(), bc.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals("A;B;", instance.toString());
    }

    @Test
    public void testToStringAprioriBasedBA() {
        FilePairApriori filePairApriori = new FilePairApriori(instance, 20, 10, 10, 30);
        assertTrue(filePairApriori.getFile2().equals(filePairApriori.getFileWithHighestConfidence()));
        assertEquals("B", filePairApriori.getFileWithHighestConfidence().getFileName());
    }

    @Test
    public void testToStringAprioriBasedAB() {
        FilePairApriori filePairApriori = new FilePairApriori(instance, 10, 20, 10, 30);
        assertTrue(filePairApriori.getFile().equals(filePairApriori.getFileWithHighestConfidence()));
        assertEquals("A", filePairApriori.getFileWithHighestConfidence().getFileName());
    }
}
