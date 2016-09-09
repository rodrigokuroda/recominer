package br.edu.utfpr.recominer.metric.network;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public interface NetworkBuilder {

    Network<String, String> build(Issue issue, Commit commit);
    
}
