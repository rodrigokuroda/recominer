package br.edu.utfpr.recominer.core.repository;

import br.edu.utfpr.recominer.core.model.PredictionFeedback;
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
public class MlPredictionFeedbackRepository extends JdbcRepository<PredictionFeedback, Integer> {

    public MlPredictionFeedbackRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "ml_prediction_feedback";

    public static final RowMapper<PredictionFeedback> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                PredictionFeedback feedback = new PredictionFeedback();
                feedback.setId(rs.getInt("id"));
                feedback.setPredictionId(rs.getInt("prediction_id"));
                feedback.setChanged(rs.getBoolean("changed"));
                feedback.setJustification(rs.getString("justification"));
                return feedback;
            };

    public static final RowUnmapper<PredictionFeedback> ROW_UNMAPPER
            = (PredictionFeedback feedback) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", feedback.getId());
                mapping.put("prediction_id", feedback.getPredictionId());
                mapping.put("changed", feedback.isChanged());
                mapping.put("justification", feedback.getJustification());
                return mapping;
            };

}
