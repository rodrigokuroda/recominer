package br.edu.utfpr.recominer.batch.cochange;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.FileDao;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.dao.QueryUtils;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.Issue;
import java.util.ArrayList;
import java.util.Date;
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

        // Select all issues' (previously cleaned) commits that should have been
        // commited between issue submit date and issue fix date.
        String selectIssuesAndCommits
                = "SELECT issue_id, scmlog_id FROM {0}.issues_scmlog i2s "
                + "  JOIN {0}_issues.issues i ON i.id = i2s.issue_id "
                + "  JOIN {0}_vcs.scmlog s ON s.id = i2s.scmlog_id "
                + " WHERE s.date >= i.submitted_on"
                ;
        
        boolean fixedIssueOnly = false;
        if (fixedIssueOnly) {
            selectIssuesAndCommits +=
                  "   AND i.fixed_on IS NOT NULL "
                + "   AND s.date <= i.fixed_on";
            
        } else {
            selectIssuesAndCommits +=
                  "   AND (i.fixed_on IS NULL OR s.date <= i.fixed_on) ";
        }

        final List<Object[]> rawIssuesAndCommits;
        if (project.getLastIssueUpdateAnalyzedForCochange() != null) {
            rawIssuesAndCommits = dao.selectNativeWithParams(
                    QueryUtils.getQueryForDatabase(selectIssuesAndCommits + " AND updated_on > ? ORDER BY updated_on ASC", projectName),
                    new Object[]{project.getLastIssueUpdateAnalyzedForCochange()});
        } else {
            rawIssuesAndCommits = dao.selectNativeWithParams(
                    QueryUtils.getQueryForDatabase(selectIssuesAndCommits + " ORDER BY updated_on ASC", projectName),
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

        final Set<File> allDistinctFiles = new HashSet<>();
        final Set<FilePair> allDistinctCochangeIdentified = new HashSet<>();
        final Map<Issue, Set<FilePair>> distinctCochangePerIssue = new HashMap<>();
        for (Map.Entry<Issue, List<Commit>> entry : issuesAndCommits.entrySet()) {
            final Issue issue = entry.getKey();
            final List<Commit> commits = entry.getValue();
            final Set<File> commitedFiles = identifier.filterAndAggregateAllFileOfIssue(commits);
            allDistinctFiles.addAll(commitedFiles);
            final Set<FilePair> cochangesIdentified = identifier.identifyFor(issue, commits, commitedFiles);

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
                + "WHERE (file1_id = ? AND file2_id = ?) "
                + "OR (file2_id = ? AND file1_id = ?)", projectName);
        final String insertCochange = QueryUtils.getQueryForDatabase(
                "INSERT INTO {0}.file_pairs (file1_id, file2_id, updated_on) "
                        + "VALUES (?, ?, ?)", projectName);
        
        final String updateCochange = QueryUtils.getQueryForDatabase(
                "UPDATE {0}.file_pairs SET updated_on = ? "
                        + " WHERE id = ?", projectName);
        final Date now = new Date();
        final Map<FilePair, FilePair> cochangesWithId = new HashMap<>();
        for (FilePair filePair : allDistinctCochangeIdentified) {
            final Integer file1 = filePair.getFile1().getId();
            final Integer file2 = filePair.getFile2().getId();
            final Object[] paramsForSelectCochangeId = new Object[]{file1, file2, file1, file2};
            
            Integer pairFileId = dao.selectNativeOneWithParams(selectCochangeId, paramsForSelectCochangeId);
            if (pairFileId == null) {
                dao.executeNativeQuery(insertCochange, new Object[]{file1, file2, now});
                pairFileId = dao.selectNativeOneWithParams(selectCochangeId, paramsForSelectCochangeId);
            } else {
                dao.executeNativeQuery(updateCochange, new Object[]{now, pairFileId});
            }

            filePair.setId(pairFileId);
            cochangesWithId.put(filePair, filePair);
        }
        
        final String insertCochangeRelatedToIssue = 
                QueryUtils.getQueryForDatabase("INSERT INTO {0}.file_pair_issue (file_pair_id, issue_id) VALUES (?, ?)", projectName);
        
        final String insertCochangeRelatedToIssueAndCommit = 
                QueryUtils.getQueryForDatabase("INSERT INTO {0}.file_pair_issue_commit (file_pair_id, issue_id, commit_id) VALUES (?, ?, ?)", projectName);
        
        for (Map.Entry<Issue, Set<FilePair>> entry : distinctCochangePerIssue.entrySet()) {
            final Issue issue = entry.getKey();
            final Set<FilePair> cochanges = entry.getValue();

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
