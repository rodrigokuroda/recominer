package br.edu.utfpr.recominer.batch.cvsanaly;

import br.edu.utfpr.recominer.externalprocess.ExternalCommand;
import java.util.ArrayList;
import java.util.List;

/**
 * cvsanaly2 --debug --writable-path=./${1} --save-logfile=./${1}/vcs_logfile
 * --db-user=root --db-password=root --db-database=${DB_NAME}_vcs --metrics-all
 * --metrics-noerr --extensions=CommitsLOCDet,FileTypes ${2} >
 * ${1}/vcs_miner.log;

 * @author Rodrigo T. Kuroda
 */
class CvsanalyCommand implements ExternalCommand {

    private static final String PYTHON = "/usr/bin/python";
    private static final String CVSANALY = "/usr/local/bin/cvsanaly2";
    private static final String CVSANALY_DB_USER = "--db-user=root";
    private static final String CVSANALY_DB_PASSWORD = "--db-password=root";
    private static final String CVSANALY_DB_NAME = "--db-database=${DB_NAME}_vcs";
    private static final String CVSANALY_DEBUG = "--debug";
    private static final String CVSANALY_METRICS_ALL = "--metrics-all";
    private static final String CVSANALY_METRICS_NOERR = "--metrics-noerr";
    private static final String CVSANALY_METRICS_EXTENSIONS = "--extensions=CommitsLOCDet,FileTypes";

    private final String name;
    private final String url;

    public CvsanalyCommand(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String[] getCommand() {
        final List<String> command = new ArrayList<>();
        command.add(PYTHON);
        command.add(CVSANALY);

        command.add(CVSANALY_DB_USER);
        command.add(CVSANALY_DB_PASSWORD);
        command.add(CVSANALY_DB_NAME.replace("${DB_NAME}", name));

        command.add(CVSANALY_DEBUG);

        command.add(CVSANALY_METRICS_ALL);
        command.add(CVSANALY_METRICS_NOERR);
        command.add(CVSANALY_METRICS_EXTENSIONS);

        command.add(url);

        return command.toArray(new String[command.size()]);
    }
}
