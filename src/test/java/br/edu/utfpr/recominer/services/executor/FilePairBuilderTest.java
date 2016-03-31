package br.edu.utfpr.recominer.services.executor;

import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kuroda
 */
public class FilePairBuilderTest {
    
    private Set<File> files;
    
    @Before
    public void setUp() {
        files = new HashSet<>();
        files.add(new File(1, "A.java"));
        files.add(new File(2, "B.java"));
        files.add(new File(3, "C.java"));
    }

    @Test
    public void testPairFiles() {
        final Set<FilePair> expected = new HashSet<>();
        final FilePair ab = new FilePair(new File(1, "A.java"), new File(2, "B.java"));
        final FilePair bc = new FilePair(new File(2, "B.java"), new File(3, "C.java"));
        final FilePair ca = new FilePair(new File(3, "C.java"), new File(1, "A.java"));
        expected.add(ab);
        expected.add(bc);
        expected.add(ca);
        
        final Set<FilePair> result = FilePairBuilder.pairFiles(files);
        Assert.assertEquals(expected, result);
    }
    
}
