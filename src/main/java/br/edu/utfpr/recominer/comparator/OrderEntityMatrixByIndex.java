package br.edu.utfpr.recominer.comparator;

import br.edu.utfpr.recominer.model.matrix.EntityMatrix;
import java.util.Comparator;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class OrderEntityMatrixByIndex implements Comparator<EntityMatrix> {

    @Override
    public int compare(EntityMatrix o1, EntityMatrix o2) {
        Integer index1 = Integer.valueOf(o1.getParams().get("index").toString());
        Integer index2 = Integer.valueOf(o2.getParams().get("index").toString());

        return index1.compareTo(index2);
    }

}
