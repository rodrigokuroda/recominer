package br.edu.utfpr.recominer.batch.associationrule;

import br.edu.utfpr.recominer.core.model.AssociationRule;
import br.edu.utfpr.recominer.core.model.CochangeApriori;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.FilePair;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.model.Transaction;
import br.edu.utfpr.recominer.core.repository.AssociationRulePredictionRepository;
import br.edu.utfpr.recominer.core.repository.CommitRepository;
import br.edu.utfpr.recominer.core.repository.FileRepository;
import br.edu.utfpr.recominer.core.repository.IssueRepository;
import br.edu.utfpr.recominer.metric.associationrule.AssociationRuleExtractor;
import br.edu.utfpr.recominer.metric.associationrule.AssociationRulePerformanceCalculator;
import br.edu.utfpr.recominer.repository.CommitMetricsRepository;
import br.edu.utfpr.recominer.repository.FileMetricsRepository;
import br.edu.utfpr.recominer.repository.IssuesMetricsRepository;
import br.edu.utfpr.recominer.repository.NetworkMetricsRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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
public class AssociationRuleProcessor implements ItemProcessor<Project, AssociationRuleLog> {

    private final Logger log = LoggerFactory.getLogger(AssociationRuleProcessor.class);

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
    private AssociationRulePredictionRepository predictionRepository;

    @Override
    public AssociationRuleLog process(Project project) throws Exception {
        commitRepository.setProject(project);
        issueRepository.setProject(project);
        fileRepository.setProject(project);
        issuesMetricsRepository.setProject(project);
        networkMetricsRepository.setProject(project);
        fileMetricsRepository.setProject(project);
        commitMetricsRepository.setProject(project);
        predictionRepository.setProject(project);
        
        AssociationRuleLog associationRuleLog = new AssociationRuleLog(project, "Zimmermann");
        associationRuleLog.start();
        
        long countFixedIssues = issueRepository.countFixedIssues();
        
        // select new commits
        final List<Commit> newCommits = commitRepository.selectNewCommits();
        for (Commit newCommit : newCommits) {
        
            log.info("Computing metrics for changed files on commit " + newCommit.getId());
            // select changed files
            final List<File> changedFiles = fileRepository.selectChangedFilesIn(newCommit);
            final List<AssociationRule<File>> predictions = new ArrayList<>();

            // TODO parameterize
            final Predicate<File> fileFilter = f -> !f.getFileName().equals("CHANGES.txt");

            for (File changedFile : changedFiles.stream().filter(fileFilter).collect(Collectors.toList())) {

                log.info("Computing association rule for file " + changedFile.getId() + " in the past.");
                // find all issues/commits where file was changed
                final List<Issue> issuesOfFile = issueRepository.selectFixedIssuesOf(changedFile, newCommit);
                final Set<Transaction<File>> transactions = new LinkedHashSet<>();

                long issuesProcessed = 0;
                for (Issue issue : issuesOfFile) {
                    log.info(++issuesProcessed + " of " + issuesOfFile.size() + " past issues processed.");

                    List<File> changedFilesInIssue = fileRepository.selectChangedFilesIn(issue);
                    final Transaction<File> transaction
                            = new Transaction<>(issue.getId().longValue(), new HashSet<>(changedFilesInIssue));
                    transactions.add(transaction);
                }

                AssociationRuleExtractor<File> extractor = new AssociationRuleExtractor<>(transactions);

                final Set<AssociationRule<File>> navigationRules = extractor.queryAssociationRules(changedFile);

                AssociationRulePerformanceCalculator<File> calculatorNavigation
                        = new AssociationRulePerformanceCalculator<>(countFixedIssues, navigationRules);

                Set<AssociationRule<File>> topAssociationRules
                        = calculatorNavigation.getTopAssociationRules(3);

                predictions.addAll(topAssociationRules);
            }
            
            predictionRepository.savePrediction(newCommit, predictions);
        }
        
        associationRuleLog.stop();

        return associationRuleLog;
    }
    
    public CochangeApriori calculeApriori(final FilePair cochange) {
        Long totalIssues = issueRepository.countFixedIssues();
        Long file1Issues = fileRepository.calculeNumberOfIssues(cochange.getFile1());
        Long file2Issues = fileRepository.calculeNumberOfIssues(cochange.getFile2());
        Long cochangeIssues = fileRepository.countFixedIssues(cochange);

        CochangeApriori apriori = new CochangeApriori(
                cochange.getFile1(), cochange.getFile2(),
                file1Issues, file2Issues,
                cochangeIssues, totalIssues);

        return apriori;
    }
}
