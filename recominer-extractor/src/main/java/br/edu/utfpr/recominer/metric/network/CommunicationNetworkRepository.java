package br.edu.utfpr.recominer.metric.network;

import br.edu.utfpr.recominer.model.Commenter;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;
import java.util.List;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public interface CommunicationNetworkRepository {

    List<Commenter> selectCommenters(Issue issue, Commit commit);
}
