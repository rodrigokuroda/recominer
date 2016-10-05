package br.edu.utfpr.recominer.metric.associationrule;

import br.edu.utfpr.recominer.core.model.AssociationRule;
import br.edu.utfpr.recominer.core.model.Transaction;
import br.edu.utfpr.recominer.model.associationrule.AssociationRulePerformanceMeasure;
import java.util.LinkedHashSet;
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
public class AssociationRulePerformanceCalculatorTest {

    private Set<Transaction<String>> transactions;
    private AssociationRule<String> navigationRuleA;
    private AssociationRule<String> navigationRuleB;
    private AssociationRule<String> preventionRuleA;
    private AssociationRule<String> closureRuleA;
    private AssociationRule<String> closureRuleB;

    @Before
    public void setUp() {
        final Set<String> t1 = asSet("A", "X", "Y", "Z", "W", "H");
        final Set<String> t2 = asSet("A", "C", "F", "D", "W");
        final Set<String> t3 = asSet("A", "W", "D", "F");
        final Set<String> t4 = asSet("A", "B", "C", "D");
        final Set<String> t5 = asSet("B", "C", "D", "W");
        final Set<String> t6 = asSet("B", "X", "Y", "W", "C");
        final Set<String> t7 = asSet("B", "D");
        final Set<String> t8 = asSet("B", "X");
        final Set<String> t9 = asSet("B", "X", "W");

        long id = 1;
        transactions = new LinkedHashSet<>();
        transactions.add(new Transaction<>(id++, t1));
        transactions.add(new Transaction<>(id++, t1));
        transactions.add(new Transaction<>(id++, t1));
        transactions.add(new Transaction<>(id++, t1));
        transactions.add(new Transaction<>(id++, t1));

        transactions.add(new Transaction<>(id++, t2));
        transactions.add(new Transaction<>(id++, t2));
        transactions.add(new Transaction<>(id++, t2));
        transactions.add(new Transaction<>(id++, t2));

        transactions.add(new Transaction<>(id++, t3));
        transactions.add(new Transaction<>(id++, t3));
        transactions.add(new Transaction<>(id++, t3));

        transactions.add(new Transaction<>(id++, t4));

        transactions.add(new Transaction<>(id++, t5));
        transactions.add(new Transaction<>(id++, t5));
        transactions.add(new Transaction<>(id++, t5));
        transactions.add(new Transaction<>(id++, t5));

        transactions.add(new Transaction<>(id++, t6));
        transactions.add(new Transaction<>(id++, t6));
        transactions.add(new Transaction<>(id++, t6));
        transactions.add(new Transaction<>(id++, t6));

        transactions.add(new Transaction<>(id++, t7));
        transactions.add(new Transaction<>(id++, t7));
        transactions.add(new Transaction<>(id++, t7));
        transactions.add(new Transaction<>(id++, t7));

        transactions.add(new Transaction<>(id++, t8));
        transactions.add(new Transaction<>(id++, t8));

        transactions.add(new Transaction<>(id++, t9));

        navigationRuleA = new AssociationRule<>("A", asSet("B", "C", "D"));
        navigationRuleB = new AssociationRule<>("B", asSet("A", "C", "D"));

        preventionRuleA = new AssociationRule<>(asSet("A", "B", "C"), asSet("D"));

        closureRuleA = new AssociationRule<>(asSet("A", "B", "C", "D"), asSet());
        closureRuleB = new AssociationRule<>(asSet("B", "X", "W"), asSet());
    }

    @Test
    public void testCalculeRecallPrecisionForNavigationRules() {
        final AssociationRuleExtractor<String> extractor = new AssociationRuleExtractor<>(transactions);
        final Set<AssociationRule<String>> extractedNavigationRules = extractor.extractNavigationRules();
        final AssociationRulePerformanceCalculator<String> calculator = new AssociationRulePerformanceCalculator<>(extractedNavigationRules);

        final Map<AssociationRule<String>, Double> associationRulesRecall = calculator.calculeRecall(3);
        final Map<AssociationRule<String>, Double> associationRulesPrecision = calculator.calculePrecision(3);

        assertEquals(0.3333, associationRulesRecall.get(navigationRuleA), 0.0001);
        assertEquals(0.2777, associationRulesPrecision.get(navigationRuleA), 0.0001);

        assertEquals(0.4444, associationRulesRecall.get(navigationRuleB), 0.0001);
        assertEquals(0.6388, associationRulesPrecision.get(navigationRuleB), 0.0001);
    }

    @Test
    public void testCalculeRecallPrecisionForNavigationRulesInBean() {
        final AssociationRuleExtractor<String> extractor = new AssociationRuleExtractor<>(transactions);
        final Set<AssociationRule<String>> extractedNavigationRulesA = extractor.queryAssociationRules(navigationRuleA.getAntecedentItem());
        final AssociationRulePerformanceCalculator<String> calculatorA = new AssociationRulePerformanceCalculator<>(extractedNavigationRulesA);

        final Map<AssociationRule<String>, AssociationRulePerformanceMeasure> associationRulesRecallA = calculatorA.calculePerformance(3);

        final AssociationRulePerformanceMeasure associationRulePerformanceA = associationRulesRecallA.get(navigationRuleA);

        assertEquals(0.3333, associationRulePerformanceA.getRecall(), 0.0001);
        assertEquals(0.2777, associationRulePerformanceA.getPrecision(), 0.0001);

        final Set<AssociationRule<String>> extractedNavigationRulesB = extractor.queryAssociationRules(navigationRuleB.getAntecedentItem());
        final AssociationRulePerformanceCalculator<String> calculatorB = new AssociationRulePerformanceCalculator<>(extractedNavigationRulesB);

        final Map<AssociationRule<String>, AssociationRulePerformanceMeasure> associationRulesRecallB = calculatorB.calculePerformance(3);

        final AssociationRulePerformanceMeasure associationRulePerformanceB = associationRulesRecallB.get(navigationRuleB);
        assertEquals(0.4444, associationRulePerformanceB.getRecall(), 0.0001);
        assertEquals(0.6388, associationRulePerformanceB.getPrecision(), 0.0001);
    }

    @Test
    public void testCalculeRecallPrecisionForPreventionRules() {
        Set<Transaction<String>> transactionsPrevention = createTransactionsForPreventionRulesTest();

        final AssociationRuleExtractor<String> extractor = new AssociationRuleExtractor<>(transactionsPrevention);
        final Set<AssociationRule<String>> extractedPreventionRules = extractor.queryAssociationRules(preventionRuleA.getAntecedentItem());
        final AssociationRulePerformanceCalculator<String> calculator = new AssociationRulePerformanceCalculator<>(extractedPreventionRules);

        final Map<AssociationRule<String>, Double> associationRulesRecall = calculator.calculeRecall(3);
        final Map<AssociationRule<String>, Double> associationRulesPrecision = calculator.calculePrecision(3);

        assertEquals(0.3333, associationRulesRecall.get(preventionRuleA), 0.0001);
        assertEquals(0.0833, associationRulesPrecision.get(preventionRuleA), 0.0001);
    }

    @Test
    public void testCalculeRecallPrecisionForPreventionRulesInBean() {
        Set<Transaction<String>> transactionsPrevention = createTransactionsForPreventionRulesTest();
        final AssociationRuleExtractor<String> extractor = new AssociationRuleExtractor<>(transactionsPrevention);
        final Set<AssociationRule<String>> extractedPreventionRules = extractor.queryAssociationRules(preventionRuleA.getAntecedentItem());
        final AssociationRulePerformanceCalculator<String> calculator = new AssociationRulePerformanceCalculator<>(extractedPreventionRules);

        final Map<AssociationRule<String>, AssociationRulePerformanceMeasure> associationRulesRecall = calculator.calculePerformance(3);

        final AssociationRulePerformanceMeasure associationRulePerformanceA = associationRulesRecall.get(preventionRuleA);

        assertEquals(0.3333, associationRulePerformanceA.getRecall(), 0.0001);
        assertEquals(0.0833, associationRulePerformanceA.getPrecision(), 0.0001);
    }

    @Test
    public void testCalculeRecallPrecisionForClosureRules() {
        final AssociationRuleExtractor<String> extractor = new AssociationRuleExtractor<>(transactions);
        final Set<AssociationRule<String>> extractedClosureRulesA = extractor.queryAssociationRules(closureRuleA.getAntecedentItem());
        final AssociationRulePerformanceCalculator<String> calculatorA = new AssociationRulePerformanceCalculator<>(extractedClosureRulesA);

        final Map<AssociationRule<String>, Double> associationRulesRecallA = calculatorA.calculeRecall(3);
        final Map<AssociationRule<String>, Double> associationRulesPrecisionA = calculatorA.calculePrecision(3);

        assertEquals(0.0000, associationRulesRecallA.get(closureRuleA), 0.0001);
        assertEquals(1.0000, associationRulesPrecisionA.get(closureRuleA), 0.0001);

        final Set<AssociationRule<String>> extractedClosureRulesB = extractor.queryAssociationRules(closureRuleB.getAntecedentItem());
        final AssociationRulePerformanceCalculator<String> calculatorB = new AssociationRulePerformanceCalculator<>(extractedClosureRulesB);

        final Map<AssociationRule<String>, Double> associationRulesRecallB = calculatorB.calculeRecall(3);
        final Map<AssociationRule<String>, Double> associationRulesPrecisionB = calculatorB.calculePrecision(3);

        assertEquals(0.0000, associationRulesRecallB.get(closureRuleB), 0.0001);
        assertEquals(0.5000, associationRulesPrecisionB.get(closureRuleB), 0.0001);
    }

    private Set<String> asSet(String... i) {
        return Stream.of(i).collect(Collectors.toSet());
    }

    private Set<Transaction<String>> createTransactionsForPreventionRulesTest() {
        final Set<String> t1 = asSet("A", "B", "C", "X", "Y", "Z", "D");
        final Set<String> t2 = asSet("A", "B", "C", "W", "E", "R", "G", "F", "S");
        final Set<String> t3 = asSet("A", "B", "C", "S", "E");
        final Set<String> t4 = asSet("A", "B", "C", "D");
        final Set<Transaction<String>> transactionsPrevention = new LinkedHashSet<>();
        long id = 1;
        transactionsPrevention.add(new Transaction<>(id++, t1));
        transactionsPrevention.add(new Transaction<>(id++, t1));
        transactionsPrevention.add(new Transaction<>(id++, t1));
        transactionsPrevention.add(new Transaction<>(id++, t1));
        transactionsPrevention.add(new Transaction<>(id++, t1));
        transactionsPrevention.add(new Transaction<>(id++, t2));
        transactionsPrevention.add(new Transaction<>(id++, t2));
        transactionsPrevention.add(new Transaction<>(id++, t2));
        transactionsPrevention.add(new Transaction<>(id++, t2));
        transactionsPrevention.add(new Transaction<>(id++, t3));
        transactionsPrevention.add(new Transaction<>(id++, t3));
        transactionsPrevention.add(new Transaction<>(id++, t3));
        transactionsPrevention.add(new Transaction<>(id++, t4));
        transactionsPrevention.add(new Transaction<>(id++, t4));
        return transactionsPrevention;
    }
}
