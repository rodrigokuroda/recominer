package br.edu.utfpr.recominer.services.executor;

import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FilePairBuilder {
    private final List<File> commitedFiles;

    public FilePairBuilder(List<File> commitedFiles) {
        this.commitedFiles = commitedFiles;
    }

    public final List<FilePair> pairFiles() {
        final int size = commitedFiles.size();
        List<FilePair> pairs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                File file1 = commitedFiles.get(i);
                File file2 = commitedFiles.get(j);
                if (!file1.equals(file2)) {
                    FilePair filePair = new FilePair(file1, file2);
                    pairs.add(filePair);
                }
            }
        }
        return pairs;
    }
}
