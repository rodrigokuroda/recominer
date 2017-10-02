package br.edu.utfpr.recominer.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import br.edu.utfpr.recominer.core.model.IssueTracker;
import br.edu.utfpr.recominer.core.model.IssueTrackerSystem;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.model.VersionControl;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import br.edu.utfpr.recominer.repository.IssueTrackerRepository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
@StepScope
public class ProjectItemReader implements ItemReader<Project> {

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private IssueTrackerRepository issueTrackerRepository;

    @Inject
    private JdbcTemplate template;

    @Value("${projectName:}")
    private String projectName;
    
    @Value("${versionControlUrl}")
    private String versionControlUrl;
    
    @Value("${repositoryPath}")
    private String repositoryPath;
    
    @Value("${issueTrackerUrl}")
    private String issueTrackerUrl;
    
    @Value("${issueTrackerSystem}")
    private String issueTrackerSystem;
    
    @Value("${issueTrackerExtractionDelay:15}")
    private Integer issueTrackerExtractionDelay;

    private Iterator<Project> projects;

    public ProjectItemReader() {
    }

    @PostConstruct
    public void onCreate() {
    	if (StringUtils.isNotBlank(projectName)
    			&& StringUtils.isNotBlank(issueTrackerSystem)
    			&& StringUtils.isNotBlank(issueTrackerUrl)
    			&& StringUtils.isNotBlank(versionControlUrl)) {
    		List<Project> selectProjectByName = projectRepository.selectProjectByName(projectName);
    		if (!selectProjectByName.isEmpty()) {
    			throw new RecominerRuntimeException(selectProjectByName.get(0) + " already registered in database. Please, use only --projectName=PROJECT parameter to use registered project or use another name.");
    		}
        	List<Project> parameterProjects = new ArrayList<>();
        	Project project = new Project();
        	project.setProjectName(projectName);
        	
        	IssueTracker issueTracker = new IssueTracker();
        	issueTracker.setSystem(IssueTrackerSystem.valueOf(issueTrackerSystem));
        	issueTracker.setExtractionDelay(issueTrackerExtractionDelay);
			IssueTracker savedIssueTracker = issueTrackerRepository.save(issueTracker);
			
			project.setIssueTracker(savedIssueTracker);
        	project.setIssueTrackerUrl(issueTrackerUrl);
        	project.setVersionControlUrl(versionControlUrl);
        	project.setRepositoryPath(repositoryPath);
			parameterProjects.add(project);
			projectRepository.save(project);
			projects = parameterProjects.iterator();
    	} else {
	        if (StringUtils.isNotBlank(projectName)) {
	        	// specific project
	            projects = projectRepository.selectProjectByName(projectName).iterator();
	        } else {
	        	// all projects registered
	            projects = projectRepository.findAll().iterator();
	        }
    	}
    }

    @Override
    public Project read() {
        if (!projects.hasNext()) {
            return null;
        }
        
        final Project project = projects.next();
        Date lastCommitDateAnalyzed;
        try {
            lastCommitDateAnalyzed = template.queryForObject(
                QueryUtils.getQueryForDatabase(
                        "SELECT MAX(date) FROM {0}_vcs.scmlog WHERE id IN (SELECT scmlog_id FROM {0}.issues_scmlog)",
                        project), Date.class);
        } catch (DataAccessException e) {
            lastCommitDateAnalyzed = null;
        }
        project.setLastCommitDateAnalyzed(lastCommitDateAnalyzed);

        project.setIssueTracker(issueTrackerRepository.findOne(project.getIssueTracker().getId()));
        return project;
    }

}
