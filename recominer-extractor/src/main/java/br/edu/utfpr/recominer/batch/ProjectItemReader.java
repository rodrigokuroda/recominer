package br.edu.utfpr.recominer.batch;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import br.edu.utfpr.recominer.repository.IssueTrackerRepository;
import java.util.Iterator;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

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

    @Value("${projectName}")
    private String projectName;

    private Iterator<Project> projects;

    public ProjectItemReader() {
    }

    @PostConstruct
    public void onCreate() {
        // all projects registered
        if (StringUtils.isNotBlank(projectName)) {
            projects = projectRepository.selectProjectByName(projectName).iterator();
        } else {
            projects = projectRepository.findAll().iterator();
        }
    }

    @Override
    public Project read() {
        if (!projects.hasNext()) {
            return null;
        }

        final Project project = projects.next();
        project.setIssueTracker(issueTrackerRepository.findOne(project.getIssueTracker().getId()));
        return project;
    }

}
