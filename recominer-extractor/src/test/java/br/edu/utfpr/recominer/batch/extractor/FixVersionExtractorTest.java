package br.edu.utfpr.recominer.batch.extractor;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class FixVersionExtractorTest {

    @Test
    public void testExtractMajorVersion() {
        FixVersionExtractor extractor = new FixVersionExtractor();
        
        assertEquals("0", extractor.getMajorVersion("0"));
        assertEquals("1", extractor.getMajorVersion("1.0"));
        assertEquals("2", extractor.getMajorVersion("2.1.0"));
    }

    @Test
    public void testExtractMinorVersion() {
        FixVersionExtractor extractor = new FixVersionExtractor();
        
        assertEquals("0", extractor.getMinorVersion("0"));
        assertEquals("1.0", extractor.getMinorVersion("1.0"));
        assertEquals("2.1", extractor.getMinorVersion("2.1.0"));
    }
    
}
