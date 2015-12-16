package br.edu.utfpr.recominer.util;

import br.edu.utfpr.recominer.model.FilePairApriori;
import br.edu.utfpr.recominer.model.FilePairAprioriOutput;
import java.util.Comparator;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class OrderFilePairAprioriOutputByConfidence
        implements Comparator<FilePairAprioriOutput> {

    @Override
    public int compare(FilePairAprioriOutput o1, FilePairAprioriOutput o2) {
        FilePairApriori apriori1 = o1.getFilePairApriori();
        FilePairApriori apriori2 = o2.getFilePairApriori();
        if (apriori1.getHighestConfidence() > apriori2.getHighestConfidence()) {
            return -1;
        } else if (apriori1.getHighestConfidence() < apriori2.getHighestConfidence()) {
            return 1;
        }
        return 0;
    }
}
