package br.edu.utfpr.recominer.model.associationrule;

import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class AssociationRulePerformance<I> {

    private final AssociationRule<I> associationRuleOccurred;
    private final AssociationRule<I> associationRulePredicted;
    private final int occurred;
    private final int predicted;
    private final long predictedOccurred;

    public AssociationRulePerformance(AssociationRule<I> associationRuleOccurred, AssociationRule<I> associationRulePredicted) {
        this.associationRuleOccurred = associationRuleOccurred;
        this.associationRulePredicted = associationRulePredicted;

        this.occurred = associationRuleOccurred.getConsequentItems().size();
        this.predicted = associationRulePredicted.getConsequentItems().size();
        // count how many items in predicted association rules matches items in association rules occurred
        this.predictedOccurred = associationRulePredicted.getConsequentItems().stream()
                .filter(item -> associationRuleOccurred.getConsequentItems().contains(item))
                .count();
    }

    public AssociationRule<I> getAssociationRuleOccurred() {
        return associationRuleOccurred;
    }

    public AssociationRule<I> getAssociationRulePredicted() {
        return associationRulePredicted;
    }

    /**
     * Recall is computed by following formula: Prediction
     * associationRuleOccurred (PO) / associationRuleOccurred (O). If (O) = 0,
     * then results 0.
     *
     * Example for association rule B -> ACD: Prediction = B -> CDE, Prediction
     * Occurred (PO) = CD (2), Occurred (O) = ACD (3)
     *
     * PO/O = 2/3 =~ 0,67
     *
     * @return recall PO/O
     */
    public double calculeRecall() {
        return occurred == 0 ? .0d : (double) predictedOccurred / (double) occurred;
    }

    /**
     * Precision is computed by following formula: Prediction
     * associationRuleOccurred (PO) / Predicted (P). If (P) = 0, then results 0.
     *
     * Example for association rule B -> ACD: Prediction = B -> CDEF, Prediction
     * Occurred (PO) = CD (2), Predicted (P) = CDEF (4)
     *
     * PO/P = 2/4 = 0,5
     *
     * @return recall PO/P
     */
    public double calculePrecision() {
        if (predicted == 0 && predictedOccurred == 0) {
            return 1.0d;
        }
        return predicted == 0 ? .0d : (double) predictedOccurred / (double) predicted;
    }

    /**
     * Compute performance metrics (recall and precision) for this rule.
     *
     * @see calculePrecision() and calculeRecall() methods.
     * @return AssociationRulePerformanceMeasure
     */
    public AssociationRulePerformanceMeasure calculePerformance() {
        return new AssociationRulePerformanceMeasure(calculeRecall(), calculePrecision());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.associationRuleOccurred);
        hash = 67 * hash + Objects.hashCode(this.associationRulePredicted);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AssociationRulePerformance<?> other = (AssociationRulePerformance<?>) obj;
        if (!Objects.equals(this.associationRuleOccurred, other.associationRuleOccurred)) {
            return false;
        }
        if (!Objects.equals(this.associationRulePredicted, other.associationRulePredicted)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Occurred = " + associationRuleOccurred + ", Predicted = " + associationRulePredicted;
    }

}
