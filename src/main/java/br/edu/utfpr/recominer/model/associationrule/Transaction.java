package br.edu.utfpr.recominer.model.associationrule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents an transaction T that contains a set of items I.
 *
 * @author Rodrigo T. Kuroda
 * @param <I> Type of item that compose the transaction
 */
public class Transaction<I> {

    private final Long id;
    private final Set<I> items;

    public Transaction(final Long id, final Set<I> items) {
        this.id = id;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    /**
     * Returns all items in a unmodifiable set.
     *
     * @return
     */
    public Set<I> getItems() {
        return Collections.unmodifiableSet(items);
    }

    /**
     * Given an item I, if this item contains in this transaction, then return
     * all others items, otherwise returns an empty set.
     *
     * @param antecedentItem
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<I> queryByAntecedent(I antecedentItem) {
        if (items.contains(antecedentItem)) {
            return items.stream().filter(i -> !antecedentItem.equals(i)).collect(Collectors.toSet());
        }
        return Collections.EMPTY_SET;
    }

    /**
     * Given an items I, returns the association rule for it.
     *
     * @param antecedentItem
     * @return
     */
    @SuppressWarnings("unchecked")
    public AssociationRule<I> queryRuleByAntecedent(I antecedentItem) {
        final Set<I> consequentItems = items.stream()
                .filter(item -> !antecedentItem.equals(item))
                .collect(Collectors.toSet());
        return new AssociationRule<>(antecedentItem, consequentItems);
    }

    /**
     * Given a collection of items I, if this item contains in this transaction,
     * then return all others items, otherwise returns an empty set. For
     * example, from set ABCD, if antecedent is ABC then return D. If antecedent
     * is AD, then return BC.
     *
     * @param antecedentItems
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<I> queryByAntecedent(Collection<I> antecedentItems) {
        if (items.containsAll(antecedentItems)) {
            return items.stream().filter(i -> !antecedentItems.contains(i)).collect(Collectors.toSet());
        }
        return Collections.EMPTY_SET;
    }

    /**
     * Given a collection of items I, returns the association rule for it.
     *
     * For example, if query is A in a transaction ABCD, the result is a
     * association rule A -> BCD.
     *
     * @param antecedentItems
     * @return
     */
    public AssociationRule<I> queryRuleByAntecedent(Set<I> antecedentItems) {
        final Set<I> consequentItems;
        if (items.containsAll(antecedentItems)) {
            consequentItems = items.stream()
                    .filter(item -> !antecedentItems.contains(item))
                    .collect(Collectors.toSet());
        } else {
            consequentItems = new HashSet<>();
        }
        return new AssociationRule<>(antecedentItems, consequentItems);
    }

    /**
     * Given many items I, if this item contains in this transaction, then
     * return all others items, otherwise returns an empty set.
     *
     * @param antecedentItems
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<I> queryByAntecedent(I... antecedentItems) {
        final Set<I> antecedentItemsSet = new LinkedHashSet<>(Arrays.asList(antecedentItems));
        if (items.containsAll(antecedentItemsSet)) {
            return items.stream().filter(i -> !antecedentItemsSet.contains(i)).collect(Collectors.toSet());
        }
        return Collections.EMPTY_SET;
    }

    public Set<AssociationRule<I>> extractNavigationAssociationRules() {
        return items.stream()
                .map((item) -> queryRuleByAntecedent(item))
                .collect(Collectors.toSet());
    }

    public Set<AssociationRule<I>> extractPreventionAssociationRules() {
        return items.stream()
                .map((item) -> queryRuleByAntecedent(queryByAntecedent(item)))
                .collect(Collectors.toSet());
    }

    public AssociationRule<I> extractClosureAssociationRules() {
        return new AssociationRule<>(items, new LinkedHashSet<I>(0));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.id);
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
        final Transaction<?> other = (Transaction<?>) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(id).append(";")
                .append(items.stream()
                        .map(i -> i.toString())
                        .collect(Collectors.joining(", ")))
                .toString();
    }

}
