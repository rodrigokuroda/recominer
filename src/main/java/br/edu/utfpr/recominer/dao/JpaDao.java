package br.edu.utfpr.recominer.dao;

import java.util.List;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public interface JpaDao {

    <T> T selectNativeOneWithParams(String select, Object... objects);

    List selectNativeWithParams(String select, Object... objects);

    <T> T selectOneWithParams(String select, String[] params, Object... objects);

}
