package br.edu.utfpr.recominer.metric.file;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.repository.FileRepository;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class FileMetricsCalculator {

    @Inject
    private FileRepository repository;

    @Inject
    private JdbcTemplate template;

    public FileMetrics calcule(final Project project, final File file, final Commit commit) {
        repository.setProject(project);
        Integer metricId;
        try {
            metricId = template.queryForObject(
                "SELECT id FROM " + project.getProjectName() + ".file_metrics "
                        + " WHERE file_id = ? AND commit_id = ?", Integer.class,
                        file.getId(), commit.getId());
        } catch (EmptyResultDataAccessException e) {
            metricId = null;
        }

        final FileMetrics fileMeasure = new FileMetrics(
                metricId, file, commit,
                repository.calculeCodeChurn(file, commit),
                repository.calculeCommitters(file, commit),
                repository.calculeCommits(file, commit),
                repository.calculeTotalFileAgeInDays(file, commit)
        );
        return fileMeasure;
    }

}
