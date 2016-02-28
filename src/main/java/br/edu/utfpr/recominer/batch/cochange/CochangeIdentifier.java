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
import java.util.HashMap;
import java.util.HashSet;
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
    private static final Set<FilePair> EMPTY_SET = Collections.unmodifiableSet(new HashSet<>(0));

    private final Logger log = LogManager.getLogger();
    private final Statistics statistics;
    private final FileDao dao;

    public CochangeIdentifier(final FileDao dao) {
        this.statistics = new Statistics();
        this.dao = dao;
    }

    public Set<FilePair> identifyFor(final Issue issue, final List<Commit> commits, final Set<File> commitedFiles) {
        log.debug("Issue #" + issue);

        // empty
        if (commitedFiles.isEmpty()) {
            log.info("No file commited for issue #" + issue);
            statistics.getExcludedCommits().addAll(commits);
            statistics.getExcludedIssues().add(issue);
            return EMPTY_SET;
        } else if (commitedFiles.size() == 1) {
            log.info("Only one file commited in one commit for issue #" + issue);
            statistics.getExcludedCommits().addAll(commits);
            statistics.getExcludedIssues().add(issue);
            return EMPTY_SET;
        } else {
            final Map<String, Long> collected = commitedFiles.stream().collect(Collectors.groupingBy(c -> c.getFileName(), Collectors.counting()));
            if (collected.size() == 1) {
                log.info("One file only commited in many commits for issue #" + issue);
                statistics.getExcludedCommits().addAll(commits);
                statistics.getExcludedIssues().add(issue);
                return EMPTY_SET;
            }
        }
        log.debug("Number of files commited and related with issue: " + commitedFiles.size());
        //return pairFiles(pairFiles, commitedFiles, issue, statistics);
        return FilePairBuilder.pairFiles(commitedFiles);
    }

    public Set<File> filterAndAggregateAllFileOfIssue(final List<Commit> commits) {
        log.debug(commits.size() + " commits references the issue");
        final Set<String> allFiles = statistics.getAllFiles();
        final Set<String> allTestJavaFiles = statistics.getAllTestJavaFiles();
        final Set<String> allFilteredFiles = statistics.getAllFilteredFiles();
        final Set<String> allJavaFiles = statistics.getAllJavaFiles();
        final Set<String> allXmlFiles = statistics.getAllXmlFiles();

        // monta os pares com os arquivos de todos os commits da issue
        final Map<File, File> commitedFiles = new HashMap<>();
        for (Commit commit : commits) {
            // select name of commited files
            final List<File> files = dao.selectCommitFiles(commit);
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
                    if (commitedFiles.containsKey(file)) {
                        commitedFiles.get(file).addCommit(commit);
                    } else {
                        commitedFiles.put(file, file);
                    }
                }
            }
        }
        return commitedFiles.keySet();
    }
}
