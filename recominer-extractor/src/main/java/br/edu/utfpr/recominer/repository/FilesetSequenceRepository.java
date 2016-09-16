package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import javax.inject.Inject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@Repository
public class FilesetSequenceRepository {

    @Inject
    private JdbcTemplate template;

    private Project project;

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getNext() {
        template.update(
                QueryUtils.getQueryForDatabase(
                        "UPDATE {0}.fileset_sequence SET id = LAST_INSERT_ID(id+1)",
                        project));
        return template.queryForObject(
                QueryUtils.getQueryForDatabase("SELECT id FROM {0}.fileset_sequence",
                        project),
                Long.class);
    }

}
