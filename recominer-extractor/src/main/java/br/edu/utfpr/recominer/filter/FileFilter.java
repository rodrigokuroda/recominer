package br.edu.utfpr.recominer.filter;

import br.edu.utfpr.recominer.core.model.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Creates <code>Predicate<? super File></code>s for File filtering.
 * 
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class FileFilter {

    /**
     * Receives a String containing names of files separated by comma.
     * 
     * @param filter String of names separated by comma.
     * @return Predicate Filtering for Stream.
     */
    public static Predicate<File> getFilterByFilename(String filter) {
        if (filter == null || StringUtils.isBlank(filter)) {
            return f -> true;
        }
        Set<String> names = getFiltersFromString(filter);
        return f -> !names.contains(f.getFileName());
    }
    
    /**
     * Receives a String containing names of files separated by comma.
     * 
     * @param filter String of names separated by comma.
     * @return Filenames to filter.
     */
    public static Set<String> getFiltersFromString(String filter) {
        if (filter == null || StringUtils.isBlank(filter)) {
            return Collections.EMPTY_SET;
        }
        Set<String> names = Arrays.stream(filter.split(",")).collect(Collectors.toSet());
        return names;
    }
}
