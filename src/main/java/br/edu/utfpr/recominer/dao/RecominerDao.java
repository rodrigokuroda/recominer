package br.edu.utfpr.recominer.dao;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.batch.extractor.IssueTracker;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.Issue;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Rodrigo Kuroda
 */
public class RecominerDao {

    private final GenericDao dao;

    public RecominerDao(GenericDao dao) {
        this.dao = dao;
    }

    public List<Project> selectProject(Integer id) {
        final List<Project> projects = new ArrayList<>();
        String query
                = "SELECT id,"
                + "    issue_tracker_url,"
                + "    last_its_update,"
                + "    last_vcs_update,"
                + "    project_name,"
                + "    repository_path,"
                + "    version_control_url,"
                + "    issue_tracker,"
                + "    version_control,"
                + "    last_commit_date_analyzed,"
                + "    last_issue_update_analyzed,"
                + "    last_issue_update_analyzed_for_cochange,"
                + "    last_apriori_update"
                + " FROM recominer.project";
        final List<Object> params = new ArrayList<>(1);
        if (id != null) {
            final Long projectId = Long.valueOf(id.toString());
            params.add(projectId);
            query += " WHERE id = ?";
        }

        final List<Object[]> rawProjects = dao.selectNativeWithParams(query, params.toArray());
        for (Object[] rawProject : rawProjects) {
            Project project = new Project(rawProject);
            project.setIssueTracker(selectIssueTracker(project.getIssueTracker()));
            projects.add(project);
        }

        return projects;
    }

    public IssueTracker selectIssueTracker(IssueTracker issueTracker) {
        final String query
                = "SELECT"
                + "    mining_delay,"
                + "    password,"
                + "    system,"
                + "    token,"
                + "    username"
                + " FROM recominer.issue_tracker"
                + " WHERE id = ?";

        final List<Object[]> its
                = dao.selectNativeWithParams(query, issueTracker.getId());

        return new IssueTracker(its.get(0));
    }

    public void updateProjectUpdate(final Project project) {
        final String update = "UPDATE recominer.project"
                + " SET"
                + " last_its_update = ?,"
                + " last_vcs_update = ?,"
                + " last_commit_date_analyzed = ?,"
                + " last_issue_update_analyzed = ?,"
                + " last_issue_update_analyzed_for_cochange = ?,"
                + " last_apriori_update = ?"
                + " WHERE id = ?";

        dao.executeNativeQuery(update,
                project.getLastItsUpdate(),
                project.getLastVcsUpdate(),
                project.getLastCommitDateAnalyzed(),
                project.getLastIssueUpdateAnalyzed(),
                project.getLastIssueUpdateAnalyzedForCochange(),
                project.getLastAprioriUpdate(),
                project.getId()
        );
    }

    public Set<FilePair> selectFilePair(final Project project) {
        final String selectFilePairs = QueryUtils.getQueryForDatabase(
                "SELECT file_pair_id, file1_id, f1.file_path, file2_id, f2.file_path "
                + "  FROM {0}.file_pairs fp "
                + "  JOIN {0}.files f1 ON f1.id = pf.file1_id"
                + "  JOIN {0}.files f2 ON f2.id = pf.file2_id" //                + "  JOIN {0}.file_pair_apriori fpa ON fpa.file_pair_id = fp.id "
                //                + " WHERE (fpa.file1_issues > 1 OR fpa.file2_issues > 1) "
                //                + "   AND (fpa.file1_confidence > 0.5 OR fpa.file2_confidence > 0.5) "
                , project.getProjectName());

        final List<Object[]> rawFilePairs = dao.selectNativeWithParams(selectFilePairs);
        return pairFileMapper(rawFilePairs);
    }

    private Set<FilePair> pairFileMapper(final List<Object[]> rawFilePairs) {
        return rawFilePairs.stream()
                .map(o -> new FilePair((Integer) o[0],
                        new File((Integer) o[1], (String) o[2]),
                        new File((Integer) o[3], (String) o[4])))
                .collect(Collectors.toSet());
    }

    public Set<FilePair> selectFilePairInOpenedIssues(final Project project) {
        final String selectFilePairs = QueryUtils.getQueryForDatabase(
                "SELECT file_pair_id, file1_id, f1.file_path, file2_id, f2.file_path "
                + "  FROM {0}.file_pairs fp "
                + "  JOIN {0}.files f1 ON f1.id = pf.file1_id"
                + "  JOIN {0}.files f2 ON f2.id = pf.file2_id"
                + "  JOIN {0}.issues_scmlog i2s ON i2s.commit_id = fpi.issue_id", project.getProjectName());

        final List<Object[]> rawFilePairs = dao.selectNativeWithParams(selectFilePairs);
        return pairFileMapper(rawFilePairs);
    }

    public Set<Issue> selectLastNonFixedIssues(final Project project) {
        final StringBuilder selectNonFixedIssuesUpdated
                = new StringBuilder();

        selectNonFixedIssuesUpdated
                .append(QueryUtils
                        .getQueryForDatabase("SELECT i.id, i.type, i.submitted_on, i.fixed_on "
                                + "  FROM {0}_issues.issues i "
                                + "  JOIN {0}.issues_scmlog i2s ON i2s.issue_id = i.id "
                                + " WHERE i.fixed_on IS NULL AND i.resolution = \"Unresolved\" ",
                                project.getProjectName()));

        final List<Object> params = new ArrayList<>();
        if (project.getLastIssueUpdateForMetrics() != null) {
            selectNonFixedIssuesUpdated.append(" AND i.updated_on > ?");
            params.add(project.getLastIssueUpdateForMetrics());
        }

        final List<Object[]> rawFilePairs = dao.selectNativeWithParams(selectNonFixedIssuesUpdated.toString(),
                params.toArray());

        return rawFilePairs.stream()
                .map(o -> new Issue(
                        (Integer) o[0],
                        (String) o[1],
                        (Timestamp) o[2],
                        (Timestamp) o[3]))
                .collect(Collectors.toSet());
    }

    public Set<Commit> selectNewCommits(Project project) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Project> listProjects() {
        return dao.selectAll(Project.class);
    }

}
