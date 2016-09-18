package br.edu.utfpr.recominer.core.repository;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.MachineLearningPrediction;
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
                machineLearningPrediction.setPredictedFile(new File(rs.getInt("predicted_file_id")));
                machineLearningPrediction.setPredictionResult(rs.getString("prediction_result"));
                machineLearningPrediction.setAlgorithmType(rs.getString("algorithm_type"));
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
                mapping.put("algorithm_type", machineLearningPrediction.getAlgorithmType());
                return mapping;
            };

    public List<MachineLearningPrediction> selectPredictedCochangesFor(Commit commit, File file) {
        return jdbcOperations.query(
                getQueryForSchema(
                        "SELECT * FROM {0}.ml_prediction "
                        + " WHERE file_id = ? "
                        + "   AND commit_id = ? "),
                rowMapper,
                file.getId(), commit.getId());
    }

}
