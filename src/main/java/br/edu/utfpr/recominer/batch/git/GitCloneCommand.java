package br.edu.utfpr.recominer.batch.git;

import br.edu.utfpr.recominer.externalprocess.ExternalCommand;

/**
 * git --git-dir={} --work-tree={} clone {}
 *
 * @author Rodrigo T. Kuroda
 */
public class GitCloneCommand implements ExternalCommand {

    private static final String BASH = "/bin/bash";
    private static final String GIT = "/usr/bin/git";
    private static final String GIT_DIR = "--git-dir=${DIR}/.git";
    private static final String GIT_WORK_TREE = "--work-tree=${WORK_TREE}";
    private static final String GIT_PULL = "clone";

    private final String gitDir;
    private final String workTree;
    private final String url;

    public GitCloneCommand(final String gitDir, final String workTree, final String url) {
        this.gitDir = gitDir;
        this.workTree = workTree;
        this.url = url;
    }

    @Override
    public String[] getCommand() {
        return new String[]{BASH, GIT, GIT_DIR.replace("${DIR}", gitDir), GIT_WORK_TREE.replace("${WORK_TREE}", workTree), GIT_PULL, url};
    }

}
