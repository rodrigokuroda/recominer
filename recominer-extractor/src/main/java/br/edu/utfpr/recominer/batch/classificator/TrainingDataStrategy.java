package br.edu.utfpr.recominer.batch.classificator;

import br.edu.utfpr.recominer.core.model.Commit;
import java.util.List;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public interface TrainingDataStrategy {
    
    List<Commit> selectCommitsForTraining();
}
