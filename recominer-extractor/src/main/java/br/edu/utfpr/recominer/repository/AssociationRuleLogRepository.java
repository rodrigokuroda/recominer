package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.batch.associationrule.AssociationRuleLog;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import br.edu.utfpr.recominer.core.repository.helper.TableDescription;
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
public class AssociationRuleLogRepository extends JdbcRepository<AssociationRuleLog, Integer> {

    public AssociationRuleLogRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    public void setProject(Project project) {
        setTable(new TableDescription(project.getProjectName(), TABLE_NAME, ID_COLUMN));
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "association_rule_log";

    public static final RowMapper<AssociationRuleLog> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                AssociationRuleLog associationRuleLog = new AssociationRuleLog(new Project(rs.getInt("project_id")), rs.getString("type"));
                associationRuleLog.setStartDate(rs.getDate("start_date"));
                associationRuleLog.setEndDate(rs.getDate("end_date"));
                return associationRuleLog;
            };

    public static final RowUnmapper<AssociationRuleLog> ROW_UNMAPPER
            = (AssociationRuleLog associationRuleLog) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", associationRuleLog.getId());
                mapping.put("project", associationRuleLog.getProject().getId());
                mapping.put("type", associationRuleLog.getType());
                mapping.put("start_date", associationRuleLog.getStartDate());
                mapping.put("end_date", associationRuleLog.getEndDate());
                return mapping;
            };

}
