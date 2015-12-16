package br.edu.utfpr.recominer.batch.git;

import br.edu.utfpr.recominer.externalprocess.ExternalCommand;

/**
 * git --git-dir={}/.git --work-tree=/local/Git/{} pull --all
 *
 * @author Rodrigo T. Kuroda
 */
public class GitPullCommand implements ExternalCommand {

    private static final String BASH = "/bin/bash";
    private static final String GIT = "/usr/bin/git";
    private static final String GIT_DIR = "--git-dir=${DIR}";
    private static final String GIT_WORK_TREE = "--work-tree=${WORK_TREE}";
    private static final String GIT_PULL = "pull";
    private static final String GIT_PULL_ALL = "--all";

    private final String gitDir;
    private final String workTree;

    public GitPullCommand(String gitDir, String workTree) {
        this.gitDir = gitDir;
        this.workTree = workTree;
    }

    @Override
    public String[] getCommand() {
        return new String[]{BASH, GIT, GIT_DIR.replace("${DIR}", gitDir), GIT_WORK_TREE.replace("${WORK_TREE}", workTree), GIT_PULL, GIT_PULL_ALL};
    }

}
