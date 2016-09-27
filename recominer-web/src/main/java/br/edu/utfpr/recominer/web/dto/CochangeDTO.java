package br.edu.utfpr.recominer.web.dto;

import br.edu.utfpr.recominer.core.model.AssociationRulePrediction;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Fileset;
import br.edu.utfpr.recominer.core.model.MachineLearningPrediction;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class CochangeDTO {
    
    // TODO enum
    private static final String ASSOCIATION_RULE = "AssociationRule";

    private Integer id;
    private FileDTO file;
    private Integer rank;
    private String predictionResult;
    private String algorithmName;

    public CochangeDTO() {
    }
    
    public static CochangeDTO from(MachineLearningPrediction cochange) {
        CochangeDTO dto = new CochangeDTO();
        dto.id = cochange.getId();
        dto.file = new FileDTO(cochange.getPredictedFile());
        dto.predictionResult = cochange.getPredictionResult();
        dto.algorithmName = cochange.getAlgorithmType();
        
        return dto;
    }

    public static List<CochangeDTO> from(AssociationRulePrediction cochange) {
        List<CochangeDTO> cochanges = new ArrayList<>();
        final Set<File> fileset = cochange.getPredictedFileset().getFiles();
        if (fileset == null) {
            return cochanges;
        }
        for (File predictedFile : fileset) {
            CochangeDTO dto = new CochangeDTO();
            dto.id = cochange.getId();
            dto.file = new FileDTO(predictedFile);
            dto.rank = cochange.getRank();
            dto.predictionResult = cochange.getPredictionResult();
            dto.algorithmName = ASSOCIATION_RULE;
            cochanges.add(dto);
        }
        
        return cochanges;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FileDTO getFile() {
        return file;
    }

    public void setFile(FileDTO file) {
        this.file = file;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getPredictionResult() {
        return predictionResult;
    }

    public void setPredictionResult(String predictionResult) {
        this.predictionResult = predictionResult;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

}
