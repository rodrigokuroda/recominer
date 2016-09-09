package br.edu.utfpr.recominer.batch.associationrule;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.core.model.Issue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class AssociationRuleStatistics {
    private final Set<String> allFiles = new HashSet<>();
    private final Set<String> allTestJavaFiles = new HashSet<>();
    private final Set<String> allJavaFiles = new HashSet<>();
    private final Set<String> allXmlFiles = new HashSet<>();
    private final Set<String> allFilteredFiles = new HashSet<>();
    private final Set<Commit> allCommits = new HashSet<>();
    private final Set<Issue> allIssues = new HashSet<>();
    private final Set<Integer> allDefectIssues = new HashSet<>();
    private final Set<Issue> allConsideredIssues = new HashSet<>();
    private final Set<Integer> allConsideredCommits = new HashSet<>();
    private final Set<String> allConsideredFiles = new HashSet<>();

    private final Map<String, Set<Issue>> filesIssues = new HashMap<>();
    private final Map<String, Set<Commit>> filesCommits = new HashMap<>();
    private final Set<Issue> excludedIssues = new HashSet<>();
    private final Set<Commit> excludedCommits = new HashSet<>();
    private final Set<FilePair> pairFiles = new HashSet<>();

    public void addFileIssue(File commitedFile, Issue issue) {
        if (getFilesIssues().containsKey(commitedFile.getFileName())) {
            getFilesIssues().get(commitedFile.getFileName()).add(issue);
        } else {
            Set<Issue> issues = new HashSet<>();
            issues.add(issue);
            getFilesIssues().put(commitedFile.getFileName(), issues);
        }
    }

    public void addFileCommit(File commitedFile, Commit commit) {
        if (getFilesCommits().containsKey(commitedFile.getFileName())) {
            getFilesCommits().get(commitedFile.getFileName()).add(commit);
        } else {
            Set<Commit> commits = new HashSet<>();
            commits.add(commit);
            getFilesCommits().put(commitedFile.getFileName(), commits);
        }
    }

    public Set<String> getAllFiles() {
        return allFiles;
    }

    public Set<String> getAllTestJavaFiles() {
        return allTestJavaFiles;
    }

    public Set<String> getAllJavaFiles() {
        return allJavaFiles;
    }

    public Set<String> getAllXmlFiles() {
        return allXmlFiles;
    }

    public Set<String> getAllFilteredFiles() {
        return allFilteredFiles;
    }

    public Set<Commit> getAllCommits() {
        return allCommits;
    }

    public Set<Issue> getAllIssues() {
        return allIssues;
    }

    public Set<Issue> getAllConsideredIssues() {
        return allConsideredIssues;
    }

    public Set<Integer> getAllConsideredCommits() {
        return allConsideredCommits;
    }

    public Set<Integer> getAllDefectIssues() {
        return allDefectIssues;
    }

    public Map<String, Set<Issue>> getFilesIssues() {
        return filesIssues;
    }

    public Map<String, Set<Commit>> getFilesCommits() {
        return filesCommits;
    }

    public Set<Issue> getExcludedIssues() {
        return excludedIssues;
    }

    public Set<Commit> getExcludedCommits() {
        return excludedCommits;
    }

    public Set<FilePair> getPairFiles() {
        return pairFiles;
    }

    public Set<String> getAllConsideredFiles() {
        return allConsideredFiles;
    }

}
