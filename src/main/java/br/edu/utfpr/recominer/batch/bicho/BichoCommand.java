package br.edu.utfpr.recominer.batch.bicho;

import br.edu.utfpr.recominer.externalprocess.ExternalCommand;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rodrigo T. Kuroda
 */
class BichoCommand implements ExternalCommand {

    private static final String PYTHON = "/usr/bin/python";
    private static final String BICHO = "/usr/local/bin/bicho";
    private static final String BICHO_BACKEND_USER = "--backend-user=chavesrules";
    private static final String BICHO_BACKEND_PASSWORD = "--backend-password=acapulcorules";
    private static final String BICHO_BACKEND_TOKEN = "--backend-token=${TOKEN}";
    private static final String BICHO_DB_USER = "--db-user-out=root";
    private static final String BICHO_DB_PASSWORD = "--db-password-out=root";
    private static final String BICHO_DB_NAME = "--db-database-out=${DB_NAME}_issues";
    private static final String BICHO_DELAY = "-d 20";
    private static final String BICHO_DEBUG = "-g";
    private static final String BICHO_BUGZILLA_ISSUE_TRACKER = "-b";
    private static final String BICHO_ISSUE_TRACKER_URL = "-u";

    private final String project;
    private final String url;
    private final IssueTrackerSystem its;

    public BichoCommand(String project, String url, IssueTrackerSystem its) {
        this.project = project;
        this.url = url;
        this.its = its;
    }

    @Override
    public String[] getCommand() {
        final List<String> command = new ArrayList<>();
        command.add(PYTHON);
        command.add(BICHO);

        if (its == IssueTrackerSystem.GITHUB) {
            command.add(BICHO_BACKEND_TOKEN);
        } else {
            command.add(BICHO_BACKEND_USER);
            command.add(BICHO_BACKEND_PASSWORD);
        }

        command.add(BICHO_DB_USER);
        command.add(BICHO_DB_PASSWORD);
        command.add(BICHO_DB_NAME.replace("${DB_NAME}", project));

        command.add(BICHO_DELAY);
        command.add(BICHO_DEBUG);

        command.add(BICHO_BUGZILLA_ISSUE_TRACKER);
        command.add(its.getCode());

        command.add(BICHO_ISSUE_TRACKER_URL);
        command.add(url);

        return command.toArray(new String[command.size()]);
    }
}
