package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.batch.associationrule.AssociationRulePrediction;
import br.edu.utfpr.recominer.batch.associationrule.Fileset;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.model.associationrule.AssociationRule;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@Repository
public class AssociationRulePredictionRepository extends JdbcRepository<AssociationRulePrediction, Integer> {

    @Inject
    private FilesetSequenceRepository filesetSequence;

    @Inject
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
                associationRulePrediction.setPredictedFileset(new Fileset(rs.getLong("predicted_fileset_id")));

                return associationRulePrediction;
            };

    public static final RowUnmapper<AssociationRulePrediction> ROW_UNMAPPER
            = (AssociationRulePrediction associationRulePrediction) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", associationRulePrediction.getId());
                mapping.put("commit_id", associationRulePrediction.getCommit().getId());
                mapping.put("fileset_id", associationRulePrediction.getFileset().getId());
                mapping.put("rank", associationRulePrediction.getRank());
                mapping.put("predicted_fileset_id", associationRulePrediction.getPredictedFileset().getId());
                return mapping;
            };

    /**
     * Save the same antecedent items for all AssociationRule.
     * 
     * @param forCommit
     * @param associationRules 
     */
    public void savePrediction(Commit forCommit, List<AssociationRule<File>> associationRules) {
        if (associationRules.isEmpty()) {
            return;
        }
        Fileset antecedentFileset = new Fileset(filesetSequence.getNext(), associationRules.get(0).getAntecedentItem());
        filesetRepository.insert(antecedentFileset);
        
        int rank = 0;
        for (AssociationRule<File> ar : associationRules) {
            Fileset consequentFileset = new Fileset(filesetSequence.getNext(), ar.getConsequentItems());
            filesetRepository.insert(consequentFileset);

            AssociationRulePrediction prediction
                    = new AssociationRulePrediction(forCommit, antecedentFileset, rank++, consequentFileset);
            save(prediction);
        }
    }
    
    public List<AssociationRulePrediction> selectPrediction(Commit forCommit) {
        List<AssociationRulePrediction> predictions = jdbcOperations.query(
                "SELECT id, commit_id, fileset_id, rank, predicted_fileset_id FROM {0}.fileset "
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

}
