package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.externalprocess.ExternalCommand;

/**
 * git --git-dir={}/.git --work-tree=/local/Git/{} pull --all
 *
 * @author Rodrigo T. Kuroda
 */
public class GitPullCommand implements ExternalCommand {

    private static final String GIT = "/usr/bin/git";
    private static final String GIT_DIR = "--git-dir=${GIT_DIR}/.git";
    private static final String GIT_WORK_TREE = "--work-tree=${WORK_TREE}";
    private static final String GIT_PULL = "pull";
    private static final String GIT_PULL_ALL = "--all";

    private final Project project;

    public GitPullCommand(final Project project) {
        this.project = project;
    }

    @Override
    public String[] getCommand() {
        return new String[]{GIT, GIT_DIR.replace("${GIT_DIR}", project.getRepositoryPath()), GIT_WORK_TREE.replace("${WORK_TREE}", project.getRepositoryPath()), GIT_PULL, GIT_PULL_ALL};
    }

}
