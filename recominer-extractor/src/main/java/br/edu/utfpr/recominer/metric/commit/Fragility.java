package br.edu.utfpr.recominer.metric.commit;

import br.edu.utfpr.recominer.model.FilePair;
import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class Fragility {

    private final FilePair filePair;
    private final double fragility;

    public Fragility(FilePair fileFile, double fragility) {
        this.filePair = fileFile;
        this.fragility = fragility;
    }

    public FilePair getFileFile() {
        return filePair;
    }

    public double getFragility() {
        return fragility;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.filePair);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Fragility other = (Fragility) obj;
        if (!Objects.equals(this.filePair, other.filePair)) {
            return false;
        }
        return true;
    }

}
