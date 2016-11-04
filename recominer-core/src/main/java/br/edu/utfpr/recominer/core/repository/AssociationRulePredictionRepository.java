package br.edu.utfpr.recominer.core.repository;

import br.edu.utfpr.recominer.core.model.AssociationRule;
import br.edu.utfpr.recominer.core.model.AssociationRulePrediction;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Fileset;
import br.edu.utfpr.recominer.core.model.PredictionFeedback;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Repository
public class AssociationRulePredictionRepository extends JdbcRepository<AssociationRulePrediction, Integer> {

    @Autowired
    private FilesetSequenceRepository filesetSequence;

    @Autowired
    private FilesetRepository filesetRepository;

    public AssociationRulePredictionRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    @Override
    public void setProject(Project project) {
        super.setProject(project);
        filesetSequence.setProject(project);
        filesetRepository.setProject(project);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "ar_prediction";

    public static final RowMapper<AssociationRulePrediction> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                AssociationRulePrediction associationRulePrediction = new AssociationRulePrediction();

                associationRulePrediction.setId(rs.getInt("id"));
                associationRulePrediction.setCommit(new Commit(rs.getInt("commit_id")));
                associationRulePrediction.setFileset(new Fileset(rs.getLong("fileset_id")));
                associationRulePrediction.setRank(rs.getInt("rank"));
                associationRulePrediction.setPredictionResult(rs.getString("prediction_result"));
                associationRulePrediction.setPredictedFileset(new Fileset(rs.getLong("predicted_fileset_id")));
                associationRulePrediction.setSupport(rs.getDouble("support"));
                associationRulePrediction.setConfidence(rs.getDouble("confidence"));
                associationRulePrediction.setTransactions(rs.getLong("transactions"));
                associationRulePrediction.setTotalTransactions(rs.getLong("total_transactions"));

                return associationRulePrediction;
            };

    public static final RowUnmapper<AssociationRulePrediction> ROW_UNMAPPER
            = (AssociationRulePrediction associationRulePrediction) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", associationRulePrediction.getId());
                mapping.put("commit_id", associationRulePrediction.getCommit().getId());
                mapping.put("fileset_id", associationRulePrediction.getFileset().getId());
                mapping.put("rank", associationRulePrediction.getRank());
                mapping.put("prediction_result", associationRulePrediction.getPredictionResult());
                mapping.put("predicted_fileset_id", associationRulePrediction.getPredictedFileset().getId());
                mapping.put("support", associationRulePrediction.getSupport());
                mapping.put("confidence", associationRulePrediction.getConfidence());
                mapping.put("transactions", associationRulePrediction.getTransactions());
                mapping.put("total_transactions", associationRulePrediction.getTotalTransactions());
                return mapping;
            };

    /**
     * Save the same antecedent items for all AssociationRule.
     *
     * @param forCommit
     * @param associationRules
     * @param topPredictions The n top association rule to consider as
     * need-to-change
     */
    public void savePrediction(Commit forCommit, List<AssociationRule<File>> associationRules, Integer topPredictions) {
        if (associationRules.isEmpty()) {
            return;
        }
        Fileset antecedentFileset = new Fileset(filesetSequence.getNext(), associationRules.get(0).getAntecedentItem());
        filesetRepository.insert(antecedentFileset);

        int rank = 1;
        AssociationRule<File> lastPositiveAr = null;
        for (AssociationRule<File> ar : associationRules) {
            Fileset consequentFileset = new Fileset(filesetSequence.getNext(), ar.getConsequentItems());
            filesetRepository.insert(consequentFileset);

            String predictionResult;
            if (rank < topPredictions
                    || ar.hasSameSupportAndConfidenteOf(lastPositiveAr)) {
                predictionResult = "C";
                lastPositiveAr = ar;
            } else {
                predictionResult = "N";
            }

            AssociationRulePrediction prediction
                    = new AssociationRulePrediction(forCommit, antecedentFileset, rank++,
                            consequentFileset, predictionResult,
                            (double) ar.getSupport(), ar.getConfidence(),
                            ar.getAntecedentTransactions(), (long) ar.getTransactions().size());
            save(prediction);
        }
    }

    public List<AssociationRulePrediction> selectPrediction(Commit forCommit) {
        List<AssociationRulePrediction> predictions = jdbcOperations.query(
                "SELECT id, commit_id, fileset_id, rank, predicted_fileset_id, support, confidence, transactions, total_transactions "
                + "  FROM {0}.fileset "
                + " WHERE commit_id = ?",
                ROW_MAPPER);

        for (AssociationRulePrediction prediction : predictions) {

            Fileset antecedent = filesetRepository.findOne(prediction.getFileset().getId());
            prediction.setFileset(antecedent);

            Fileset consequent = filesetRepository.findOne(prediction.getPredictedFileset().getId());
            prediction.setPredictedFileset(consequent);
        }

        return predictions;
    }

    public List<AssociationRulePrediction> selectPredictedCochangesFor(Commit commit, File file) {
        List<AssociationRulePrediction> predictions = jdbcOperations.query(
                getQueryForSchema(
                        "SELECT DISTINCT arp.*, "
                        + "     pfs.file_id, "
                        + "     pf.file_path, "
                        + "     pfb.id AS prediction_feedback_id, "
                        + "     pfb.changed, "
                        + "     pfb.justification "
                        + "  FROM {0}.ar_prediction arp "
                        + "  JOIN {0}.fileset fs ON fs.id = arp.fileset_id "
                        + "  LEFT JOIN {0}.fileset pfs ON pfs.id = arp.predicted_fileset_id "
                        + "  LEFT JOIN {0}.files_commits pf ON pf.file_id = pfs.file_id AND pf.commit_id = "
                        + "      (SELECT MAX(ifc.commit_id) FROM {0}.files_commits ifc WHERE ifc.file_id = pfs.file_id AND ifc.commit_id < arp.commit_id) "
                        + "  LEFT JOIN {0}.prediction_feedback pfb ON pfb.prediction_id = arp.id "
                        + " WHERE fs.file_id = ? "
                        + "   AND arp.commit_id = ? "
                        + "   AND arp.prediction_result = \"C\" "
                        + " ORDER BY rank ASC "),
                (ResultSet rs, int rowNum) -> {
                    AssociationRulePrediction associationRulePrediction = ROW_MAPPER.mapRow(rs, rowNum);

                    final Set<File> predictedFiles = new HashSet<>(1);
                    predictedFiles.add(new File(rs.getInt("file_id"), rs.getString("file_path")));

                    final Fileset predictedFileset = new Fileset(rs.getLong("predicted_fileset_id"));
                    predictedFileset.setFile(predictedFiles);

                    associationRulePrediction.setPredictedFileset(predictedFileset);

                    final int feedbackId = rs.getInt("prediction_feedback_id");
                    final PredictionFeedback predictionFeedback = new PredictionFeedback(feedbackId == 0 ? null : feedbackId);
                    predictionFeedback.setChanged(rs.getBoolean("changed"));
                    predictionFeedback.setPredictionId(rs.getInt("id"));
                    predictionFeedback.setJustification(rs.getString("justification"));
                    associationRulePrediction.setFeedback(predictionFeedback);

                    return associationRulePrediction;
                },
                file.getId(), commit.getId()
        );

        return predictions;
    }
}
