package br.edu.utfpr.recominer.batch.git;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.GenericDao;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
@Dependent
public class GitPullReader extends AbstractItemReader {

    @PersistenceContext(unitName = "postgresql")
    private EntityManager em;
    
    @Inject
    private JobContext jobContext;

    private Iterator<Project> iterator;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        final GenericDao dao = new GenericDao(em);
        
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
