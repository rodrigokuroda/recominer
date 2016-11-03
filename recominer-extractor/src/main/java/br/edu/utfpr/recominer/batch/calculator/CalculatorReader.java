package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.batch.ProjectItemReader;
import javax.inject.Named;
import org.springframework.batch.core.configuration.annotation.StepScope;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
@StepScope
public class CalculatorReader extends ProjectItemReader {

}
