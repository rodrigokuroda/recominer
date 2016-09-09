package br.edu.utfpr.recominer.batch.classificator;

import br.edu.utfpr.recominer.externalprocess.ExternalCommand;
import br.edu.utfpr.recominer.core.model.Project;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rscript \<file\> \<parameters\>
 *
 * @author Rodrigo T. Kuroda
 */
public class RscriptCommand implements ExternalCommand {

    private static final Logger LOG = LoggerFactory.getLogger(RscriptCommand.class);

    private static final String RSCRIPT = "C:\\Program Files\\R\\R-3.1.1\\bin\\x64\\Rscript";
    private final Project project;

    public RscriptCommand(final Project project) {
        this.project = project;
    }

    @Override
    public String[] getCommand(String... parameters) {
        List<String> command = new ArrayList<>();
        command.add(RSCRIPT);
        command.addAll(Arrays.asList(parameters));

        return command.toArray(new String[command.size()]);
    }
}
