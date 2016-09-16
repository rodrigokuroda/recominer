package br.edu.utfpr.recominer.batch.dataset;

import br.edu.utfpr.recominer.core.model.Cochange;
import br.edu.utfpr.recominer.metric.file.FileMetrics;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.model.CommitMetrics;
import br.edu.utfpr.recominer.model.ContextualMetrics;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.model.IssuesMetrics;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.repository.CommitMetricsRepository;
import br.edu.utfpr.recominer.core.repository.CommitRepository;
import br.edu.utfpr.recominer.repository.FileMetricsRepository;
import br.edu.utfpr.recominer.repository.FilePairIssueCommitRepository;
import br.edu.utfpr.recominer.core.repository.FileRepository;
import br.edu.utfpr.recominer.core.repository.IssueRepository;
import br.edu.utfpr.recominer.repository.IssuesMetricsRepository;
import br.edu.utfpr.recominer.repository.NetworkMetricsRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
public class DatasetProcessor implements ItemProcessor<Project, DatasetLog> {

    private final Logger log = LoggerFactory.getLogger(DatasetProcessor.class);

    @Inject
    private CommitRepository commitRepository;

    @Inject
    private IssueRepository issueRepository;

    @Inject
    private FileRepository fileRepository;

    @Inject
    private IssuesMetricsRepository issuesMetricsRepository;

    @Inject
    private NetworkMetricsRepository networkMetricsRepository;

    @Inject
    private FileMetricsRepository fileMetricsRepository;

    @Inject
    private CommitMetricsRepository commitMetricsRepository;
    
    @Inject
    private FilePairIssueCommitRepository filePairIssueCommitRepository;
    
    @Inject
    private DatasetOutput datasetOutput;

    @Override
    public DatasetLog process(Project project) throws Exception {
        DatasetLog datasetLog = new DatasetLog(project, "All");
        datasetLog.start();

        commitRepository.setProject(project);
        issueRepository.setProject(project);
        fileRepository.setProject(project);
        issuesMetricsRepository.setProject(project);
        networkMetricsRepository.setProject(project);
        fileMetricsRepository.setProject(project);
        commitMetricsRepository.setProject(project);
        filePairIssueCommitRepository.setProject(project);

        // select new commits
        final List<Commit> newCommits = commitRepository.selectNewCommits();
        for (Commit newCommit : newCommits) {

            log.info("Computing metrics of new commit " + newCommit.getId());
            CommitMetrics newCommitMetrics = commitMetricsRepository.selectMetricsOf(newCommit);

            // select issues associated to new commit
            final List<Issue> issues = issueRepository.selectIssuesRelatedTo(newCommit);

            Map<IssueCommit, IssuesMetrics> issuesMetricsCache = new HashMap<>();
            Map<IssueCommit, NetworkMetrics> networkMetricsCache = new HashMap<>();

            log.info("Getting metrics of issues associated with new commit " + newCommit.getId());
            for (Issue issue : issues) {
                log.info("Getting metrics of issue " + issue.getId());
                final IssuesMetrics issuesMetrics = issuesMetricsRepository.selectMetricsOf(issue, newCommit);
                issuesMetricsCache.put(new IssueCommit(issue, newCommit), issuesMetrics);

                log.info("Getting network metrics of issue " + issue.getId());
                final NetworkMetrics networkMetrics = networkMetricsRepository.selectMetricsOf(issue, newCommit);
                networkMetricsCache.put(new IssueCommit(issue, newCommit), networkMetrics);
            }

            log.info("Getting changed files on commit " + newCommit.getId());
            // select changed files
            final List<File> changedFiles = fileRepository.selectChangedFilesIn(newCommit);

            final Predicate<File> fileFilter = f -> !f.getFileName().equals("CHANGES.txt");

            for (File changedFile : changedFiles.stream().filter(fileFilter).collect(Collectors.toList())) {

                FileMetrics fileMetrics = fileMetricsRepository.selectMetricsOf(changedFile, newCommit);

                List<ContextualMetrics> train = new ArrayList<>();
                List<ContextualMetrics> test = new ArrayList<>();

                // creates test dataset 
                for (IssueCommit issue : issuesMetricsCache.keySet()) {

                    ContextualMetrics contextualMetrics
                            = new ContextualMetrics(
                                    issuesMetricsCache.get(issue),
                                    networkMetricsCache.get(issue),
                                    newCommitMetrics, fileMetrics);
                    test.add(contextualMetrics);
                }

                // stores all cochanged occurred with changed file in the new issue
                Set<Cochange> cochanges = new HashSet<>();

                log.info("Getting issues where file " + changedFile.getId() + " was changed.");
                // find all issues/commits where file was changed
                List<Issue> issuesOfFile = issueRepository.selectIssuesOf(changedFile);

                long issuesProcessed = 0;
                for (Issue issue : issuesOfFile) {

                    log.info(++issuesProcessed + " of " + issuesOfFile.size() + " past issues processed.");

                    log.info("Getting commits in issue " + issue.getId() + " where file " + changedFile.getId() + " was changed.");
                    List<Commit> commitsOfFile = commitRepository.selectCommitsOf(issue, changedFile);

                    long commitProcessed = 0;
                    for (Commit commit : commitsOfFile) {
                        log.info(++commitProcessed + " of " + commitsOfFile.size() + " past commits processed.");
                        log.info("Getting metrics for file " + changedFile.getId() + " of commit " + commit.getId());
                        
                        final List<Cochange> cochangedFilesInCommit = fileRepository.selectCochangedFilesIn(commit, changedFile);
                        cochanges.addAll(cochangedFilesInCommit);
                        
                        filePairIssueCommitRepository.save(changedFile, cochangedFilesInCommit, issue, commit);

                        CommitMetrics historicalCommitMetrics = commitMetricsRepository.selectMetricsOf(commit);

                        FileMetrics historicalFileMetrics = fileMetricsRepository.selectMetricsOf(changedFile, commit);

                        log.info("Getting metrics for file " + changedFile.getId() + " of issue " + issue.getId());
                        IssuesMetrics issuesMetrics = issuesMetricsRepository.selectMetricsOf(issue, commit);

                        log.info("Getting network metrics for file " + changedFile.getId() + " of issue " + issue.getId());
                        NetworkMetrics networkMetrics = networkMetricsRepository.selectMetricsOf(issue, commit);

                        // creates a dataset's instance (one line)
                        ContextualMetrics contextualMetrics = new ContextualMetrics(issuesMetrics,
                                networkMetrics, historicalCommitMetrics, historicalFileMetrics);
                        // collecting instances that will compose the train dataset
                        train.add(contextualMetrics);
                    }
                }

                // group commits by cochange in order to set 0 or 1 for classification
                Map<File, Set<Commit>> cochangesCommits = cochanges.stream()
                        .collect(Collectors.groupingBy(
                                Cochange::getFile,
                                Collectors.mapping(
                                        Cochange::getCommit,
                                        Collectors.toSet())));

                // creates a dataset for each cochange
                for (Map.Entry<File, Set<Commit>> cochangeCommits : cochangesCommits.entrySet()) {
                    File cochange = cochangeCommits.getKey();
                    Set<Commit> commits = cochangeCommits.getValue();

                    if (commits.isEmpty()) {
                        continue;
                    }

                    assert (commits != null);
                    assert (!commits.isEmpty());

                    final Dataset trainDataset = new Dataset(changedFile, cochange, train, commits);
                    
                    datasetOutput.write(project, newCommit, trainDataset, "train");
                }

                final Dataset testDataset = new Dataset(changedFile, test, newCommit);
                
                datasetOutput.write(project, newCommit, testDataset, "test");
            }
        }

        datasetLog.stop();

        return datasetLog;
    }
}
