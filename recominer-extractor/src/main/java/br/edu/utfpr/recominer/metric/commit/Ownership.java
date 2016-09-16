package br.edu.utfpr.recominer.metric.commit;

import br.edu.utfpr.recominer.core.model.FilePair;
import java.util.Objects;

/**
 * File pair commit-based metrics for developers.
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class Ownership {

    private final FilePair userFilePair;

    /**
     * Proporção de Ownership: taxa (valor entre 0 e 1) do numero de commits que
     * o desenvolvedor fez relativo ao total de commits do par.
     */
    private final double rate;

    /**
     * Developer experience (EXP): é determinado pelo numero de commits
     * completados por um desenvolvedor feitas anteriormente da mudança atual
     * iniciar. EXP é calculada pela média geométrica ponderada sobre o conjunto
     * de desenvolvedores envolvidos nas mudanças daquele par, onde os pesos são
     * o número de commits do par de cada desenvolvedor.
     */
    private final double experience;

    /**
     * Recent experience (REXP): deltas recentes ganham pesos maiores que deltas
     * feitos a longo tempo - (o numero de deltas feitos n anos atrás recebe o
     * peso 1/n+1 )
     */
    private final double recentExperience;

    /**
     * Subsystem Experience (SEXP): apenas deltas do subsistema que a mudança
     * toca são incluídos calculando a experiencia do desenvolvedor.
     */
    private final double subsystemExperience;

    public Ownership(FilePair userPairFile, double rate, double experience, double recentExperience, double subsystemExperience) {
        this.userFilePair = userPairFile;
        this.rate = rate;
        this.experience = experience;
        this.recentExperience = recentExperience;
        this.subsystemExperience = subsystemExperience;
    }

    public FilePair getUserPairFile() {
        return userFilePair;
    }

    public double getRate() {
        return rate;
    }

    public double getExperience() {
        return experience;
    }

    public double getRecentExperience() {
        return recentExperience;
    }

    public double getSubsystemExperience() {
        return subsystemExperience;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.userFilePair);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ownership other = (Ownership) obj;
        return Objects.equals(this.userFilePair, other.getUserPairFile());
    }

}
