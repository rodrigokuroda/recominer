package br.edu.utfpr.recominer.batch.dataset;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Project;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public interface DatasetOutput {
    
    void write(Project project, Commit commit, Dataset dataset, String datasetName);
    
}
