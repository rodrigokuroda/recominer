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
    private PredictionFeedback feedback;
    private Double support;
    private Double confidence;
    private Long transactions;
    private Long totalTransactions;

    public AssociationRulePrediction() {
    }

    public AssociationRulePrediction(Integer id) {
        this.id = id;
    }

    public AssociationRulePrediction(Commit commit, Fileset file, Integer rank, Fileset predictedFile, 
            String predictionResult, Double support, Double confidence, Long transactions, Long totalTransactions) {
        this.commit = commit;
        this.fileset = file;
        this.rank = rank;
        this.predictedFileset = predictedFile;
        this.predictionResult = predictionResult;
        this.support = support;
        this.confidence = confidence;
        this.transactions = transactions;
        this.totalTransactions = totalTransactions;
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

    public PredictionFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(PredictionFeedback feedback) {
        this.feedback = feedback;
    }

    public Double getSupport() {
        return support;
    }

    public void setSupport(Double support) {
        this.support = support;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Long getTransactions() {
        return transactions;
    }

    public void setTransactions(Long transactions) {
        this.transactions = transactions;
    }

    public Long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(Long totalTransactions) {
        this.totalTransactions = totalTransactions;
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
