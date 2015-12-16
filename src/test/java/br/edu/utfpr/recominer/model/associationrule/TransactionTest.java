package br.edu.utfpr.recominer.model.associationrule;

import br.edu.utfpr.recominer.model.associationrule.Transaction;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class TransactionTest {

    private Transaction<String> t;

    @Before
    public void setUp() {
        Set<String> items = new LinkedHashSet<>();
        items.add("A");
        items.add("B");
        items.add("C");
        items.add("D");

        t = new Transaction<>(1l, items);
    }

    @Test
    public void testQueryByAntecedentItem() {
        Set<String> expectedConsequentBCD = new LinkedHashSet<>();
        expectedConsequentBCD.add("B");
        expectedConsequentBCD.add("C");
        expectedConsequentBCD.add("D");
        assertEquals(expectedConsequentBCD, t.queryByAntecedent("A"));

        Set<String> expectedConsequentACD = new LinkedHashSet<>();
        expectedConsequentACD.add("A");
        expectedConsequentACD.add("C");
        expectedConsequentACD.add("D");
        assertEquals(expectedConsequentACD, t.queryByAntecedent("B"));

        Set<String> expectedConsequentABD = new LinkedHashSet<>();
        expectedConsequentABD.add("A");
        expectedConsequentABD.add("B");
        expectedConsequentABD.add("D");
        assertEquals(expectedConsequentABD, t.queryByAntecedent("C"));

        Set<String> expectedConsequentABC = new LinkedHashSet<>();
        expectedConsequentABC.add("A");
        expectedConsequentABC.add("B");
        expectedConsequentABC.add("C");
        assertEquals(expectedConsequentABC, t.queryByAntecedent("D"));
    }

    @Test
    public void testQueryByAntecedentItems() {
        Set<String> expectedConsequentA = new LinkedHashSet<>();
        expectedConsequentA.add("A");
        assertEquals(expectedConsequentA, t.queryByAntecedent("B", "C", "D"));

        Set<String> expectedConsequentB = new LinkedHashSet<>();
        expectedConsequentB.add("B");
        assertEquals(expectedConsequentB, t.queryByAntecedent("A", "C", "D"));

        Set<String> expectedConsequentC = new LinkedHashSet<>();
        expectedConsequentC.add("C");
        assertEquals(expectedConsequentC, t.queryByAntecedent("A", "B", "D"));

        Set<String> expectedConsequentD = new LinkedHashSet<>();
        expectedConsequentD.add("D");
        assertEquals(expectedConsequentD, t.queryByAntecedent("A", "B", "C"));

    }

    @Test
    public void testQueryByAntecedentItemsExpectingEmptySet() {
        @SuppressWarnings("unchecked")
        Set<String> expectedEmptyConsequent = Collections.EMPTY_SET;
        assertEquals(expectedEmptyConsequent, t.queryByAntecedent("E"));
        assertEquals(expectedEmptyConsequent, t.queryByAntecedent("A", "B", "C", "D"));
    }

    @Test
    public void testEquals() {
        @SuppressWarnings("unchecked")
        Transaction<String> anotherTransactionWithSameId = new Transaction<>(1l, Collections.EMPTY_SET);
        assertTrue(t.equals(anotherTransactionWithSameId));
        assertTrue(anotherTransactionWithSameId.equals(t));

        assertTrue(t.hashCode() == anotherTransactionWithSameId.hashCode());
    }

    @Test
    public void testNotEquals() {
        @SuppressWarnings("unchecked")
        Transaction<String> anotherTransactionWithDiferentId = new Transaction<>(1l, Collections.EMPTY_SET);
        assertTrue(t.equals(anotherTransactionWithDiferentId));
        assertTrue(anotherTransactionWithDiferentId.equals(t));

        assertTrue(t.hashCode() == anotherTransactionWithDiferentId.hashCode());
    }

}
