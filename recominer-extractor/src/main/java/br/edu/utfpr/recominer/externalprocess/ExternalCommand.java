package br.edu.utfpr.recominer.externalprocess;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public interface ExternalCommand {

    String[] getCommand(String... parameters);
}
