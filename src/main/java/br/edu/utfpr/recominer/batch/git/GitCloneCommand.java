package br.edu.utfpr.recominer.batch.git;

import br.edu.utfpr.recominer.batch.cvsanaly.VersionControl;
import br.edu.utfpr.recominer.externalprocess.ExternalCommand;
import br.edu.utfpr.recominer.model.Version;

/**
 * git --git-dir={} --work-tree={} clone {}
 *
 * @author Rodrigo T. Kuroda
 */
public class GitCloneCommand implements ExternalCommand {

    private static final String BASH = "/bin/bash";
    private static final String GIT = "/usr/bin/git";
    private static final String CLONE = "clone";
    
    private final VersionControl versionControl;
    private final String path;

    public GitCloneCommand(final VersionControl versionControl, final String path) {
        this.versionControl = versionControl;
        this.path = path;
    }

    @Override
    public String[] getCommand() {
        return new String[]{BASH, GIT, CLONE, versionControl.getUrl(), path};
    }

    public String getPath() {
        return path;
    }

}
