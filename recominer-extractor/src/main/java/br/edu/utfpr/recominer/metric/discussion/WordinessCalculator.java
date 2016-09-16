
package br.edu.utfpr.recominer.metric.discussion;

import com.google.common.base.Strings;
import java.util.List;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class WordinessCalculator {

    public static long calcule(String body, List<String> comments) {
        long wordiness = LuceneUtil.tokenizeString(Strings.nullToEmpty(body)).size();

        for (final String comment : comments) {
            wordiness += LuceneUtil.tokenizeString(Strings.nullToEmpty(comment)).size();
        }

        return wordiness;
    }
}
