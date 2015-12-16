package br.edu.utfpr.recominer.log;

import br.edu.utfpr.recominer.log.StringBuilderAppender;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class StringBuilderAppenderTest {

    @Test
    public void testAppendLine() {
        StringBuilderAppender sba = new StringBuilderAppender(2);
        sba.appendLine("Line 1");
        sba.appendLine("Line 2");

        assertEquals("Line 1\nLine 2\n", sba.toString());
        assertEquals(2, sba.getLines());

        sba.appendLine("Line 3");

        assertEquals("Line 2\nLine 3\n", sba.toString());
        assertEquals(2, sba.getLines());
    }

}
