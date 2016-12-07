package br.edu.utfpr.recominer.web.dto;

import br.edu.utfpr.recominer.core.model.AssociationRulePrediction;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.MachineLearningPrediction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private PredictionFeedbackDTO feedback;
    private Double probability;

    public CochangeDTO() {
    }

    public CochangeDTO(Integer id) {
        this.id = id;
    }
    
    public static CochangeDTO from(MachineLearningPrediction cochange) {
        CochangeDTO dto = new CochangeDTO();
        dto.id = cochange.getId();
        dto.file = new FileDTO(cochange.getPredictedFile());
        dto.predictionResult = cochange.getPredictionResult();
        dto.algorithmName = cochange.getAlgorithmType();
        dto.feedback = PredictionFeedbackDTO.from(cochange.getFeedback());
        final BigDecimal probability = BigDecimal.valueOf(cochange.getProbability() * 100).setScale(2, RoundingMode.DOWN);
        dto.probability = probability.doubleValue();
        
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
            dto.feedback = PredictionFeedbackDTO.from(cochange.getFeedback());
            final BigDecimal probability = BigDecimal.valueOf(cochange.getConfidence() * 100).setScale(2, RoundingMode.DOWN);
            dto.probability = dto.predictionResult.equals("N") ? 100 - probability.doubleValue() : probability.doubleValue();
            cochanges.add(dto);
        }
        
        return cochanges;
    }

    // TODO criar uma tabela com todos os arquivos 
    public static CochangeDTO from(File file) {
        CochangeDTO dto = new CochangeDTO();        
        dto.id = file.getId();
        dto.file = new FileDTO(file);
        dto.rank = null;
        dto.predictionResult = null;
        dto.algorithmName = null;
        dto.probability = .0d;
        dto.feedback  = new PredictionFeedbackDTO();
        
        return dto;
    }

//    public void append(CochangeDTO cochangeDTO) {
//        algorithmName += " / " + cochangeDTO.getAlgorithmName();
//        probability += " / " + cochangeDTO.getProbability();
//    }

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

    public PredictionFeedbackDTO getFeedback() {
        return feedback;
    }

    public void setFeedback(PredictionFeedbackDTO feedback) {
        this.feedback = feedback;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.file);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CochangeDTO other = (CochangeDTO) obj;
        if (!Objects.equals(this.file, other.file)) {
            return false;
        }
        return true;
    }

}
