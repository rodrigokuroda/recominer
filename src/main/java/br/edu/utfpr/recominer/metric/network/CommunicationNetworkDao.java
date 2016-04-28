package br.edu.utfpr.recominer.metric.network;

import br.edu.utfpr.recominer.model.Commenter;
import br.edu.utfpr.recominer.model.Issue;
import java.util.List;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public interface CommunicationNetworkDao {

    List<Commenter> selectCommenters(Issue issue);
}
