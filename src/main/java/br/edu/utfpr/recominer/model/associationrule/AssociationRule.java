package br.edu.utfpr.recominer.model.associationrule;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Rodrigo T. Kuroda
 * @param <I> Type of items
 */
public class AssociationRule<I> {

    private final Set<I> antecedentItems;
    private final Set<I> consequentItems;
    private final Set<Transaction<I>> transactions;

    public AssociationRule(Set<I> antecedentItems, Set<I> consequentItems) {
        this.antecedentItems = antecedentItems;
        this.consequentItems = consequentItems;
        this.transactions = new LinkedHashSet<>();
    }

    public AssociationRule(I antecedentItem, Set<I> consequentItems) {
        this.antecedentItems = new LinkedHashSet<>();
        this.antecedentItems.add(antecedentItem);
        this.consequentItems = consequentItems;
        this.transactions = new LinkedHashSet<>();
    }

    public void addTransaction(final Transaction<I> transaction) {
        transactions.add(transaction);
    }

    public void addTransactions(final Collection<Transaction<I>> transactions) {
        this.transactions.addAll(transactions);
    }

    public Set<I> getAntecedentItem() {
        return Collections.unmodifiableSet(antecedentItems);
    }

    public Set<I> getConsequentItems() {
        return Collections.unmodifiableSet(consequentItems);
    }

    public Set<Transaction<I>> getTransactions() {
        return Collections.unmodifiableSet(transactions);
    }

    public int getSupport() {
        return transactions.size();
    }

    public double getConfidence(final Set<Transaction<I>> allTransactions) {
        return (double) transactions.size() / (double) allTransactions.size();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.antecedentItems);
        hash = 79 * hash + Objects.hashCode(this.consequentItems);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AssociationRule<?> other = (AssociationRule<?>) obj;

        return this.antecedentItems.containsAll(other.antecedentItems)
                && other.antecedentItems.containsAll(this.antecedentItems)
                && this.consequentItems.containsAll(other.consequentItems)
                && other.consequentItems.containsAll(this.consequentItems);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        if (antecedentItems.isEmpty()) {
            sb.append("{}");
        } else {
            sb.append(antecedentItems.stream()
                    .map(i -> i.toString())
                    .collect(Collectors.joining()));
        }

        sb.append(" -> ");

        if (consequentItems.isEmpty()) {
            sb.append("{}");
        } else {
            sb.append(consequentItems.stream()
                    .map(i -> i.toString())
                    .collect(Collectors.joining()));
        }

        return sb.toString();
    }

}
