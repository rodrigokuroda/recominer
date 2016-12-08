package br.edu.utfpr.recominer.core.repository;

import br.edu.utfpr.recominer.core.model.FeedbackJustification;
import br.edu.utfpr.recominer.core.model.Issue;
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
public class FeedbackJustificationRepository extends JdbcRepository<FeedbackJustification, Integer> {

    public FeedbackJustificationRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "feedback_justification";

    public static final RowMapper<FeedbackJustification> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                FeedbackJustification feedback = new FeedbackJustification(rs.getInt("id"), rs.getString("technique"));
                feedback.setIssue(new Issue(rs.getInt("issue_id")));
                feedback.setJustification(rs.getString("justification"));
                feedback.setSubmitDate(rs.getDate("submit_date"));
                return feedback;
            };

    public static final RowUnmapper<FeedbackJustification> ROW_UNMAPPER
            = (FeedbackJustification feedback) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", feedback.getId());
                mapping.put("issue_id", feedback.getIssue().getId());
                mapping.put("justification", feedback.getJustification());
                mapping.put("submit_date", feedback.getSubmitDate());
                mapping.put("technique", feedback.getTechnique());
                return mapping;
            };

}
