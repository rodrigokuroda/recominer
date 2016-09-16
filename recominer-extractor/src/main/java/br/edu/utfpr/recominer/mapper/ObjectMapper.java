package br.edu.utfpr.recominer.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class ObjectMapper implements ResultSetExtractor<List<Object[]>> {

    @Override
    public List<Object[]> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Object[]> records = new ArrayList<>();
        
        // call only once
        int cols = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            Object[] row = new Object[cols];
            for (int i = 0; i < cols; i++) {
                row[i] = rs.getObject(i + 1);
            }
            records.add(row);
        }
        
        return records;
    }

}
