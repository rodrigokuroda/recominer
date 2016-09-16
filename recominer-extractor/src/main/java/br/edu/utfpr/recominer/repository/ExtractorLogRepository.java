package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.model.ExtractorLog;
import br.edu.utfpr.recominer.core.model.Project;
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
public class ExtractorLogRepository extends JdbcRepository<ExtractorLog, Integer> {

    private static final String SCHEMA_NAME = "recominer";
    private static final String TABLE_NAME = "extractor_log";
    private static final String ID_COLUMN = "id";

    public ExtractorLogRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, SCHEMA_NAME, TABLE_NAME, ID_COLUMN);
    }

    public static final RowMapper<ExtractorLog> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                ExtractorLog extractorLog = new ExtractorLog();
                extractorLog.setId(rs.getInt("id "));
                extractorLog.setProject(new Project(rs.getInt("project_id")));
                
                extractorLog.setGitProcessStartDate(rs.getDate("git_process_start_date"));
                extractorLog.setGitProcessEndDate(rs.getDate("git_process_end_date"));
                extractorLog.setGitProcessReturnCode(rs.getInt("git_process_return_code"));
                
                extractorLog.setCvsanalyProcessStartDate(rs.getDate("cvsanaly_process_start_date"));
                extractorLog.setCvsanalyProcessEndDate(rs.getDate("cvsanaly_process_end_date"));
                extractorLog.setCvsanalyProcessReturnCode(rs.getInt("cvsanaly_process_return_code"));
                
                extractorLog.setBichoProcessStartDate(rs.getDate("bicho_process_start_date"));
                extractorLog.setBichoProcessEndDate(rs.getDate("bicho_process_end_date"));
                extractorLog.setBichoProcessReturnCode(rs.getInt("bicho_process_return_code"));
                
                extractorLog.setAssociationProcessStartDate(rs.getDate("association_process_start_date"));
                extractorLog.setAssociationProcessEndDate(rs.getDate("association_process_end_date"));
                
                return extractorLog;
            };
    
    public static final RowUnmapper<ExtractorLog> ROW_UNMAPPER
            = (ExtractorLog extractorLog) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", extractorLog.getId());
                mapping.put("project_id", extractorLog.getProject().getId());
                
                mapping.put("git_process_start_date", extractorLog.getGitProcessStartDate());
                mapping.put("git_process_end_date", extractorLog.getGitProcessEndDate());
                mapping.put("git_process_return_code", extractorLog.getGitProcessReturnCode());
                
                mapping.put("cvsanaly_process_start_date", extractorLog.getCvsanalyProcessStartDate());
                mapping.put("cvsanaly_process_end_date", extractorLog.getCvsanalyProcessEndDate());
                mapping.put("cvsanaly_process_return_code", extractorLog.getCvsanalyProcessReturnCode());
                
                mapping.put("bicho_process_start_date", extractorLog.getBichoProcessStartDate());
                mapping.put("bicho_process_end_date", extractorLog.getBichoProcessEndDate());
                mapping.put("bicho_process_return_code", extractorLog.getBichoProcessReturnCode());
                
                mapping.put("association_process_start_date", extractorLog.getAssociationProcessStartDate());
                mapping.put("association_process_end_date", extractorLog.getAssociationProcessEndDate());
                return mapping;
            };

}
