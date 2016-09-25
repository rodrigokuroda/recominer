package br.edu.utfpr.recominer.core.repository;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Repository
public class FilesetSequenceRepository {

    @Autowired
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
