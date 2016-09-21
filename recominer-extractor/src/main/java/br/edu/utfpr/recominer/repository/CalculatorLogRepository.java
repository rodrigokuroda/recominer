package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.batch.calculator.CalculatorLog;
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
public class CalculatorLogRepository extends JdbcRepository<CalculatorLog, Integer> {

    public CalculatorLogRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, SCHEMA_NAME, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String SCHEMA_NAME = "recominer";
    private static final String TABLE_NAME = "calculator_log";

    public static final RowMapper<CalculatorLog> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                CalculatorLog calculatorLog = new CalculatorLog(rs.getInt("id"), 
                        new Project(rs.getInt("project_id")), rs.getString("metric"));
                calculatorLog.setStartDate(rs.getDate("start_date"));
                calculatorLog.setEndDate(rs.getDate("end_date"));
                calculatorLog.setLastCommitDate(rs.getDate("last_commit_date"));
                return calculatorLog;
            };
    
    public static final RowUnmapper<CalculatorLog> ROW_UNMAPPER
            = (CalculatorLog calculatorLog) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", calculatorLog.getId());
                mapping.put("project_id", calculatorLog.getProject().getId());
                mapping.put("metric", calculatorLog.getMetric());
                mapping.put("start_date", calculatorLog.getStartDate());
                mapping.put("end_date", calculatorLog.getEndDate());
                mapping.put("last_commit_date", calculatorLog.getLastCommitDate());
                return mapping;
            };

}
