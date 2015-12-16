package br.edu.utfpr.recominer.util;

import br.edu.utfpr.recominer.model.FilePairApriori;
import br.edu.utfpr.recominer.model.FilePairAprioriOutput;
import java.util.Comparator;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class OrderFilePairAprioriOutputBySupport
        implements Comparator<FilePairAprioriOutput> {

    @Override
    public int compare(FilePairAprioriOutput o1, FilePairAprioriOutput o2) {
        FilePairApriori apriori1 = o1.getFilePairApriori();
        FilePairApriori apriori2 = o2.getFilePairApriori();
        if (apriori1.getSupportFilePair() > apriori2.getSupportFilePair()) {
            return -1;
        } else if (apriori1.getSupportFilePair() < apriori2.getSupportFilePair()) {
            return 1;
        }
        return 0;
    }
}
