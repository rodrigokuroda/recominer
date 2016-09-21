package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.CommitRepository;
import br.edu.utfpr.recominer.core.repository.FileRepository;
import br.edu.utfpr.recominer.core.repository.IssueRepository;
import br.edu.utfpr.recominer.filter.FileFilter;
import br.edu.utfpr.recominer.metric.file.FileMetrics;
import br.edu.utfpr.recominer.metric.file.FileMetricsCalculator;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import br.edu.utfpr.recominer.model.CommitMetrics;
import br.edu.utfpr.recominer.model.IssuesMetrics;
import br.edu.utfpr.recominer.repository.CommitMetricsRepository;
import br.edu.utfpr.recominer.repository.FileMetricsRepository;
import br.edu.utfpr.recominer.repository.IssuesMetricsRepository;
import br.edu.utfpr.recominer.repository.NetworkMetricsRepository;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
public class CalculatorProcessor implements ItemProcessor<Project, CalculatorLog> {

    private final Logger log = LoggerFactory.getLogger(CalculatorProcessor.class);

    @Inject
    private CommitRepository commitRepository;

    @Inject
    private IssueRepository issueRepository;

    @Inject
    private FileRepository fileRepository;

    @Inject
    private CommunicationMetricProcessor communicationMetricProcessor;

    @Inject
    private CommitMetricsCalculator commitMetricCalculator;

    @Inject
    private IssuesMetricsRepository issuesMetricsRepository;

    @Inject
    private IssueMetricCalculator issueMetricCalculator;

    @Inject
    private NetworkMetricsRepository networkMetricsRepository;

    @Inject
    private FileMetricsRepository fileMetricsRepository;

    @Inject
    private FileMetricsCalculator fileMetricCalculator;

    @Inject
    private CommitMetricsRepository commitMetricsRepository;

    @Value("${filenameFilter:}")
    private String filter;

    @Override
    public CalculatorLog process(Project project) throws Exception {
        CalculatorLog calculatorLog = new CalculatorLog(project, "AllMetrics");
        calculatorLog.start();

        commitRepository.setProject(project);
        issueRepository.setProject(project);
        fileRepository.setProject(project);
        issuesMetricsRepository.setProject(project);
        networkMetricsRepository.setProject(project);
        fileMetricsRepository.setProject(project);
        commitMetricsRepository.setProject(project);

        final Date lastCommitDate = commitRepository.selectLastNewCommitDateForCalculator();
        calculatorLog.setLastCommitDate(lastCommitDate);

        // select new commits
        final List<Commit> newCommits = commitRepository.selectNewCommitsForCalculator();
        log.info(newCommits.size() + " new commits to be processed.");
        
        for (Commit newCommit : newCommits) {

            log.info("Computing metrics for changed files on commit " + newCommit.getId());
            // select changed files
            final List<File> changedFiles = fileRepository.selectChangedFilesIn(newCommit);

            final Predicate<File> fileFilter = FileFilter.getFilterByFilename(filter);

            for (File changedFile : changedFiles.stream()
                    .filter(fileFilter)
                    .collect(Collectors.toList())) {

                log.info("Computing metrics for file " + changedFile.getId() + " in the past.");
                // find all issues/commits where file was changed
                List<Issue> issuesOfFile = issueRepository.selectIssuesOf(changedFile);

                long issuesProcessed = 0;
                for (Issue issue : issuesOfFile) {
                    log.info(++issuesProcessed + " of " + issuesOfFile.size() + " past issues processed.");

                    List<Commit> commitsOfFile = commitRepository.selectCommitsOf(issue, changedFile);
                    long commitProcessed = 0;
                    for (Commit commit : commitsOfFile) {
                        log.info(++commitProcessed + " of " + commitsOfFile.size() + " past commits processed.");
                        log.info("Computing metrics for file " + changedFile.getId() + " of commit " + commit.getId());

                        CommitMetrics historicalCommitMetrics = commitMetricsRepository.selectMetricsOf(commit);

                        if (historicalCommitMetrics == null) {
                            log.info("Computing metrics of past commit " + commit.getId());
                            historicalCommitMetrics = commitMetricCalculator.calculeFor(project, commit);
                            commitMetricsRepository.save(historicalCommitMetrics);
                        } else {
                            log.info("Metrics for commit " + commit.getId() + " has already computed.");
                        }

                        FileMetrics historicalFileMetrics = fileMetricsRepository.selectMetricsOf(changedFile, commit);

                        if (historicalFileMetrics == null) {
                            log.info("Computing metrics for file " + changedFile.getId() + " in past commit " + commit.getId());
                            historicalFileMetrics = fileMetricCalculator.calcule(project, changedFile, commit);
                            fileMetricsRepository.save(historicalFileMetrics);
                        } else {
                            log.info("Metrics for file " + changedFile.getId() + " in past commit " + commit.getId() + " has already computed.");
                        }

                        log.info("Computing metrics for file " + changedFile.getId() + " of issue " + issue.getId());
                        final IssuesMetrics issueMetrics = issueMetricCalculator.calculeIssueMetrics(project, issue, commit);
                        issuesMetricsRepository.save(issueMetrics);

                        log.info("Computing network metrics for file " + changedFile.getId() + " of issue " + issue.getId());
                        final NetworkMetrics networkMetrics = communicationMetricProcessor.process(project, issue, commit);
                        networkMetricsRepository.save(networkMetrics);
                    }
                }

                FileMetrics fileMetrics = fileMetricsRepository.selectMetricsOf(changedFile, newCommit);

                if (fileMetrics == null) {

                    log.info("Computing metrics for file " + changedFile.getId());
                    fileMetrics = fileMetricCalculator.calcule(project, changedFile, newCommit);
                    fileMetricsRepository.save(fileMetrics);
                } else {
                    log.info("Metrics for file " + changedFile.getId() + " already computed.");
                }
            }

            log.info("Computing metrics of new commit " + newCommit.getId());
            CommitMetrics newCommitMetrics = commitMetricsRepository.selectMetricsOf(newCommit);

            if (newCommitMetrics == null) {
                newCommitMetrics = commitMetricCalculator.calculeFor(project, newCommit);
                commitMetricsRepository.save(newCommitMetrics);
            } else {
                log.info("Metrics for new commit " + newCommit.getId() + " already computed.");
            }

            // select issues associated to new commit
            final List<Issue> issues = issueRepository.selectIssuesRelatedTo(newCommit);

            log.info("Computing metrics of issues associated with new commit " + newCommit.getId());
            for (Issue issue : issues) {
                log.info("Computing metrics of issue " + issue.getId());
                final IssuesMetrics issuesMetrics = issueMetricCalculator.calculeIssueMetrics(project, issue, newCommit);
                issuesMetricsRepository.save(issuesMetrics);

                log.info("Computing network metrics of issue " + issue.getId());
                final NetworkMetrics networkMetrics = communicationMetricProcessor.process(project, issue, newCommit);
                networkMetricsRepository.save(networkMetrics);
            }
        }
        calculatorLog.stop();

        return calculatorLog;
    }
}
