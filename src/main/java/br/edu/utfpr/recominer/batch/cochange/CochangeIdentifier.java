package br.edu.utfpr.recominer.batch.cochange;

import br.edu.utfpr.recominer.dao.FileDao;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.Issue;
import br.edu.utfpr.recominer.services.executor.FilePairBuilder;
import br.edu.utfpr.recominer.services.matrix.Statistics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class CochangeIdentifier {

    private static final List<FilePair> EMPTY_ARRAY_LIST = Collections.unmodifiableList(new ArrayList<>(0));

    private final Logger log = LogManager.getLogger();
    private final Statistics statistics;
    private final FileDao dao;

    public CochangeIdentifier(final FileDao dao) {
        this.statistics = new Statistics();
        this.dao = dao;
    }

    public List<FilePair> identifyFor(final Issue issue, final List<Commit> commits) {
        log.debug("Issue #" + issue);
        //log.debug(count++ + " of the " + issuesConsideredCommits.size());
        log.debug(commits.size() + " commits references the issue");
        final List<File> commitedFiles = filterAndAggregateAllFileOfIssue(commits, statistics);

        // empty
        if (commitedFiles.isEmpty()) {
            log.info("No file commited for issue #" + issue);
            statistics.getExcludedCommits().addAll(commits);
            statistics.getExcludedIssues().add(issue);
            return EMPTY_ARRAY_LIST;
        } else if (commitedFiles.size() == 1) {
            log.info("Only one file commited in one commit for issue #" + issue);
            statistics.getExcludedCommits().addAll(commits);
            statistics.getExcludedIssues().add(issue);
            return EMPTY_ARRAY_LIST;
        } else {
            final Map<String, Long> collected = commitedFiles.stream().collect(Collectors.groupingBy(c -> c.getFileName(), Collectors.counting()));
            if (collected.size() == 1) {
                log.info("One file only commited in many commits for issue #" + issue);
                statistics.getExcludedCommits().addAll(commits);
                statistics.getExcludedIssues().add(issue);
                return EMPTY_ARRAY_LIST;
            }
        }
        log.debug("Number of files commited and related with issue: " + commitedFiles.size());
        //return pairFiles(pairFiles, commitedFiles, issue, statistics);
        return FilePairBuilder.pairFiles(commitedFiles);
    }

    protected List<File> filterAndAggregateAllFileOfIssue(List<Commit> commits, Statistics statistics) {
        final Set<String> allFiles = statistics.getAllFiles();
        final Set<String> allTestJavaFiles = statistics.getAllTestJavaFiles();
        final Set<String> allFilteredFiles = statistics.getAllFilteredFiles();
        final Set<String> allJavaFiles = statistics.getAllJavaFiles();
        final Set<String> allXmlFiles = statistics.getAllXmlFiles();

        // monta os pares com os arquivos de todos os commits da issue
        final List<File> commitedFiles = new ArrayList<>();
        for (Commit commit : commits) {
            // select name of commited files
            final List<File> files = dao.selectCommitFiles(commit.getId());
            log.info(files.size() + " files in commit #" + commit.getId());
            for (File file : files) {
                allFiles.add(file.getFileName());
                if (file.getFileName().endsWith("Test.java") || file.getFileName().toLowerCase().endsWith("_test.java")) {
                    allTestJavaFiles.add(file.getFileName());
                    allFilteredFiles.add(file.getFileName());
                } else if (!file.getFileName().endsWith(".java") && !file.getFileName().endsWith(".xml")) {
                    allFilteredFiles.add(file.getFileName());
                } else {
                    if (file.getFileName().endsWith(".java")) {
                        allJavaFiles.add(file.getFileName());
                    } else if (file.getFileName().endsWith(".xml")) {
                        allXmlFiles.add(file.getFileName());
                    }
                    commitedFiles.add(file);
                }
            }
        }
        return commitedFiles;
    }
}
