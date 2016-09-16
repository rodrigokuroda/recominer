package br.edu.utfpr.recominer.core.repository.helper;

import java.util.Map;

/**
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 * @since 12/19/12, 9:45 PM
 */
public class MissingRowUnmapper<T> implements RowUnmapper<T> {

    @Override
    public Map<String, Object> mapColumns(Object o) {
        throw new UnsupportedOperationException("This repository is read-only, it can't store or update entities");
    }
}
