package br.edu.utfpr.recominer.core.util;

import br.edu.utfpr.recominer.core.model.Project;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class QueryUtilsTest {

    @Test
    public void testGetQueryForDatabaseProjectSchema() {
        String sql = QueryUtils.getQueryForDatabase(
                "SELECT * FROM {0}.table;",
                new Project(1, "test", "test_schema", "", null, null));
        assertEquals("SELECT * FROM test_schema.table;", sql);
    }

    @Test
    public void testGetQueryForDatabase3args() {
        String sql = QueryUtils.getQueryForDatabase(
                "SELECT * FROM {0}.{1};",
                new Project(1, "test", "test_schema", "", null, null), 
                "test_table");
        assertEquals("SELECT * FROM test_schema.test_table;", sql);
    }

    @Test
    public void testRemoveComments() {
        String sql = QueryUtils.removeComments("-- This comment should be removed.\n"
                + "SELECT * FROM test;\n");
        assertEquals("SELECT * FROM test;\n", sql);
    }

    @Test
    public void testRemoveCommentsWithSpace() {
        String sql = QueryUtils.removeComments("\r\n\r\n-- SQL's comment should be removed\r\n"
                + "SELECT * FROM test;\n");
        assertEquals("SELECT * FROM test;\n", sql);
    }

}
