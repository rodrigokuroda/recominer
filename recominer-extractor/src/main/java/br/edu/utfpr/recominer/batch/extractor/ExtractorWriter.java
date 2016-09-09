package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.model.ExtractorLog;
import br.edu.utfpr.recominer.repository.ExtractorLogRepository;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.batch.item.ItemWriter;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class ExtractorWriter implements ItemWriter<ExtractorLog> {

    @Inject
    private ExtractorLogRepository extractorLogRepository;

    @Override
    public void write(List<? extends ExtractorLog> logs) throws Exception {
        for (ExtractorLog log : logs) {
            extractorLogRepository.save(log);
        }
    }

}
