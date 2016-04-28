package br.edu.utfpr.recominer.dao;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.metric.network.CommunicationNetworkDao;
import br.edu.utfpr.recominer.model.Commenter;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.Issue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class RecominerMetricDao
        implements CommunicationNetworkDao {

    private final Project project;
    private final GenericDao dao;

    public RecominerMetricDao(final GenericDao dao, final Project project) {
        this.project = project;
        this.dao = dao;
    }

    @Override
    public List<Commenter> selectCommenters(final Issue issue) {
        final StringBuilder selectCommenters
                = new StringBuilder(QueryUtils.getQueryForDatabase("SELECT p.id, p.user_id, p.email, p.is_dev"
                        + "  FROM {0}_issues.comments c"
                        + "  JOIN {0}_issues.issues i ON i.id = c.issue_id"
                        + "  JOIN {0}_issues.people p ON p.id = c.submitted_by"
                        // ignore continuous integration user
                        + " WHERE UPPER(p.user_id) <> ?"
                        + "   AND i.id = ?"
                        + " ORDER BY c.submitted_on ASC", project.getProjectName()));

        final List<Object[]> rawFilesPath
                = dao.selectNativeWithParams(selectCommenters.toString(), "HUDSON", issue.getId());

        final List<Commenter> files = new ArrayList<>();
        for (Object[] row : rawFilesPath) {
            Integer id = (Integer) row[0];
            String userId = (String) row[1];
            String email = (String) row[2];
            boolean isDev = ((Integer) row[3]) == 1;
            Commenter commenter = new Commenter(id, userId, email, isDev);
            files.add(commenter);
        }

        return files;
    }

    public void selectPairFiles(final File file) {
        final StringBuilder selectPairFiles
                = new StringBuilder(QueryUtils.getQueryForDatabase(
                        "SELECT fpic.issue_id, fpic.commit_id "
                        + "  FROM avro.file_pair_issue_commit fpic "
                        + "  JOIN avro.file_pairs fp ON fp.id = fpic.file_pair_id "
                        + " WHERE fp.file1_id = ? OR fp.file2_id = ?",
                        project.getProjectName()));
        
        final List<Object[]> rawIssuesCommits
                = dao.selectNativeWithParams(selectPairFiles.toString(), file.getId(), file.getId());

    }
}
