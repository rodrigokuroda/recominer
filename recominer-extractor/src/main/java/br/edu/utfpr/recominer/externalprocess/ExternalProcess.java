package br.edu.utfpr.recominer.externalprocess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class ExternalProcess {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalProcess.class);

    private final ExternalCommand command;
    private int exitValue;

    public ExternalProcess(final ExternalCommand command) {
        this.command = command;
    }

    /**
     * Start the process, wait for it conclusion and returns the exit value for
     * the subprocess.
     *
     * @param params Parameters to command.
     * @return the exit value of the subprocess represented by this Process
     * object. By convention, the value 0 indicates normal termination.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public int startAndWaitFor(String... params) throws IOException, InterruptedException {
        String[] commandArray = command.getCommand(params);
        LOG.info("Executing external command {} ", String.join(" ", commandArray));
        
        ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            LOG.debug(line);
        }
        process.waitFor();
        exitValue = process.exitValue();
        return exitValue;
    }

    public ExternalCommand getCommand() {
        return command;
    }

    public int getExitValue() {
        return exitValue;
    }
}
