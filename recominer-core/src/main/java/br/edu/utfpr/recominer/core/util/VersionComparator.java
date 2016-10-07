package br.edu.utfpr.recominer.core.util;

import br.edu.utfpr.recominer.core.model.Version;
import java.util.Comparator;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class VersionComparator implements Comparator<Version> {

    @Override
    public int compare(Version o1, Version o2) {

        String[] version1 = o1.getVersion().split("[.]");
        String[] version2 = o2.getVersion().split("[.]");

        int length = version1.length;
        if (version2.length < version1.length) {
            length = version2.length;
        }

        for (int i = 0; i < length; i++) {
            String s0 = null;
            if (i < version1.length) {
                s0 = version1[i].split("[-]")[0];
            }

            Integer i0;
            try {
                i0 = (s0 == null) ? 0 : Integer.parseInt(s0);
            } catch (java.lang.NumberFormatException ex) {
                i0 = 9999;
            }

            String s1 = null;
            if (i < version2.length) {
                s1 = version2[i].split("[-]")[0];
            }
            Integer i1;
            try {
                i1 = (s1 == null) ? 0 : Integer.parseInt(s1);
            } catch (java.lang.NumberFormatException ex) {
                i1 = 9999;
            }

            if (version1[i].contains("-")
                    && version2[i].contains("-")) {
                if (version1[i].compareTo(version2[i]) < 0) {
                    return -1;
                } else if (version2[i].compareTo(version1[i]) < 0) {
                    return 1;
                }
            } else {
                if (i0.compareTo(i1) < 0) {
                    return -1;
                } else if (i1.compareTo(i0) < 0) {
                    return 1;
                }
            }
        }

        return 0;
    }
}
