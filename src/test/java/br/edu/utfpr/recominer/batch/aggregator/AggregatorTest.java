package br.edu.utfpr.recominer.batch.aggregator;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The Batch specification allows you to implement process workflow using a Job
 * Specification Language (JSL). In this
 * sample, by using the +step+ element, it's possible to configure a job that
 * runs multiple steps.
 *
 * One Chunk oriented Step and a Batchlet are configured in the file
 * +myJob.xml+. They both execute in order of
 * declaration. First the Chunk oriented Step and finally the Batchlet Step.
 *
 * include::myJob.xml[]
 *
 * @author Rodrigo T. Kuroda
 */
@RunWith(Arquillian.class)
public class AggregatorTest {

    public AggregatorTest() {
    }

    @Test
    public void testOpen() throws Exception {
    }

    @Test
    public void testReadItem() throws Exception {
    }

}
