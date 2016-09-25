package br.edu.utfpr.recominer.web.dto;

import br.edu.utfpr.recominer.core.model.MachineLearningPrediction;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class CochangeDTO {

    private Integer id;
    private FileDTO file;
    private String predictionResult;
    private String algorithmName;

    public CochangeDTO(MachineLearningPrediction cochange) {
        this.id = cochange.getId();
        this.file = new FileDTO(cochange.getPredictedFile());
        this.predictionResult = cochange.getPredictionResult();
        this.algorithmName = cochange.getAlgorithmType();
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
