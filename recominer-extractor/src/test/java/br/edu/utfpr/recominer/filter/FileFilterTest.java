package br.edu.utfpr.recominer.filter;

import br.edu.utfpr.recominer.core.model.Cochange;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class FileFilterTest {
    
    @Test
    public void testGetFilterByFilename() {
    }

    @Test
    public void testGetFilterByRegex() {
        List<Cochange> cochanges = new ArrayList<>();
        final Cochange cochange1 = new Cochange(new File(1, "A.txt"), new Commit(1));
        cochanges.add(cochange1);
        final Cochange cochange2 = new Cochange(new File(2, "B.java"), new Commit(2));
        cochanges.add(cochange2);
        final Cochange cochange3 = new Cochange(new File(3, "C.xml"), new Commit(2));
        cochanges.add(cochange3);
        
        Predicate<File> fileFilter = FileFilter.getFilterByRegex("([^\\s]+(\\.(?i)(java|xml))$)");
        
        final List<Cochange> cochangedFilesInCommit = cochanges
                                .stream()
                                .filter(cf -> fileFilter.test(cf.getFile()))
                                .collect(Collectors.toList());
        
        assertEquals(2, cochangedFilesInCommit.size());
        assertFalse(cochangedFilesInCommit.contains(cochange1));
        assertTrue(cochangedFilesInCommit.contains(cochange2));
        assertTrue(cochangedFilesInCommit.contains(cochange3));
    }

    @Test
    public void testGetFiltersFromString() {
    }
    
}
