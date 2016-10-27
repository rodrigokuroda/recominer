package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.externalprocess.ExternalCommand;
import java.io.File;

/**
 * git --git-dir={} --work-tree={} clone {}
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class GitCloneCommand implements ExternalCommand {

    private static final String GIT = "/usr/bin/git";
    private static final String CLONE = "clone";
    
    private final Project project;

    public GitCloneCommand(final Project project) {
        this.project = project;
    }

    @Override
    public String[] getCommand(String... parameters) {
        final File projectRepositoryPath = new File(project.getRepositoryPath());
        return new String[]{GIT, CLONE, project.getVersionControlUrl(), projectRepositoryPath.getAbsolutePath()};
    }

}
