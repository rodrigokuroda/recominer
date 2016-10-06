package br.edu.utfpr.recominer.batch.associationrule;

import br.edu.utfpr.recominer.core.model.AssociationRule;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class AssociationRuleOrdering {
    
    public static <I> List<AssociationRule<I>> sortBySupportAndConfidence(final Collection<AssociationRule<I>> associationRules) {
        return associationRules.stream()
                .sorted((ar1, ar2) -> Double.compare(ar1.getConfidence(), ar2.getConfidence()) * -1)
                .sorted((ar1, ar2) -> Integer.compare(ar1.getSupport(), ar2.getSupport()) * -1)
                .collect(Collectors.toList());
    }
}
