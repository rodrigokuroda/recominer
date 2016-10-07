package br.edu.utfpr.recominer.batch.classificator;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class TrainingDataStrategyBuilder {
    
    private static TrainingDataStrategy trainingDataStrategy;
    
    public static TrainingDataStrategy build() {
        return trainingDataStrategy;
    }
    
    public static TrainingDataStrategyBuilder forIssueKey(String issueKey) {
        return null;
    }
}
