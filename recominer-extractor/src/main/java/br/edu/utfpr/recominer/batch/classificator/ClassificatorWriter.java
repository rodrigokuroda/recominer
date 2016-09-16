package br.edu.utfpr.recominer.batch.classificator;

import br.edu.utfpr.recominer.repository.ClassificatorLogRepository;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.batch.item.ItemWriter;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
public class ClassificatorWriter implements ItemWriter<ClassificatorLog> {

    @Inject
    private ClassificatorLogRepository classificatorLogRepository;

    @Override
    public void write(List<? extends ClassificatorLog> logs) throws Exception {
        for (ClassificatorLog log : logs) {
            classificatorLogRepository.save(log);
        }
    }

}
