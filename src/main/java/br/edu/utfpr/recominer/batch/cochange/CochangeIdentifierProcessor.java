package br.edu.utfpr.recominer.batch.cochange;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.FileDao;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.Issue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class CochangeIdentifierProcessor implements ItemProcessor {

    @Inject
    @Mysql
    private EntityManagerFactory factory;

    @Inject
    private JobContext jobContext;

    @Override
    public Object processItem(Object item) throws Exception {
        final Project project = (Project) item;
        final Properties properties = new Properties();
        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, project.getProjectName());
        final GenericDao dao = new GenericDao(factory.createEntityManager(properties));
        final GenericDao genericDao = new GenericDao(factory.createEntityManager());
        final FileDao fileDao = new FileDao(genericDao, project.getProjectName(), 20);
        final CochangeIdentifier identifier = new CochangeIdentifier(fileDao);

        final List<Object[]> rawIssuesAndCommits = dao.selectNativeWithParams("SELECT issue_id, scmlog_id FROM issues_scmlog WHERE updated_on > ?", new Object[]{project.getLastIssueUpdateAnalyzedForCochange()});

        final Map<Issue, List<Commit>> issuesAndCommits = new HashMap<>();
        for (Object[] rawIssueAndCommit : rawIssuesAndCommits) {
            final Issue issue = new Issue((Integer) rawIssueAndCommit[0]);
            final Commit commit = new Commit((Integer) rawIssueAndCommit[1]);
            if (issuesAndCommits.containsKey(issue)) {
                issuesAndCommits.get(issue).add(commit);
            } else {
                final ArrayList<Commit> commitList = new ArrayList<>();
                commitList.add(commit);
                issuesAndCommits.put(issue, commitList);
            }
        }

        final Set<FilePair> allDistinctCochangeIdentified = new HashSet<>();
        final Map<Issue, Set<FilePair>> distinctCochangePerIssue = new HashMap<>();
        for (Map.Entry<Issue, List<Commit>> entry : issuesAndCommits.entrySet()) {
            final Issue issue = entry.getKey();
            final List<Commit> commits = entry.getValue();
            final List<FilePair> cochangesIdentified = identifier.identifyFor(issue, commits);

            allDistinctCochangeIdentified.addAll(cochangesIdentified);
            if (distinctCochangePerIssue.containsKey(issue)) {
                distinctCochangePerIssue.get(issue).addAll(cochangesIdentified);
            } else {
                final Set<FilePair> filePairSet = new HashSet<>();
                filePairSet.addAll(cochangesIdentified);
                distinctCochangePerIssue.put(issue, filePairSet);
            }
        }

        for (FilePair filePair : allDistinctCochangeIdentified) {
            final Map<String, Object> params = new HashMap<>();
            final String file1 = filePair.getFile1().getFileName();
            params.put("file1", file1);
            final String file2 = filePair.getFile2().getFileName();
            params.put("file2", file2);
            Integer pairFileId = dao.selectNativeOneWithParams("SELECT id FROM file_pairs WHERE (file1_path = :file1 AND file2_path = :file2) OR (file2_path = :file1 AND file1_path = :file2)",
                    params);
            if (pairFileId == null) {
                dao.executeNativeQuery("INSERT INTO file_pairs (file1_path, file2_path, file1_id, file2_id) VALUES (?, ?, ?, ?)",
                        new Object[]{file1, file2});
                pairFileId = dao.selectNativeOneWithParams("SELECT id FROM file_pairs WHERE (file1_path = :file1 AND file2_path = :file2) OR (file2_path = :file1 AND file1_path = :file2)", params);
            }

            filePair.setId(pairFileId);
        }

        for (Map.Entry<Issue, Set<FilePair>> entry : distinctCochangePerIssue.entrySet()) {
            Issue issue = entry.getKey();
            Set<FilePair> cochanges = entry.getValue();

            for (FilePair cochange : cochanges) {
                dao.executeNativeQuery("INSERT INTO file_pair_issue (file_pair_id, issue_id) VALUES (?, ?)", new Object[]{cochange.getId(), issue.getId()});
            }
        }

        return project;
    }
}
