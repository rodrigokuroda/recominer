package br.edu.utfpr.recominer.batch.cochange;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.FileDao;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.dao.QueryUtils;
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
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
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
        final String projectName = project.getProjectName();

        final List<Object[]> rawIssuesAndCommits;
        if (project.getLastIssueUpdateAnalyzedForCochange() != null) {
            rawIssuesAndCommits = dao.selectNativeWithParams(
                    QueryUtils.getQueryForDatabase("SELECT issue_id, scmlog_id FROM {0}_issues.issues_scmlog i2s JOIN {0}_issues.issues i ON i.id = i2s.issue_id WHERE updated_on > ? ORDER BY updated_on ASC", projectName), 
                    new Object[]{project.getLastIssueUpdateAnalyzedForCochange()});
        } else {
            rawIssuesAndCommits = dao.selectNativeWithParams(
                    QueryUtils.getQueryForDatabase("SELECT issue_id, scmlog_id FROM {0}_issues.issues_scmlog i2s JOIN {0}_issues.issues i ON i.id = i2s.issue_id ORDER BY updated_on ASC", projectName), 
                    new Object[0]);
        }
        
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
            final Set<FilePair> cochangesIdentified = identifier.identifyFor(issue, commits);

            allDistinctCochangeIdentified.addAll(cochangesIdentified);
            if (distinctCochangePerIssue.containsKey(issue)) {
                distinctCochangePerIssue.get(issue).addAll(cochangesIdentified);
            } else {
                final Set<FilePair> filePairSet = new HashSet<>();
                filePairSet.addAll(cochangesIdentified);
                distinctCochangePerIssue.put(issue, filePairSet);
            }
        }

        final String selectCochangeId = QueryUtils.getQueryForDatabase(
                "SELECT id FROM {0}.file_pairs "
                + "WHERE (file1_path = ? AND file2_path = ?) "
                + "OR (file2_path = ? AND file1_path = ?)", projectName);
        final String insertCochange = QueryUtils.getQueryForDatabase(
                "INSERT INTO {0}.file_pairs (file1_path, file2_path, file1_id, file2_id) "
                        + "VALUES (?, ?, ?, ?)", projectName);
        final Map<FilePair, FilePair> cochangesWithId = new HashMap<>();
        for (FilePair filePair : allDistinctCochangeIdentified) {
            final String file1 = filePair.getFile1().getFileName();
            final String file2 = filePair.getFile2().getFileName();
            final Object[] paramsForSelectCochangeId = new Object[]{file1, file2, file1, file2};
            
            Integer pairFileId = dao.selectNativeOneWithParams(selectCochangeId, paramsForSelectCochangeId);
            if (pairFileId == null) {
                dao.executeNativeQuery(insertCochange, new Object[]{file1, file2, filePair.getFile1().getId(), filePair.getFile2().getId()});
                pairFileId = dao.selectNativeOneWithParams(selectCochangeId, paramsForSelectCochangeId);
            }

            filePair.setId(pairFileId);
            cochangesWithId.put(filePair, filePair);
        }
        
        final String insertCochangeRelatedToIssue = 
                QueryUtils.getQueryForDatabase("INSERT INTO {0}.file_pair_issue (file_pair_id, issue_id) VALUES (?, ?)", projectName);
        
        final String insertCochangeRelatedToIssueAndCommit = 
                QueryUtils.getQueryForDatabase("INSERT INTO {0}.file_pair_issue_commit (file_pair_id, issue_id, commit_id) VALUES (?, ?, ?)", projectName);
        
        for (Map.Entry<Issue, Set<FilePair>> entry : distinctCochangePerIssue.entrySet()) {
            Issue issue = entry.getKey();
            Set<FilePair> cochanges = entry.getValue();

            for (FilePair cochange : cochanges) {
                dao.executeNativeQuery(insertCochangeRelatedToIssue, new Object[]{cochangesWithId.get(cochange).getId(), issue.getId()});
                for (Commit commit : cochange.getCommits()) {
                    dao.executeNativeQuery(insertCochangeRelatedToIssueAndCommit, new Object[]{cochangesWithId.get(cochange).getId(), issue.getId(), commit.getId()});
                }
            }
        }
        
        if (!rawIssuesAndCommits.isEmpty()) { 
            final String selectLastIssueUpdateDate = QueryUtils.getQueryForDatabase("SELECT MAX(updated_on) FROM {0}_issues.issues WHERE id = ?", projectName);
            final Integer lastIssueUpdatedAnalyzed = (Integer) rawIssuesAndCommits.get(rawIssuesAndCommits.size() -1)[0];
            java.sql.Timestamp lastIssueUpdate = (java.sql.Timestamp) dao.selectNativeOneWithParams(selectLastIssueUpdateDate, new Object[]{lastIssueUpdatedAnalyzed});
            // setting date of last commit analyzed
            project.setLastIssueUpdateAnalyzedForCochange(lastIssueUpdate);
        }
        return project;
    }
}
