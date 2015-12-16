package br.edu.utfpr.recominer.util;

import br.edu.utfpr.recominer.model.FilePairAprioriOutput;
import java.util.Comparator;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class OrderFilePairAprioriOutputByFile1Issues
        implements Comparator<FilePairAprioriOutput> {

    @Override
    public int compare(FilePairAprioriOutput o1, FilePairAprioriOutput o2) {
        long issues1 = o1.getFilePairApriori().getFileIssues();
        long issues2 = o2.getFilePairApriori().getFileIssues();
        if (issues1 > issues2) {
            return -1;
        } else if (issues1 < issues2) {
            return 1;
        }
        return 0;
    }
}
