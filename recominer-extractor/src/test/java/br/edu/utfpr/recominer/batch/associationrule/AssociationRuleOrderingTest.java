package br.edu.utfpr.recominer.batch.associationrule;

import br.edu.utfpr.recominer.core.model.AssociationRule;
import br.edu.utfpr.recominer.core.model.Transaction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class AssociationRuleOrderingTest {

    @Test
    public void testSortBySupportAndConfidence() {
        Set<AssociationRule<String>> associationRules = new HashSet<>();
        
        final AssociationRule<String> ab = new AssociationRule<>("A", "B", 10);
        ab.addTransaction(new Transaction<>(1l, new HashSet<>(Arrays.asList("A", "B", "1", "2"))));
        ab.addTransaction(new Transaction<>(2l, new HashSet<>(Arrays.asList("A", "B", "1", "2"))));
        ab.addTransaction(new Transaction<>(3l, new HashSet<>(Arrays.asList("A", "B", "1", "2"))));
        ab.addTransaction(new Transaction<>(4l, new HashSet<>(Arrays.asList("A", "B", "1", "2"))));
        associationRules.add(ab);
        
        final AssociationRule<String> cd = new AssociationRule<>("C", "D", 10);
        cd.addTransaction(new Transaction<>(1l, new HashSet<>(Arrays.asList("C", "D", "1"))));
        cd.addTransaction(new Transaction<>(2l, new HashSet<>(Arrays.asList("C", "D", "1"))));
        cd.addTransaction(new Transaction<>(3l, new HashSet<>(Arrays.asList("C", "D", "1"))));
        associationRules.add(cd);
        
        final AssociationRule<String> ef = new AssociationRule<>("E", "F", 10);
        ef.addTransaction(new Transaction<>(1l, new HashSet<>(Arrays.asList("E", "F"))));
        ef.addTransaction(new Transaction<>(2l, new HashSet<>(Arrays.asList("E", "F"))));
        associationRules.add(ef);
        
        final AssociationRule<String> gh = new AssociationRule<>("G", "H", 10);
        ef.addTransaction(new Transaction<>(1l, new HashSet<>(Arrays.asList("G", "H"))));
        associationRules.add(gh);
        
        final AssociationRule<String> ij = new AssociationRule<>("I", "J", 10);
        ij.addTransaction(new Transaction<>(1l, new HashSet<>(Arrays.asList("I", "J", "3", "4", "5"))));
        ij.addTransaction(new Transaction<>(2l, new HashSet<>(Arrays.asList("I", "J", "3", "4", "5"))));
        ij.addTransaction(new Transaction<>(3l, new HashSet<>(Arrays.asList("I", "J", "3", "4", "5"))));
        ij.addTransaction(new Transaction<>(4l, new HashSet<>(Arrays.asList("I", "J", "3", "4", "5"))));
        ij.addTransaction(new Transaction<>(5l, new HashSet<>(Arrays.asList("I", "J", "3", "4", "5"))));
        associationRules.add(ij);
        
        List<AssociationRule> expected = new ArrayList<>();
        expected.add(ij);
        expected.add(ab);
        expected.add(cd);
        expected.add(ef);
        expected.add(gh);
        assertEquals(expected, AssociationRuleOrdering.sortBySupportAndConfidence(associationRules));
    }
    
}
