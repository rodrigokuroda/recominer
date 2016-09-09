package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.core.model.IssueTracker;
import br.edu.utfpr.recominer.core.model.IssueTrackerSystem;
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
public class IssueTrackerRepository extends JdbcRepository<IssueTracker, Integer> {

    private static final String SCHEMA_NAME = "recominer";
    private static final String TABLE_NAME = "issue_tracker";
    private static final String ID_COLUMN = "id";

    public IssueTrackerRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, SCHEMA_NAME, TABLE_NAME, ID_COLUMN);
    }

    public static final RowMapper<IssueTracker> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                IssueTracker issueTracker = new IssueTracker();
                issueTracker.setId(rs.getInt("id"));
                issueTracker.setSystem(IssueTrackerSystem.valueOf(rs.getString("system")));
                issueTracker.setUsername(rs.getString("username"));
                issueTracker.setPassword(rs.getString("password"));
                issueTracker.setToken(rs.getString("token"));
                issueTracker.setExtractionDelay(rs.getInt("extraction_delay"));
                return issueTracker;
            };
    
    public static final RowUnmapper<IssueTracker> ROW_UNMAPPER
            = (IssueTracker issueTracker) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", issueTracker.getId());
                mapping.put("system", issueTracker.getSystem().toString());
                mapping.put("username", issueTracker.getUsername());
                mapping.put("password", issueTracker.getPassword());
                mapping.put("token", issueTracker.getToken());
                mapping.put("extraction_delay", issueTracker.getExtractionDelay());
                return mapping;
            };
}
