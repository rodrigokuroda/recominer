package br.edu.utfpr.recominer.metric.network;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.QueryUtils;
import br.edu.utfpr.recominer.model.Commenter;
import br.edu.utfpr.recominer.model.Issue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kuroda
 */
public class BichoCommunicationNetworkDao implements CommunicationNetworkDao {

    private final GenericDao dao;
    private final String SELECT_COMMENTERS_BY_ISSUE_ORDER_BY_SUBMIT;

    public BichoCommunicationNetworkDao(GenericDao dao, Project project) {
        this.dao = dao;
        
        SELECT_COMMENTERS_BY_ISSUE_ORDER_BY_SUBMIT
                = QueryUtils.getQueryForDatabase("SELECT p.id, p.user_id, p.email, p.is_dev"
                        + "  FROM {0}_issues.comments c"
                        + "  JOIN {0}_issues.people p ON p.id = c.submitted_by"
                        + " WHERE c.issue_id = ?"
                        + " ORDER BY c.submitted_on ASC", project.getProjectName());
    }
    
    @Override
    public List<Commenter> selectCommenters(Issue issue) {
        List<Object[]> rawFilesPath
                = dao.selectNativeWithParams(SELECT_COMMENTERS_BY_ISSUE_ORDER_BY_SUBMIT,
                        new Object[]{issue});

        List<Commenter> commenters = new ArrayList<>();
        for (Object[] row : rawFilesPath) {
            Integer id = (Integer) row[0];
            String userId = (String) row[1];
            String email = (String) row[2];
            boolean isDev = ((Integer) row[3]) == 1;
            Commenter commenter = new Commenter(id, userId, email, isDev);
            commenters.add(commenter);
        }
        return commenters;
    }
    
}
