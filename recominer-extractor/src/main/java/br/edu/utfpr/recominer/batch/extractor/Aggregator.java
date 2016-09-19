package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.model.svn.Scmlog;

/**
 * Strategy for aggregation between issues and commits.
 * 
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public interface Aggregator {

    void aggregate(Iterable<Scmlog> commits);
}
