package br.edu.utfpr.recominer.dao;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.batch.bicho.IssueTracker;
import java.util.ArrayList;
import java.util.List;

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
        
        final List<Object[]> its = 
                dao.selectNativeWithParams(query, issueTracker.getId());
        
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
                project.getLastAprioriUpdate()
        );
    }

}
