package br.edu.utfpr.recominer.metric.network;

import br.edu.utfpr.recominer.model.Issue;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public interface NetworkBuilder {

    Network<String, String> build(final Issue issue);
    
}
