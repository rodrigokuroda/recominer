package br.edu.utfpr.recominer.externalprocess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class ExternalProcess {

    private final Logger log = LogManager.getLogger();

    private final ProcessBuilder processBuilder;
    private final ExternalCommand command;
    private int exitValue;

    public ExternalProcess(final ExternalCommand command) {
        this.command = command;
        processBuilder = new ProcessBuilder(command.getCommand());
        processBuilder.redirectErrorStream(true);
    }

    /**
     * Start the process, wait for it conclusion and returns the exit value for
     * the subprocess.
     *
     * @return the exit value of the subprocess represented by this Process
     * object. By convention, the value 0 indicates normal termination.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public int startAndWaitFor() throws IOException, InterruptedException {
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // TODO return Process or InputStream
        String line;
        while ((line = reader.readLine()) != null) {
            log.info(line);
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
