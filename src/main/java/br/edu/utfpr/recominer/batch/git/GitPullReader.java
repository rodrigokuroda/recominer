package br.edu.utfpr.recominer.batch.git;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.batch.api.chunk.AbstractItemReader;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class GitPullReader extends AbstractItemReader {

    @Inject
    private GenericBichoDAO dao;

    private Iterator<Project> iterator;

    @PostConstruct
    public void loadProjects() {
        // reads all VCS' projects available (database schemas)
        final List<Project> projects = dao.selectAll(Project.class);
        iterator = projects.listIterator();
    }

    @Override
    public Project readItem() throws Exception {
        if (!iterator.hasNext()) {
            return null;
        }

        final Project project = iterator.next();
        return project;
    }

}
