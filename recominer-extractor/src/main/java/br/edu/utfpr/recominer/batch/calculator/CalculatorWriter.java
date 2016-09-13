package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.repository.CalculatorLogRepository;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.batch.item.ItemWriter;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class CalculatorWriter implements ItemWriter<CalculatorLog> {

    @Inject
    private CalculatorLogRepository calculatorLogRepository;

    @Override
    public void write(List<? extends CalculatorLog> logs) throws Exception {
        for (CalculatorLog log : logs) {
            calculatorLogRepository.save(log);
        }
    }

}