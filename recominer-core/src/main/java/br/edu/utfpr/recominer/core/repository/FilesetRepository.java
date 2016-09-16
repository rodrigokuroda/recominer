package br.edu.utfpr.recominer.core.repository;

import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Fileset;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Repository
public class FilesetRepository extends JdbcRepository<Fileset, Long> {

    public FilesetRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "fileset";

    public static final RowMapper<Fileset> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                Fileset fileset = new Fileset(rs.getLong("id"));
                return fileset;
            };

    public static final RowUnmapper<Fileset> ROW_UNMAPPER
            = (Fileset fileset) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", fileset.getId());
                return mapping;
            };

    public void insert(Fileset fileset) {
        Iterator<File> files = fileset.getFiles().iterator();
        
        jdbcOperations.batchUpdate(
                getQueryForSchema("INSERT INTO {0}.fileset (id, file_id) VALUES (?, ?)"),
                new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                File file = files.next();

                ps.setLong(1, fileset.getId());
                ps.setLong(2, file.getId());
            }

            @Override
            public int getBatchSize() {
                return fileset.getFiles().size();
            }
        });
    }
}
