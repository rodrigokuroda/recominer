package br.edu.utfpr.recominer.metric.file;

import br.edu.utfpr.recominer.model.CodeChurn;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.File;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FileMetrics {

    public static final String HEADER
            = "committers;" // file's committers
            + "commits;" // file's commits
            + "addedLines;deletedLines;changedLines;" // of 1st file, in current commit
            + "fileAge;" // file's age in release
            + "fileAgeInProject;" // file's age in project
            // + "totalCommitters;" // file's committers since the 1st commit of this file
            // + "totalCommits;" // todos commits do arquivo
            ;

    private final File file;
    private final Commit commit;
    private final CodeChurn codeChurn;
    private final long committers;
    private final long commits;
    private final long fileAgeInRelease;
    private final long fileAgeInProject;

    public FileMetrics(final File file, final Commit commit,
            final CodeChurn codeChurn,
            final long committers, final long commits,
            final long fileAgeInRelease, final long fileAgeInProject) {
        this.file = file;
        this.commit = commit;
        this.codeChurn = codeChurn;
        this.committers = committers;
        this.commits = commits;
        this.fileAgeInRelease = fileAgeInRelease;
        this.fileAgeInProject = fileAgeInProject;
    }

    public File getFile() {
        return file;
    }

    public Commit getCommit() {
        return commit;
    }

    public CodeChurn getCodeChurn() {
        return codeChurn;
    }

    public long getCommitters() {
        return committers;
    }

    public long getCommits() {
        return commits;
    }

    public long getFileAgeInRelease() {
        return fileAgeInRelease;
    }

    public long getFileAgeInProject() {
        return fileAgeInProject;
    }

}
