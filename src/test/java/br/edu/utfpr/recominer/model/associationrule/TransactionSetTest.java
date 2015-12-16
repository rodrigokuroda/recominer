package br.edu.utfpr.recominer.model.associationrule;

import br.edu.utfpr.recominer.model.associationrule.TransactionSet;
import br.edu.utfpr.recominer.model.associationrule.Transaction;
import br.edu.utfpr.recominer.model.associationrule.AssociationRule;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class TransactionSetTest {

    private Set<Transaction<String>> transactions;

    @Before
    public void setUp() {
        final Set<String> t1 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> t2 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> t3 = Stream.of("A", "B").collect(Collectors.toSet());
        final Set<String> t4 = Stream.of("A", "C").collect(Collectors.toSet());
        final Set<String> t5 = Stream.of("A", "C").collect(Collectors.toSet());
        final Set<String> t6 = Stream.of("A", "D").collect(Collectors.toSet());
        final Set<String> t7 = Stream.of("A", "D").collect(Collectors.toSet());
        final Set<String> t8 = Stream.of("A", "F").collect(Collectors.toSet());

        transactions = new LinkedHashSet<>();
        transactions.add(new Transaction<>(1l, t1));
        transactions.add(new Transaction<>(2l, t2));
        transactions.add(new Transaction<>(3l, t3));
        transactions.add(new Transaction<>(4l, t4));
        transactions.add(new Transaction<>(5l, t5));
        transactions.add(new Transaction<>(6l, t6));
        transactions.add(new Transaction<>(7l, t7));
        transactions.add(new Transaction<>(8l, t8));
    }

    @Test
    public void testGetNavigationRules() {
        final TransactionSet<String> transactionSet = new TransactionSet<>(transactions);
        final AssociationRule<String> top1 = new AssociationRule<>("A", Stream.of("B").collect(Collectors.toSet()));
        final AssociationRule<String> top2 = new AssociationRule<>("A", Stream.of("C").collect(Collectors.toSet()));
        final AssociationRule<String> top3 = new AssociationRule<>("A", Stream.of("D").collect(Collectors.toSet()));
        final AssociationRule<String> top4 = new AssociationRule<>("A", Stream.of("F").collect(Collectors.toSet()));
        final List<AssociationRule<String>> expected = Stream.of(top1, top2, top3, top4).collect(Collectors.toList());
        Assert.assertEquals(expected, transactionSet.getNavigationRules("A"));
    }

    @Test
    public void testGetTop3NavigationRules() {
        final TransactionSet<String> transactionSet = new TransactionSet<>(transactions);
        final AssociationRule<String> top1 = new AssociationRule<>("A", Stream.of("B").collect(Collectors.toSet()));
        final AssociationRule<String> top2 = new AssociationRule<>("A", Stream.of("C").collect(Collectors.toSet()));
        final AssociationRule<String> top3 = new AssociationRule<>("A", Stream.of("D").collect(Collectors.toSet()));
        final List<AssociationRule<String>> expected = Stream.of(top1, top2, top3).collect(Collectors.toList());
        Assert.assertEquals(expected, transactionSet.getTopNavigationRules("A", 3));
    }

}
