package br.edu.utfpr.recominer.batch.dataset;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Project;
import java.io.File;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public interface DatasetOutput {
    
    void write(File workingDir, Project project, Commit commit, Dataset dataset, String datasetName);
    
}
