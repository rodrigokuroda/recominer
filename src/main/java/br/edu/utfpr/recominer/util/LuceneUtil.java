package br.edu.utfpr.recominer.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 *
 * @author geazzy
 */
public final class LuceneUtil {

    //Remove os stopwords;
    public static List<String> tokenizeString(String linha) {
       
        Analyzer analyzer = new StopAnalyzer();

        List<String> result = new ArrayList<>();

        try {
            TokenStream stream = analyzer.tokenStream(null, new StringReader(linha));
            stream.reset();
            while (stream.incrementToken()) {

                result.add(stream.getAttribute(CharTermAttribute.class).toString());

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());

        }

        return result;
    }

}
