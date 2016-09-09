package br.edu.utfpr.recominer.metric.associationrule;

import br.edu.utfpr.recominer.model.associationrule.AssociationRule;
import br.edu.utfpr.recominer.model.associationrule.Transaction;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class AssociationRulePredictorTest {

    private Set<Transaction<String>> transactions;

    @Before
    public void setUp() {
        final Set<String> t1 = asSet("A", "B");
        final Set<String> t2 = asSet("A", "B");
        final Set<String> t3 = asSet("A", "B");
        final Set<String> t4 = asSet("A", "B", "C");

        transactions = new LinkedHashSet<>();
        transactions.add(new Transaction<>(1l, t1));
        transactions.add(new Transaction<>(2l, t2));
        transactions.add(new Transaction<>(3l, t3));
        transactions.add(new Transaction<>(4l, t4));
    }

    @Test
    public void testPredictNavigationRules() {
        final AssociationRuleExtractor<String> extractor = new AssociationRuleExtractor<>(transactions);
        final Set<AssociationRule<String>> navigationRules = new LinkedHashSet<>(extractor.extractNavigationRules());

        Set<AssociationRule<String>> expectedNavigationRules = new LinkedHashSet<>();
        expectedNavigationRules.add(new AssociationRule<>("A", asSet("B")));
        expectedNavigationRules.add(new AssociationRule<>("A", asSet("B", "C")));
        expectedNavigationRules.add(new AssociationRule<>("B", asSet("A")));
        expectedNavigationRules.add(new AssociationRule<>("B", asSet("A", "C")));
        expectedNavigationRules.add(new AssociationRule<>("C", asSet("A", "B")));

        assertEquals(expectedNavigationRules, navigationRules);
    }

    @Test
    public void testPredictPreventionRules() {
        final AssociationRuleExtractor<String> extractor = new AssociationRuleExtractor<>(transactions);
        final Set<AssociationRule<String>> preventionRules = new LinkedHashSet<>(extractor.extractPreventionRules());

        Set<AssociationRule<String>> expectedPreventionRules = new LinkedHashSet<>();
        expectedPreventionRules.add(new AssociationRule<>("B", asSet("A")));
        expectedPreventionRules.add(new AssociationRule<>(asSet("B", "C"), asSet("A")));
        expectedPreventionRules.add(new AssociationRule<>("A", asSet("B")));
        expectedPreventionRules.add(new AssociationRule<>(asSet("A", "C"), asSet("B")));
        expectedPreventionRules.add(new AssociationRule<>(asSet("A", "B"), asSet("C")));

        assertEquals(expectedPreventionRules, preventionRules);
    }

    @Test
    public void testPredictClosureRules() {
        final AssociationRuleExtractor<String> extractor = new AssociationRuleExtractor<>(transactions);
        final Set<AssociationRule<String>> closureRules = new LinkedHashSet<>(extractor.extractClosureRules());

        Set<AssociationRule<String>> expectedPreventionRules = new LinkedHashSet<>();
        expectedPreventionRules.add(new AssociationRule<>(asSet("A", "B"), new LinkedHashSet<String>()));
        expectedPreventionRules.add(new AssociationRule<>(asSet("A", "B", "C"), new LinkedHashSet<String>()));

        assertEquals(expectedPreventionRules, closureRules);
    }

    private Set<String> asSet(String... i) {
        return Stream.of(i).collect(Collectors.toSet());
    }
}
