package br.edu.utfpr.recominer.batch.associationrule;

import br.edu.utfpr.recominer.repository.AssociationRuleLogRepository;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.batch.item.ItemWriter;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class AssociationRuleWriter implements ItemWriter<AssociationRuleLog> {

    @Inject
    private AssociationRuleLogRepository associationRuleLogRepository;

    @Override
    public void write(List<? extends AssociationRuleLog> logs) throws Exception {
        for (AssociationRuleLog log : logs) {
            //associationRuleLogRepository.save(log);
        }
    }

}
