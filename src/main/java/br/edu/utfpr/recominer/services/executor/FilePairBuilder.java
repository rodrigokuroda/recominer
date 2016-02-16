package br.edu.utfpr.recominer.services.executor;

import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FilePairBuilder {

    public static final Set<FilePair> pairFiles(final Set<File> commitedFiles) {
        final int size = commitedFiles.size();
        final File[] files = commitedFiles.toArray(new File[size]);
        final int numberOfFilePair = (size * (size + 1) / 2) - size;
        final Set<FilePair> pairs = new HashSet<>(numberOfFilePair, 1);
        for (int i = 0; i < files.length; i++) {
            for (int j = i + 1; j < files.length; j++) {
                File file1 = files[i];
                File file2 = files[j];
                if (!file1.equals(file2)) {
                    FilePair filePair = new FilePair(file1, file2);
                    pairs.add(filePair);
                }
            }
        }
        return pairs;
    }
}
