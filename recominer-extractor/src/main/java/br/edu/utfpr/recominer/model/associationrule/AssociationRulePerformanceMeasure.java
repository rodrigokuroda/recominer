package br.edu.utfpr.recominer.model.associationrule;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class AssociationRulePerformanceMeasure {

    private final double recall;
    private final double precision;

    public AssociationRulePerformanceMeasure(double recall, double precision) {
        this.recall = recall;
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getPrecision() {
        return precision;
    }

}
