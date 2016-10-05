package br.edu.utfpr.recominer.model.associationrule;

import br.edu.utfpr.recominer.core.model.AssociationRule;
import br.edu.utfpr.recominer.core.model.Transaction;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 * @param <I> Type of item in transaction
 */
public class TransactionSet<I> {

    private final Set<Transaction<I>> transactions;

    public TransactionSet() {
        this.transactions = new LinkedHashSet<>();
    }

    public TransactionSet(final Set<Transaction<I>> transactions) {
        this.transactions = transactions;
    }

    public List<AssociationRule<I>> getNavigationRules(final I antecedent) {
        final Map<AssociationRule<I>, AssociationRule<I>> rules = new LinkedHashMap<>();

        transactions.stream()
                .forEach(transaction -> {
                    transaction.getItems().stream()
                    .filter(item -> item.equals(antecedent))
                    .map(item -> new AssociationRule<>(item, transaction.queryByAntecedent(item), transactions.size()))
                            .forEach(rule -> {
                                if (rules.containsKey(rule)) {
                                    rules.get(rule).addTransaction(transaction);
                                } else {
                                    rules.put(rule, rule);
                                }
                    });
                });

        return rules.keySet().stream()
                .sorted((r1, r2) -> Double.compare(r1.getConfidence(), r2.getConfidence()) * -1)
                .sorted((r1, r2) -> Integer.compare(r1.getSupport(), r2.getSupport()) * -1)
                .collect(Collectors.toList());
    }

    public List<AssociationRule<I>> getTopNavigationRules(final I antecedent, final int k) {
        final List<AssociationRule<I>> navigationRules = getNavigationRules(antecedent);
        return navigationRules.subList(0, k);
    }
}
