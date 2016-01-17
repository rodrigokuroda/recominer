package br.edu.utfpr.recominer.batch.git;

import br.edu.utfpr.recominer.batch.cvsanaly.VersionControl;
import br.edu.utfpr.recominer.externalprocess.ExternalCommand;

/**
 * git --git-dir={}/.git --work-tree=/local/Git/{} pull --all
 *
 * @author Rodrigo T. Kuroda
 */
public class GitPullCommand implements ExternalCommand {

    private static final String BASH = "/bin/bash";
    private static final String GIT = "/usr/bin/git";
    private static final String GIT_DIR = "--git-dir=${GIT_DIR}/.git";
    private static final String GIT_WORK_TREE = "--work-tree=${WORK_TREE}";
    private static final String GIT_PULL = "pull";
    private static final String GIT_PULL_ALL = "--all";

    private final VersionControl versionControl;

    public GitPullCommand(final VersionControl versionControl) {
        this.versionControl = versionControl;
    }

    @Override
    public String[] getCommand() {
        return new String[]{BASH, GIT, GIT_DIR.replace("${GIT_DIR}", versionControl.getPath()), GIT_WORK_TREE.replace("${WORK_TREE}", versionControl.getPath()), GIT_PULL, GIT_PULL_ALL};
    }

}
