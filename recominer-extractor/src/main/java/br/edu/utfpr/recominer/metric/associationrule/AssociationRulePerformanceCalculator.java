package br.edu.utfpr.recominer.metric.associationrule;

import br.edu.utfpr.recominer.model.associationrule.AssociationRule;
import br.edu.utfpr.recominer.model.associationrule.AssociationRulePerformance;
import br.edu.utfpr.recominer.model.associationrule.AssociationRulePerformanceMeasure;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class AssociationRulePerformanceCalculator<I> {

    private final Set<AssociationRule<I>> associationRules;

    public AssociationRulePerformanceCalculator(final long transactions, final Set<AssociationRule<I>> associationRules) {
        // sorted by support (high priority) and confidence (low priority)
        this.associationRules = associationRules.stream()
                .sorted((ar1, ar2) -> Double.compare(ar1.getConfidence(transactions), ar2.getConfidence(transactions)) * -1)
                .sorted((ar1, ar2) -> Integer.compare(ar1.getSupport(), ar2.getSupport()) * -1)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Map<AssociationRule<I>, Double> calculePrecision(final int topK) {
        Map<AssociationRulePerformance<I>, AssociationRulePerformance<I>> associationRulesPerformance = extractTopAssociationRules(topK);

        final Map<AssociationRule<I>, Double> precisionAverage = associationRulesPerformance.keySet().stream()
                .collect(Collectors.groupingBy(ar -> ar.getAssociationRuleOccurred(), Collectors.averagingDouble(AssociationRulePerformance::calculePrecision)));

        return precisionAverage;
    }

    public Map<AssociationRule<I>, Double> calculeRecall(final int topK) {
        Map<AssociationRulePerformance<I>, AssociationRulePerformance<I>> associationRulesPerformance = extractTopAssociationRules(topK);

        final Map<AssociationRule<I>, Double> recallAverage = associationRulesPerformance.keySet().stream()
                .collect(Collectors.groupingBy(ar -> ar.getAssociationRuleOccurred(), Collectors.averagingDouble(AssociationRulePerformance::calculeRecall)));

        return recallAverage;
    }

    public Map<AssociationRule<I>, AssociationRulePerformanceMeasure> calculePerformance(final int topK) {
        final Map<AssociationRule<I>, Double> associationRulesRecall = calculeRecall(topK);
        final Map<AssociationRule<I>, Double> associationRulesPrecision = calculePrecision(topK);

        final Map<AssociationRule<I>, AssociationRulePerformanceMeasure> associationRulePerformance = new LinkedHashMap<>();
        associationRulesRecall.entrySet().stream().forEach((entry) -> {
            final AssociationRule<I> associationRule = entry.getKey();
            final Double recall = entry.getValue();
            final Double precision = associationRulesPrecision.get(associationRule);

            AssociationRulePerformanceMeasure measure = new AssociationRulePerformanceMeasure(recall, precision);
            associationRulePerformance.put(associationRule, measure);
        });

        return associationRulePerformance;
    }

    public Set<AssociationRule<I>> getTopAssociationRules(final int topK) {
        return associationRules.stream().limit(topK).collect(Collectors.toSet());
    }

    /**
     * For each association rule, calcule the coverage for the three top
     * association rules previously ordered.
     *
     * @param topK Top quantity of rules to compute
     *
     * @return
     */
    private Map<AssociationRulePerformance<I>, AssociationRulePerformance<I>> extractTopAssociationRules(final int topK) {
        final Map<AssociationRulePerformance<I>, AssociationRulePerformance<I>> associationRulesPerformance = new LinkedHashMap<>();
        associationRules.stream().forEach((associationRule) -> {
            associationRules.stream()
                    .filter(ar -> ar.getAntecedentItem().equals(associationRule.getAntecedentItem())) // filtering by same query
                    .limit(topK)
                    .forEach(topAssociationRulePredicted -> {
                final AssociationRulePerformance<I> coverage = new AssociationRulePerformance<>(associationRule, topAssociationRulePredicted);
                associationRulesPerformance.put(coverage, coverage);
                    });
        });
        return associationRulesPerformance;
    }

    private Map<AssociationRulePerformance<I>, AssociationRulePerformance<I>> extractAllAssociationRules(final int topK) {
        final Map<AssociationRulePerformance<I>, AssociationRulePerformance<I>> associationRulesPerformance = new LinkedHashMap<>();
        associationRules.stream().forEach((associationRule) -> {
            associationRules.stream()
                    .filter(ar -> ar.getAntecedentItem().equals(associationRule.getAntecedentItem())) // filtering by same query
                    .forEach(topAssociationRulePredicted -> {
                final AssociationRulePerformance<I> coverage = new AssociationRulePerformance<>(associationRule, topAssociationRulePredicted);
                associationRulesPerformance.put(coverage, coverage);
                    });
        });
        return associationRulesPerformance;
    }
}
