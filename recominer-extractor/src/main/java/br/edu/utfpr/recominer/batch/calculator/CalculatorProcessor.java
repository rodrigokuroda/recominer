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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
@StepScope
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

    @Value("#{jobParameters[filenameFilter]}")
    private String filter;

    @Value("#{jobParameters[regexFilenameFilter]}")
    private String regexFilenameFilter;

    @Value("#{jobParameters[issueKey]}")
    private String issueKey;
    
    @Value("#{jobParameters[onlyOneRandomFileFromIssue]}")
    private String onlyOneRandomFileFromIssue;
    
    @Value("#{jobParameters[trainPastVersions] ?: 1}")
    private String trainPastVersions;
    
    @Value("#{jobParameters[trainAllData] ?: false}")
    private String trainAllData;
    
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

        // select new commits
        final List<Commit> newCommits;
        if (StringUtils.isBlank(issueKey)) {
            newCommits = commitRepository.selectNewCommitsForCalculator();
            log.info("{} new commits to be processed.", newCommits.size());
        } else {
            newCommits = commitRepository.selectFirstCommitsOf(issueKey);
            log.info("Running classification for issue {}", issueKey);
        }
        final Random randomizer = new Random();

        for (Commit newCommit : newCommits) {

            log.info("Computing metrics for changed files on commit {}.", newCommit.getId());
            // select changed files
            final List<File> changedFiles;
            if (StringUtils.isBlank(onlyOneRandomFileFromIssue)) {
                changedFiles = fileRepository.selectChangedFilesIn(newCommit);
            } else {
                final List<File> files = fileRepository.selectChangedFilesIn(newCommit);
                changedFiles = new ArrayList<>(1);
                final File randomFile = files.get(randomizer.nextInt(files.size()));
                changedFiles.add(randomFile);
            }

            final Predicate<File> fileFilter = FileFilter.getFilterByRegex(regexFilenameFilter);

            for (File changedFile : changedFiles.stream()
                    .filter(fileFilter)
                    .collect(Collectors.toList())) {

                log.info("Computing metrics for file {} in the past.", changedFile.getId());

                final List<Issue> issuesOfFile;
                if (Boolean.valueOf(trainAllData)) {
                    issuesOfFile = issueRepository.selectFixedIssuesOf(changedFile, newCommit);
                    log.info("Retrieved {} issues from commit {}.", issuesOfFile.size(), newCommit.getId());
                } else {
                    issuesOfFile = issueRepository.selectFixedIssuesFromLastVersionOf(changedFile, newCommit, Integer.valueOf(trainPastVersions));
                    log.info("Retrieved {} issues from the last {} previous versions of commit {}.", issuesOfFile.size(), trainPastVersions, newCommit.getId());
                }
                
                long issuesProcessed = 0;
                for (Issue issue : issuesOfFile) {
                    log.info("{} of {} past issues processed.", ++issuesProcessed, issuesOfFile.size());

                    List<Commit> commitsOfFile = commitRepository.selectCommitsOf(issue, changedFile);
                    long commitProcessed = 0;
                    for (Commit commit : commitsOfFile) {
                        log.info("{} of {} past commits processed.", ++commitProcessed, commitsOfFile.size());
                        log.info("Computing metrics for file {} of commit {}.", changedFile.getId(), commit.getId());

                        CommitMetrics historicalCommitMetrics = commitMetricsRepository.selectMetricsOf(commit);

                        if (historicalCommitMetrics == null) {
                            log.info("Computing metrics of past commit {}.", commit.getId());
                            historicalCommitMetrics = commitMetricCalculator.calculeFor(project, commit);
                            commitMetricsRepository.save(historicalCommitMetrics);
                        } else {
                            log.info("Metrics for commit {} has already computed.", commit.getId());
                        }

                        FileMetrics historicalFileMetrics = fileMetricsRepository.selectMetricsOf(changedFile, commit);

                        if (historicalFileMetrics == null) {
                            log.info("Computing metrics for file {} in past commit {}.", changedFile.getId(), commit.getId());
                            historicalFileMetrics = fileMetricCalculator.calcule(project, changedFile, commit);
                            fileMetricsRepository.save(historicalFileMetrics);
                        } else {
                            log.info("Metrics for file {} in past commit {} has already computed.", changedFile.getId(), commit.getId());
                        }

                        log.info("Computing metrics for file {} of issue {}.", changedFile.getId(), issue.getId());
                        final IssuesMetrics issueMetrics = issueMetricCalculator.calculeIssueMetrics(project, issue, commit);
                        issuesMetricsRepository.save(issueMetrics);

                        log.info("Computing network metrics for file {} of issue {}.", changedFile.getId(), issue.getId());
                        final NetworkMetrics networkMetrics = communicationMetricProcessor.process(project, issue, commit);
                        networkMetricsRepository.save(networkMetrics);
                    }
                }

                FileMetrics fileMetrics = fileMetricsRepository.selectMetricsOf(changedFile, newCommit);

                if (fileMetrics == null) {

                    log.info("Computing metrics for file {}.", changedFile.getId());
                    fileMetrics = fileMetricCalculator.calcule(project, changedFile, newCommit);
                    fileMetricsRepository.save(fileMetrics);
                } else {
                    log.info("Metrics for file {} already computed.", changedFile.getId());
                }
            }

            log.info("Computing metrics of new commit {}.", newCommit.getId());
            CommitMetrics newCommitMetrics = commitMetricsRepository.selectMetricsOf(newCommit);

            if (newCommitMetrics == null) {
                newCommitMetrics = commitMetricCalculator.calculeFor(project, newCommit);
                commitMetricsRepository.save(newCommitMetrics);
            } else {
                log.info("Metrics for new commit {} already computed.", newCommit.getId());
            }

            // select issues associated to new commit
            final List<Issue> issues;
            if (StringUtils.isBlank(issueKey)) {
                issues = issueRepository.selectIssuesRelatedTo(newCommit);
            } else {
                issues = issueRepository.selectIssue(issueKey);
            }

            log.info("Computing metrics of issues associated with new commit {}.", newCommit.getId());
            for (Issue issue : issues) {
                log.info("Computing metrics of issue {}.", issue.getId());
                final IssuesMetrics issuesMetrics = issueMetricCalculator.calculeIssueMetrics(project, issue, newCommit);
                issuesMetricsRepository.save(issuesMetrics);

                log.info("Computing network metrics of issue {}.", issue.getId());
                final NetworkMetrics networkMetrics = communicationMetricProcessor.process(project, issue, newCommit);
                networkMetricsRepository.save(networkMetrics);
            }
        }
        calculatorLog.stop();

        return calculatorLog;
    }
}
