package br.edu.utfpr.recominer.core.model;

import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class AssociationRulePrediction implements Persistable<Integer> {
    
    private Integer id;
    private Commit commit;
    private Fileset fileset;
    private Integer rank;
    private Fileset predictedFileset;
    private String predictionResult;

    public AssociationRulePrediction() {
    }

    public AssociationRulePrediction(Integer id) {
        this.id = id;
    }

    public AssociationRulePrediction(Commit commit, Fileset file, Integer rank, Fileset predictedFile, String predictionResult) {
        this.commit = commit;
        this.fileset = file;
        this.rank = rank;
        this.predictedFileset = predictedFile;
        this.predictionResult = predictionResult;
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

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public Fileset getFileset() {
        return fileset;
    }

    public void setFileset(Fileset fileset) {
        this.fileset = fileset;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Fileset getPredictedFileset() {
        return predictedFileset;
    }

    public void setPredictedFileset(Fileset predictedFileset) {
        this.predictedFileset = predictedFileset;
    }

    public String getPredictionResult() {
        return predictionResult;
    }

    public void setPredictionResult(String predictionResult) {
        this.predictionResult = predictionResult;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.id);
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
        final AssociationRulePrediction other = (AssociationRulePrediction) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
}
