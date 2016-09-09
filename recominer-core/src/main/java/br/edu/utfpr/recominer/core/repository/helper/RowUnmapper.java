package br.edu.utfpr.recominer.core.repository.helper;

import java.util.Map;

public interface RowUnmapper<T> {

    Map<String, Object> mapColumns(T t);
}
