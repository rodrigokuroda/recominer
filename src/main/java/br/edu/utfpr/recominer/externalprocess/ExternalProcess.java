package br.edu.utfpr.recominer.externalprocess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class ExternalProcess {

    private final ProcessBuilder processBuilder;
    private final ExternalCommand command;

    public ExternalProcess(final ExternalCommand command) {
        this.command = command;
        processBuilder = new ProcessBuilder(command.getCommand());
        processBuilder.redirectErrorStream(true);
    }

    public void start() throws IOException, InterruptedException {
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // TODO return Process or InputStream
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(getClass().getSimpleName() + ": " + line);
        }
        process.waitFor();
    }

    public ExternalCommand getCommand() {
        return command;
    }
}
