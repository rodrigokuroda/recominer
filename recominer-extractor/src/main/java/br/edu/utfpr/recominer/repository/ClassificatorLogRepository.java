package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.batch.classificator.ClassificatorLog;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Repository
public class ClassificatorLogRepository extends JdbcRepository<ClassificatorLog, Integer> {

    public ClassificatorLogRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, SCHEMA_NAME, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "classificator_log";
    private static final String SCHEMA_NAME = "recominer";

    public static final RowMapper<ClassificatorLog> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                ClassificatorLog classificatorLog = new ClassificatorLog(new Project(rs.getInt("project_id")), rs.getString("type"));
                classificatorLog.setId(rs.getInt("id"));
                classificatorLog.setStartDate(rs.getDate("start_date"));
                classificatorLog.setEndDate(rs.getDate("end_date"));
                classificatorLog.setLastCommitDate(rs.getDate("last_commit_date"));
                return classificatorLog;
            };
    
    public static final RowUnmapper<ClassificatorLog> ROW_UNMAPPER
            = (ClassificatorLog classificatorLog) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", classificatorLog.getId());
                mapping.put("project_id", classificatorLog.getProject().getId());
                mapping.put("type", classificatorLog.getType());
                mapping.put("start_date", classificatorLog.getStartDate());
                mapping.put("end_date", classificatorLog.getEndDate());
                mapping.put("last_commit_date", classificatorLog.getLastCommitDate());
                return mapping;
            };

}
