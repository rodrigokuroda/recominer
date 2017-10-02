package br.edu.utfpr.recominer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Parameters:
 *   --skipExtractor=true|false
 *   
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Component
public class ConfigurationValues {
    
    @Value("${skipExtractor}") 
    private String skipExtractor;

    public boolean skipExtractor() {
        return Boolean.valueOf(skipExtractor);
    }
    
    public String getSkipExtractor() {
        return skipExtractor;
    }
}