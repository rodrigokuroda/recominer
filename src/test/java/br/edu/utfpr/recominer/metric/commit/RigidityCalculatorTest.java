package br.edu.utfpr.recominer.metric.commit;

import br.edu.utfpr.recominer.metric.commit.RigidityCalculator;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class RigidityCalculatorTest {

    @Test
    public void testCalculeOneFile() {
        RigidityCalculator calc = new RigidityCalculator(100);
        Assert.assertEquals(0.16, calc.calcule(16), 0.001);
    }

    @Test
    public void testCalculePairFile() {
        RigidityCalculator calc = new RigidityCalculator(100);
        Assert.assertEquals(0.33, calc.calcule(16, 17), 0.001);
    }

}
