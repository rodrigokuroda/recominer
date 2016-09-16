package br.edu.utfpr.recominer.batch.dataset;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Project;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
public class FileSystemDatasetOutput implements DatasetOutput {

    private final Logger log = LoggerFactory.getLogger(FileSystemDatasetOutput.class);

    @Override
    public void write(Project project, Commit commit, Dataset dataset, String datasetName) {
        // Structure of folder: PROJECT/COMMIT/FILE/COCHANGE/train.csv
        //                      PROJECT/COMMIT/FILE/test.csv
        // Using toString() method to force error when attribute is null.
        StringBuilder path = new StringBuilder();
        path.append(project.getProjectName().toString())
                .append("/").append(commit.getId().toString())
                .append("/").append(dataset.getFile().getId().toString());

        // Test dataset does not have a "cochange"
        if (dataset.getCochange() != null) {
            path.append("/").append(dataset.getCochange().getId().toString());
        }

        File dir = new File("generated", path.toString());

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File datasetFile = new File(dir, datasetName + ".csv");

        try (FileWriter fw = new FileWriter(datasetFile)) {
            fw.append(dataset.toString());
            fw.flush();
        } catch (IOException ex) {
            log.error("Error to write dataset to file system.", ex);
        }
    }

}
