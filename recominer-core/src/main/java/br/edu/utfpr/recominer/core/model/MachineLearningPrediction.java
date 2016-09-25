package br.edu.utfpr.recominer.core.model;

import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class MachineLearningPrediction implements Persistable<Integer> {
    
    private Integer id;
    private File file;
    private Commit commit;
    private File predictedFile;
    private String predictionResult;
    private String algorithmType;

    public MachineLearningPrediction(Integer id) {
        this.id = id;
    }

    public MachineLearningPrediction(File file, Commit commit, File predictedFile, String predictionResult, String algorithmType) {
        this.file = file;
        this.commit = commit;
        this.predictedFile = predictedFile;
        this.predictionResult = predictionResult;
        this.algorithmType = algorithmType;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public File getPredictedFile() {
        return predictedFile;
    }

    public void setPredictedFile(File predictedFile) {
        this.predictedFile = predictedFile;
    }

    public String getPredictionResult() {
        return predictionResult;
    }

    public void setPredictionResult(String predictionResult) {
        this.predictionResult = predictionResult;
    }

    public String getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(String algorithmType) {
        this.algorithmType = algorithmType;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
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
        final MachineLearningPrediction other = (MachineLearningPrediction) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
}
