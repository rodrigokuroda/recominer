package br.edu.utfpr.recominer.batch.git;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.externalprocess.ExternalCommand;
import java.io.File;

/**
 * git --git-dir={} --work-tree={} clone {}
 *
 * @author Rodrigo T. Kuroda
 */
public class GitCloneCommand implements ExternalCommand {

    private static final String GIT = "/usr/bin/git";
    private static final String CLONE = "clone";
    
    private final Project project;
    private final String path;

    public GitCloneCommand(final Project project, final String path) {
        this.project = project;
        this.path = path;
    }

    @Override
    public String[] getCommand() {
        final File projectRepositoryPath = new File(path, project.getProjectName());
        return new String[]{GIT, CLONE, project.getVersionControlUrl(), projectRepositoryPath.getAbsolutePath()};
    }

    public String getPath() {
        return path;
    }

}
