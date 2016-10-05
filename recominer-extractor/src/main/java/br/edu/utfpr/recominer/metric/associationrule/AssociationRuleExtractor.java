package br.edu.utfpr.recominer.metric.associationrule;

import br.edu.utfpr.recominer.core.model.AssociationRule;
import br.edu.utfpr.recominer.core.model.Transaction;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Extracts all association rules from Transactions. It is possible to extract
 * navigation rules, prevention rules and closure rules (Zimmermmann et al.,
 * 2005).
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 * @param <I> Type of items in transaction
 */
public class AssociationRuleExtractor<I> {

    private final Set<Transaction<I>> transactions;

    public AssociationRuleExtractor(Set<Transaction<I>> transactions) {
        this.transactions = transactions;
    }

    /**
     * For a transaction with items ABCD, extract the rules A -> BCD, B -> ACD,
     * C -> ABD and D -> ABC.
     *
     * @return
     */
    public Set<AssociationRule<I>> extractNavigationRules() {
        final Map<AssociationRule<I>, AssociationRule<I>> associationRules = new LinkedHashMap<>();
        transactions.stream()
                .forEach(t -> {
                    t.extractNavigationAssociationRules().stream().forEach(ar -> {
                        ar.setTotalTransactions(transactions.size());
                        if (associationRules.containsKey(ar)) {
                            associationRules.get(ar).addTransaction(t);
                        } else {
                            ar.addTransaction(t);
                            associationRules.put(ar, ar);
                        }
                    });
                });

        return associationRules.keySet();
    }

    /**
     * For a transaction with items ABCD, extract the rules ABC -> D, ABD -> C,
     * ACD -> B and BCD -> A.
     *
     * @return
     */
    public Set<AssociationRule<I>> extractPreventionRules() {
        final Map<AssociationRule<I>, AssociationRule<I>> associationRules = new LinkedHashMap<>();
        transactions.stream()
                .forEach(t -> {
                    t.extractPreventionAssociationRules().stream().forEach(ar -> {
                        ar.setTotalTransactions(transactions.size());
                        if (associationRules.containsKey(ar)) {
                            associationRules.get(ar).addTransaction(t);
                        } else {
                            ar.addTransaction(t);
                            associationRules.put(ar, ar);
                        }
                    });
                });

        return associationRules.keySet();
    }

    /**
     * For a transaction with items ABCD, extract the rule ABCD -> {}.
     *
     * @return
     */
    public Set<AssociationRule<I>> extractClosureRules() {

        final Map<AssociationRule<I>, AssociationRule<I>> associationRules = new LinkedHashMap<>();
        transactions.stream()
                .forEach(t -> {
                    final AssociationRule<I> ar = t.extractClosureAssociationRules();
                    ar.setTotalTransactions(transactions.size());
                    if (associationRules.containsKey(ar)) {
                        associationRules.get(ar).addTransaction(t);
                    } else {
                        ar.addTransaction(t);
                        associationRules.put(ar, ar);
                    }
                });

        return associationRules.keySet();
    }

    /**
     * Given a query ABC, extract all rules for ABC (ABC -> ?).
     *
     * @param query
     *
     * @return
     */
    public Set<AssociationRule<I>> queryAssociationRules(Set<I> query) {
        final Map<AssociationRule<I>, AssociationRule<I>> associationRules = new LinkedHashMap<>();
        transactions.stream()
                .filter(t -> t.getItems().containsAll(query))
                .forEach(t -> {
                    AssociationRule<I> ar = t.queryRuleByAntecedent(query);
                    ar.setTotalTransactions(transactions.size());
                    if (associationRules.containsKey(ar)) {
                        associationRules.get(ar).addTransaction(t);
                    } else {
                        ar.addTransaction(t);
                        associationRules.put(ar, ar);
                    }
                });

        return associationRules.keySet();
    }

    /**
     * Given a query A, extract all rules A -> ?, where ? can be a set of items,
     * i.e., a single item or multiple items.
     *
     * @param query
     *
     * @return
     */
    public Set<AssociationRule<I>> queryAssociationRules(I query) {
        final Map<AssociationRule<I>, AssociationRule<I>> associationRules = new LinkedHashMap<>();
        transactions.stream()
                .forEach(t -> {
                    AssociationRule<I> ar = t.queryRuleByAntecedent(query);
                    ar.setTotalTransactions(transactions.size());
                    if (associationRules.containsKey(ar)) {
                        associationRules.get(ar).addTransaction(t);
                    } else {
                        ar.addTransaction(t);
                        associationRules.put(ar, ar);
                    }
                });

        return associationRules.keySet();
    }

    /**
     * Given a query A, extract all rules A -> ?, where ? is a single item.
     *
     * @param query
     *
     * @return
     */
    public Set<AssociationRule<I>> queryAssociationRulesSingleConsequent(I query) {
        final Map<AssociationRule<I>, AssociationRule<I>> associationRules = new LinkedHashMap<>();
        transactions.stream()
                .forEach(t -> {
                    Set<AssociationRule<I>> arSet = t.queryRuleByAntecedentWithSingleConsequent(query);
                    for (AssociationRule<I> ar : arSet) {
                        ar.setTotalTransactions(transactions.size());
                        if (associationRules.containsKey(ar)) {
                            associationRules.get(ar).addTransaction(t);
                        } else {
                            ar.addTransaction(t);
                            associationRules.put(ar, ar);
                        }
                    }
                });

        return associationRules.keySet();
    }
}
