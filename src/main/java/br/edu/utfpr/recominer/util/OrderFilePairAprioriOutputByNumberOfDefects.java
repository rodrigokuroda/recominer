package br.edu.utfpr.recominer.util;

import br.edu.utfpr.recominer.model.FilePairAprioriOutput;
import java.util.Comparator;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class OrderFilePairAprioriOutputByNumberOfDefects implements Comparator<FilePairAprioriOutput> {

    @Override
    public int compare(FilePairAprioriOutput o1, FilePairAprioriOutput o2) {
        final int defectIssuesIdWeight1 = o1.getFutureDefectIssuesIdWeight();
        final int defectIssuesIdWeight2 = o2.getFutureDefectIssuesIdWeight();
        if (defectIssuesIdWeight1 > defectIssuesIdWeight2) {
            return -1;
        } else if (defectIssuesIdWeight1 < defectIssuesIdWeight2) {
            return 1;
        }
        return 0;
    }
}
