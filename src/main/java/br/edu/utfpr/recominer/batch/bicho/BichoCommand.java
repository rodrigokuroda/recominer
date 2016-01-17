package br.edu.utfpr.recominer.batch.bicho;

import br.edu.utfpr.recominer.batch.aggregator.Project;
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
    private static final String BICHO_BACKEND_USER = "--backend-user=${BACKEND_USER}";
    private static final String BICHO_BACKEND_PASSWORD = "--backend-password=${BACKEND_PASSWORD}";
    private static final String BICHO_BACKEND_TOKEN = "--backend-token=${TOKEN}";
    private static final String BICHO_DB_USER = "--db-user-out=root";
    private static final String BICHO_DB_PASSWORD = "--db-password-out=root";
    private static final String BICHO_DB_NAME = "--db-database-out=${DB_NAME}_issues";
    private static final String BICHO_DELAY = "-d";
    private static final String BICHO_DEBUG = "-g";
    private static final String BICHO_ISSUE_TRACKER_SYSTEM = "-b";
    private static final String BICHO_ISSUE_TRACKER_URL = "-u";

    private final Project project;

    BichoCommand(final Project project) {
        this.project = project;
    }

    @Override
    public String[] getCommand() {
        final List<String> command = new ArrayList<>();
        command.add(PYTHON);
        command.add(BICHO);

        final IssueTracker its = project.getIssueTracker();
        if (its.getSystem() == IssueTrackerSystem.GITHUB
                && project.getIssueTracker() != null) {
            command.add(BICHO_BACKEND_TOKEN.replace("${TOKEN}", its.getToken()));
        } else if (its.getUser() != null && its.getPassword() != null) {
            command.add(BICHO_BACKEND_USER.replace("${BACKEND_USER}", its.getUser()));
            command.add(BICHO_BACKEND_PASSWORD.replace("${BACKEND_PASSWORD}", its.getPassword()));
        }

        command.add(BICHO_DB_USER);
        command.add(BICHO_DB_PASSWORD);
        command.add(BICHO_DB_NAME.replace("${DB_NAME}", project.getProjectName().toLowerCase()));

        command.add(BICHO_DELAY);
        command.add(its.getMiningDelay().toString());
        command.add(BICHO_DEBUG);

        command.add(BICHO_ISSUE_TRACKER_SYSTEM);
        command.add(its.getSystem().getCode());

        command.add(BICHO_ISSUE_TRACKER_URL);
        command.add(project.getIssueTrackerUrl());

        return command.toArray(new String[command.size()]);
    }
}
