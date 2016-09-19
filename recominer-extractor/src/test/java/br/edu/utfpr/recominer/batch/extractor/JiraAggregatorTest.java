package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.core.model.IssueTracker;
import br.edu.utfpr.recominer.core.model.Project;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class JiraAggregatorTest {

    private JiraAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new JiraAggregator(null, new Project(1, "avro", "avro_test", "", new IssueTracker(), ""));
    }

    @Test
    public void testAggregate() {
    }

    @Test
    public void testReplaceUrlHttps() {
        String replaced = aggregator.replaceUrl("Bootstrap trunk.\n"
                + "\n"
                + "    git-svn-id: https://svn.apache.org/repos/asf/hadoop/avro/trunk@763807 13f79535-47bb-0310-9956-ffa450edef68 ");

        assertEquals("Bootstrap trunk.", replaced);
    }

    @Test
    public void testReplaceUrlHttp() {
        String replaced = aggregator.replaceUrl("Bootstrap trunk.\n"
                + "\n"
                + "    git-svn-id: http://svn.apache.org/repos/asf/hadoop/avro/trunk@763807 13f79535-47bb-0310-9956-ffa450edef68 ");

        assertEquals("Bootstrap trunk.", replaced);
    }

    @Test
    public void testReplaceUrl() {
        String replaced = aggregator.replaceUrl("Bootstrap trunk.\n"
                + "\n"
                + "    git-svn-id: svn://svn.apache.org/repos/asf/hadoop/avro/trunk@763807 13f79535-47bb-0310-9956-ffa450edef68 ");

        assertEquals("Bootstrap trunk.", replaced);
    }

}
