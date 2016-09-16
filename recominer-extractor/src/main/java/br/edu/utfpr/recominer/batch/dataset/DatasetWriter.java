package br.edu.utfpr.recominer.batch.dataset;

import br.edu.utfpr.recominer.repository.DatasetLogRepository;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.batch.item.ItemWriter;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
public class DatasetWriter implements ItemWriter<DatasetLog> {

    @Inject
    private DatasetLogRepository datasetLogRepository;

    @Override
    public void write(List<? extends DatasetLog> logs) throws Exception {
        for (DatasetLog log : logs) {
            datasetLogRepository.save(log);
        }
    }

}
