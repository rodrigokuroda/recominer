package br.edu.utfpr.recominer.core.repository;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.MachineLearningPrediction;
import br.edu.utfpr.recominer.core.model.PredictionFeedback;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Repository
public class MachineLearningPredictionRepository extends JdbcRepository<MachineLearningPrediction, Integer> {

    public MachineLearningPredictionRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "ml_prediction";

    public static final RowMapper<MachineLearningPrediction> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                MachineLearningPrediction machineLearningPrediction = new MachineLearningPrediction(rs.getInt("id"));
                machineLearningPrediction.setFile(new File(rs.getInt("file_id")));
                machineLearningPrediction.setCommit(new Commit(rs.getInt("commit_id")));
                machineLearningPrediction.setPredictedFile(new File(rs.getInt("predicted_file_id"), rs.getString("file_path")));
                machineLearningPrediction.setPredictionResult(rs.getString("prediction_result"));
                machineLearningPrediction.setAlgorithmType(rs.getString("algorithm_name"));
                machineLearningPrediction.setProbability(rs.getDouble("probability"));
                return machineLearningPrediction;
            };

    public static final RowUnmapper<MachineLearningPrediction> ROW_UNMAPPER
            = (MachineLearningPrediction machineLearningPrediction) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", machineLearningPrediction.getId());
                mapping.put("file_id", machineLearningPrediction.getFile().getId());
                mapping.put("commit_id", machineLearningPrediction.getCommit().getId());
                mapping.put("predicted_file_id", machineLearningPrediction.getPredictedFile().getId());
                mapping.put("prediction_result", machineLearningPrediction.getPredictionResult());
                mapping.put("algorithm_name", machineLearningPrediction.getAlgorithmType());
                mapping.put("probability", machineLearningPrediction.getProbability());
                return mapping;
            };

    public List<MachineLearningPrediction> selectPredictedCochangesFor(Commit commit, File file) {
        return jdbcOperations.query(
                getQueryForSchema(
                        "SELECT DISTINCT mlp.id, mlp.file_id, "
                        + "     mlp.commit_id, "
                        + "     mlp.predicted_file_id, "
                        + "     mlp.prediction_result, "
                        + "     mlp.algorithm_name, "
                        + "     mlp.probability, "
                        + "     pfb.id AS prediction_feedback_id, "
                        + "     pfb.changed, "
                        + "     pfb.justification, "
                        + "     f.file_path"
                        + "  FROM {0}.ml_prediction mlp "
                        + "  JOIN {0}.files_commits f ON f.file_id = mlp.predicted_file_id "
                        + "   AND f.commit_id = (SELECT MAX(ifc.commit_id) "
                        + "                        FROM {0}.files_commits ifc "
                        + "                       WHERE ifc.commit_id <= mlp.commit_id "
                        + "                         AND ifc.file_id = mlp.predicted_file_id)"
                        + "  LEFT JOIN {0}.prediction_feedback pfb ON pfb.prediction_id = mlp.id "
                        + " WHERE mlp.file_id = ? "
                        + "   AND mlp.commit_id = ? "
                        //+ "   AND mlp.prediction_result = \"C\" "
                        + " ORDER BY mlp.prediction_result ASC, mlp.probability DESC"),
                (ResultSet rs, int rowNum) -> {
                    MachineLearningPrediction machineLearningPrediction = ROW_MAPPER.mapRow(rs, rowNum);

                    final int feedbackId = rs.getInt("prediction_feedback_id");
                    final PredictionFeedback predictionFeedback = new PredictionFeedback(feedbackId == 0 ? null : feedbackId);
                    predictionFeedback.setChanged(rs.getBoolean("changed"));
                    predictionFeedback.setPredictionId(rs.getInt("id"));
                    predictionFeedback.setJustification(rs.getString("justification"));
                    machineLearningPrediction.setFeedback(predictionFeedback);

                    return machineLearningPrediction;
                },
                file.getId(), commit.getId());
    }

}
