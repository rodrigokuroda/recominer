package br.edu.utfpr.recominer.model.associationrule;

import br.edu.utfpr.recominer.model.associationrule.Transaction;
import br.edu.utfpr.recominer.model.associationrule.AssociationRule;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class AssociationRuleTest {

    private AssociationRule<String> ar;
    private Set<Transaction<String>> transactions;

    @Before
    public void setUp() {
        final Set<String> t1 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> t2 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> t3 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> t4 = Stream.of("A", "B", "C").collect(Collectors.toSet());

        ar = new AssociationRule<>("A", Stream.of("B").collect(Collectors.toSet()));
        ar.addTransaction(new Transaction<>(1l, t1));
        ar.addTransaction(new Transaction<>(2l, t2));
        ar.addTransaction(new Transaction<>(3l, t3));

        transactions = new LinkedHashSet<>();
        transactions.add(new Transaction<>(1l, t1));
        transactions.add(new Transaction<>(2l, t2));
        transactions.add(new Transaction<>(3l, t3));
        transactions.add(new Transaction<>(4l, t4));
    }

    @Test
    public void testAddTransaction() {
        final Set<String> t1 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> t2 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> t3 = Stream.of("A", "B").collect(Collectors.toSet());

        final Set<Transaction<String>> expectedTx = new LinkedHashSet<>();
        expectedTx.add(new Transaction<>(1l, t1));
        expectedTx.add(new Transaction<>(2l, t2));
        expectedTx.add(new Transaction<>(3l, t3));

        AssociationRule<String> associationRule = new AssociationRule<>("A", Stream.of("B").collect(Collectors.toSet()));
        associationRule.addTransaction(new Transaction<>(1l, t1));
        associationRule.addTransaction(new Transaction<>(2l, t2));
        associationRule.addTransaction(new Transaction<>(3l, t3));

        assertEquals(expectedTx, associationRule.getTransactions());
    }

    @Test
    public void testAddTransactions() {
    }

    @Test
    public void testGetAntecedentItem() {
    }

    @Test
    public void testGetConsequentItems() {
    }

    @Test
    public void testGetTransactions() {
        final Set<String> t1 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> t2 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> t3 = Stream.of("A", "B").collect(Collectors.toSet());

        final Set<Transaction<String>> expectedTx = new LinkedHashSet<>();
        expectedTx.add(new Transaction<>(1l, t1));
        expectedTx.add(new Transaction<>(2l, t2));
        expectedTx.add(new Transaction<>(3l, t3));
        assertEquals(expectedTx, ar.getTransactions());
    }

    @Test
    public void testGetSupport() {
        assertEquals(3, ar.getSupport());
    }

    @Test
    public void testGetConfidence() {
        assertEquals(0.75, ar.getConfidence(transactions), 0.001);
    }

    @Test
    public void testHashCode() {
        AssociationRule<String> associationRule1 = new AssociationRule<>("A", Stream.of("B").collect(Collectors.toSet()));
        AssociationRule<String> associationRule2 = new AssociationRule<>("A", Stream.of("B").collect(Collectors.toSet()));

        assertTrue(associationRule1.hashCode() == associationRule2.hashCode());
    }

    @Test
    public void testEquals() {
        AssociationRule<String> associationRule1 = new AssociationRule<>("A", Stream.of("B").collect(Collectors.toSet()));
        AssociationRule<String> associationRule2 = new AssociationRule<>("A", Stream.of("B").collect(Collectors.toSet()));

        assertTrue(associationRule1.equals(associationRule2));
        assertTrue(associationRule2.equals(associationRule1));
    }

    @Test
    public void testEqualsEmpty() {
        AssociationRule<String> associationRule1 = new AssociationRule<>(new LinkedHashSet<String>(), new LinkedHashSet<String>());
        AssociationRule<String> associationRule2 = new AssociationRule<>(new LinkedHashSet<String>(), new LinkedHashSet<String>());

        assertTrue(associationRule1.equals(associationRule2));
        assertTrue(associationRule2.equals(associationRule1));
    }

    @Test
    public void testEqualsAntecedentEmpty() {
        final Set<String> consequent1 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> consequent2 = Stream.of("A", "B").collect(Collectors.toSet());
        AssociationRule<String> associationRule1 = new AssociationRule<>(new LinkedHashSet<String>(), consequent1);
        AssociationRule<String> associationRule2 = new AssociationRule<>(new LinkedHashSet<String>(), consequent2);

        assertTrue(associationRule1.equals(associationRule2));
        assertTrue(associationRule2.equals(associationRule1));
    }

    @Test
    public void testEqualsMultiple() {
        final Set<String> antecedent1 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> consequent1 = Stream.of("C", "D").collect(Collectors.toSet());
        AssociationRule<String> associationRule1 = new AssociationRule<>(antecedent1, consequent1);

        final Set<String> antecedent2 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> consequent2 = Stream.of("C", "D").collect(Collectors.toSet());
        AssociationRule<String> associationRule2 = new AssociationRule<>(antecedent2, consequent2);

        assertTrue(associationRule1.equals(associationRule2));
        assertTrue(associationRule2.equals(associationRule1));
    }

    @Test
    public void testToString() {
        assertEquals("A -> B", ar.toString());
    }

    @Test
    public void testToStringAntecedentEmpty() {
        final Set<String> consequent = Stream.of("A", "B").collect(Collectors.toSet());

        AssociationRule<String> associationRule1 = new AssociationRule<>(new LinkedHashSet<String>(), consequent);
        assertEquals("{} -> AB", associationRule1.toString());
    }

    @Test
    public void testToStringConsequentEmpty() {
        final Set<String> consequent = Stream.of("A", "B").collect(Collectors.toSet());

        AssociationRule<String> associationRule2 = new AssociationRule<>(consequent, new LinkedHashSet<String>());
        assertEquals("AB -> {}", associationRule2.toString());
    }

    @Test
    public void testToStringMultiple() {
        final Set<String> antecedent = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> consequent = Stream.of("C", "D").collect(Collectors.toSet());
        AssociationRule<String> associationRule = new AssociationRule<>(antecedent, consequent);
        assertEquals("AB -> CD", associationRule.toString());
    }

}
