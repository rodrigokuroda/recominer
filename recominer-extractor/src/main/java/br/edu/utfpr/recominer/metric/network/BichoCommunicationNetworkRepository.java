package br.edu.utfpr.recominer.metric.network;

import br.edu.utfpr.recominer.model.Commenter;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author kuroda
 */
public class BichoCommunicationNetworkRepository implements CommunicationNetworkRepository {

    private final String SELECT_COMMENTERS_BY_ISSUE_ORDER_BY_SUBMIT;
    private final JdbcTemplate template;

    public BichoCommunicationNetworkRepository(Project project, JdbcTemplate template) {
        SELECT_COMMENTERS_BY_ISSUE_ORDER_BY_SUBMIT
                = QueryUtils.getQueryForDatabase("SELECT p.id, p.user_id, p.email, p.is_dev"
                        + "  FROM {0}_issues.comments c"
                        + "  JOIN {0}_issues.people p ON p.id = c.submitted_by"
                        + " WHERE c.issue_id = ?"
                        + "   AND c.submitted_on < (SELECT date FROM {0}_vcs.scmlog s WHERE s.id = ?)"
                        + " ORDER BY c.submitted_on ASC", project);
        this.template = template;
    }

    @Override
    public List<Commenter> selectCommenters(Issue issue, Commit commit) {
        return template.query(SELECT_COMMENTERS_BY_ISSUE_ORDER_BY_SUBMIT,
                (ResultSet rs, int rowNum) -> new Commenter(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBoolean(4)),
                issue.getId(), commit.getId());
    }

}
