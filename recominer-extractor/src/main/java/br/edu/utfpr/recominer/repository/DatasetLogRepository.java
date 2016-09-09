package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.batch.dataset.DatasetLog;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@Repository
public class DatasetLogRepository extends JdbcRepository<DatasetLog, Integer> {

    public DatasetLogRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, SCHEMA_NAME, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String SCHEMA_NAME = "recominer";
    private static final String TABLE_NAME = "dataset_log";
    
    public static final RowMapper<DatasetLog> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                final Project project = new Project(rs.getInt("project_id"));
                final String type = rs.getString("type");
                
                DatasetLog datasetLog = new DatasetLog(project, type);
                datasetLog.setId(rs.getInt("id"));
                datasetLog.setStartDate(rs.getDate("start_date"));
                datasetLog.setEndDate(rs.getDate("end_date"));
                
                return datasetLog;
            };
    
    public static final RowUnmapper<DatasetLog> ROW_UNMAPPER
            = (DatasetLog datasetLog) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", datasetLog.getId());
                mapping.put("project_id", datasetLog.getProject().getId());
                mapping.put("type", datasetLog.getType());
                mapping.put("start_date", datasetLog.getStartDate());
                mapping.put("end_date", datasetLog.getEndDate());
                return mapping;
            };

}
