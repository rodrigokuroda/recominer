package br.edu.utfpr.recominer.externalprocess;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public interface ExternalCommand {

    String[] getCommand(String... parameters);
}
