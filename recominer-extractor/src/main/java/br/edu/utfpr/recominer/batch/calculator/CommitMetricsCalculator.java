package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.model.CommitMetrics;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.repository.CommitRepository;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@Named
public class CommitMetricsCalculator {
    
    @Inject
    private CommitRepository commitRepository;
    
    @Inject
    private JdbcTemplate template;

    public CommitMetricsCalculator() {
    }
    
    public CommitMetrics calculeFor(Project project, Commit commit) {
        commitRepository.setProject(project);
        
        Integer metricId;
        try {
            metricId = template.queryForObject(
                "SELECT id FROM " + project.getProjectName() + ".commit_metrics "
                        + " WHERE commit_id = ?", Integer.class,
                        commit.getId());
        } catch (EmptyResultDataAccessException e) {
            metricId = null;
        }
        
        Commit metric = commitRepository.selectWithCommitter(commit);
        return new CommitMetrics(metricId, metric);
    }
}
