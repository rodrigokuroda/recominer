package br.edu.utfpr.recominer.metric.discussion;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author geazzy, Rodrigo T. Kuroda
 */
public final class LuceneUtil {
    
    private static final Logger log = LoggerFactory.getLogger(LuceneUtil.class);

    // remove stopwords like "a", "an", "the", etc.
    public static List<String> tokenizeString(String linha) {

        Analyzer analyzer = new StopAnalyzer();

        List<String> result = new ArrayList<>();

        try {
            // every extracted body/comment by Bicho begins with "p>"
            final StringReader stringReader = new StringReader(linha.replaceFirst("p>", ""));
            
            // remove all HTML tags from text
            final HTMLStripCharFilter stringFilter = new HTMLStripCharFilter(stringReader);
                    
            TokenStream stream = analyzer.tokenStream(null, stringFilter);
            stream.reset();
            while (stream.incrementToken()) {

                result.add(stream.getAttribute(CharTermAttribute.class).toString());

            }
        } catch (IOException e) {
            log.error("Error to tokenize string.", e);
        }

        return result;
    }
}
