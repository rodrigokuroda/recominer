package br.edu.utfpr.recominer.metric.associationrule;

import br.edu.utfpr.recominer.core.model.AssociationRule;
import br.edu.utfpr.recominer.core.model.Transaction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class AssociationRuleExtractorTest {
    
    private Set<Transaction<String>> transactions;
    private AssociationRuleExtractor<String> extractor;
    
    @Before
    public void setup() {
        transactions = new HashSet<>();
        // A = 1, B = 1, C = 1
        transactions.add(new Transaction<>(1l, asSet("A", "B", "C")));
        
        // A = 2, B = 2, C = 1
        transactions.add(new Transaction<>(2l, asSet("A", "B")));
        
        // A = 3, B = 2, C = 1, D = 1
        transactions.add(new Transaction<>(3l, asSet("A", "D")));
        
        // A = 4, B = 3, C = 2, D = 1
        transactions.add(new Transaction<>(4l, asSet("A", "B", "C")));
        
        // A = 4, B = 3, C = 2, D = 1, X = 1, Y = 1, Z = 1
        transactions.add(new Transaction<>(5l, asSet("X", "Y", "Z")));
        
        // A = 5, B = 3, C = 2, D = 1, X = 1, Y = 2, Z = 2
        transactions.add(new Transaction<>(6l, asSet("A", "Y", "Z")));
        
        extractor = new AssociationRuleExtractor<>(transactions);
    }

    @Test
    public void testExtractNavigationRules() {
    }

    @Test
    public void testExtractPreventionRules() {
    }

    @Test
    public void testQueryPreventionRules() {
    }

    @Test
    public void testExtractClosureRules() {
    }

    @Test
    public void testQueryAssociationRules_Set() {
    }

    @Test
    public void testQueryAssociationRules_GenericType() {
    }

    @Test
    public void testQueryAssociationRulesSingleConsequent() {
        Set<AssociationRule<String>> dQuery = extractor.queryAssociationRulesSingleConsequent("D");
        assertEquals(asSet(new AssociationRule<>("D", "A")), dQuery);
        
        AssociationRule<String> adResult = dQuery.iterator().next();
        assertEquals("Support", 1, adResult.getSupport());
        assertEquals("Tx", 1, adResult.getTransactions().size());
        assertEquals("Total Tx",1, adResult.getAntecedentTransactions());
        assertEquals("Confidence", 1.0, adResult.getConfidence(), 0.01);
        
        Set<AssociationRule<String>> xQuery = extractor.queryAssociationRulesSingleConsequent("Y");
        final AssociationRule<String> yxAssociationRule = new AssociationRule<>("Y", "X");
        final AssociationRule<String> yzAssociationRule = new AssociationRule<>("Y", "Z");
        final AssociationRule<String> yaAssociationRule = new AssociationRule<>("Y", "A");
        assertEquals(asSet(yxAssociationRule, yzAssociationRule, yaAssociationRule), xQuery);
        
        Map<AssociationRule<String>, AssociationRule<String>> map = new HashMap<>();
        for (AssociationRule<String> associationRule : xQuery) {
            map.put(associationRule, associationRule);
        }
        
        AssociationRule<String> yzResult = map.get(yzAssociationRule);
        
        assertEquals("Support", 2, yzResult.getSupport());
        assertEquals("Antecedent Tx", 2, yzResult.getAntecedentTransactions());
        assertEquals("Confidence", 1.0, yzResult.getConfidence(), 0.01);
        
        AssociationRule<String> yxResult = map.get(yxAssociationRule);
        
        assertEquals("Support", 1, yxResult.getSupport());
        assertEquals("Antecedent Tx", 2, yxResult.getAntecedentTransactions());
        assertEquals("Confidence", 0.5, yxResult.getConfidence(), 0.01);
        
        AssociationRule<String> yaResult = map.get(yaAssociationRule);
        
        assertEquals("Support", 1, yaResult.getSupport());
        assertEquals("Antecedent Tx", 2, yaResult.getAntecedentTransactions());
        assertEquals("Confidence", 0.5, yaResult.getConfidence(), 0.01);
    }

    @Test
    public void testCountTransactionsOf_GenericType() {
        assertEquals(5, extractor.countTransactionsOf("A"));
    }

    @Test
    public void testCountTransactionsOf_Set() {
        assertEquals(2, extractor.countTransactionsOf(asSet("A", "B", "C")));
    }

    private <I> Set<I> asSet(I... i) {
        return Stream.of(i).collect(Collectors.toSet());
    }
}
