
package br.edu.utfpr.recominer.metric.network;

import br.edu.utfpr.recominer.metric.network.LocalMeasure;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for LocalMeasure class.
 * 
 * @author Rodrigo T. Kuroda
 */
public class LocalMeasureTest {
    
    private LocalMeasure<String> instance;
    
    @Before
    public void setup() {
        instance = new LocalMeasure<>("V", 0, 1, 2);
    }
    
    @After
    public void tearDown() {
        instance = null;
    }
    
    /**
     * Test of toString method
     */
    @Test
    public void testToString() {
        String expResult = "V, in degree: 0, out degree: 1, in and out degree: 1, clusteringCoefficient: 2.0";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of equals method. It is equals if the vertex of two compared 
     * LocalMeasure are equal.
     */
    @Test
    public void testEqualsTrue() {
        LocalMeasure<String> equal = new LocalMeasure<>("V", 2, 1, 0);
        
        assertTrue(instance.hashCode() == equal.hashCode());
        assertTrue(instance.equals(equal));
    }
    
    /**
     * Test of equals method. It is not equals if the vertex of two compared 
     * LocalMeasure are not equal.
     */
    @Test
    public void testEqualsFalse() {
        LocalMeasure<String> notEqual = new LocalMeasure<>("V1", 0, 1, 2);
        
        assertFalse(instance.hashCode() == notEqual.hashCode());
        assertFalse(instance.equals(notEqual));
    }
    
}
