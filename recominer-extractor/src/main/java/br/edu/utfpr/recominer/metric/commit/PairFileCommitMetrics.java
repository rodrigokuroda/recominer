
package br.edu.utfpr.recominer.metric.commit;

import br.edu.utfpr.recominer.model.FilePair;
import java.util.Objects;

/**
 * Commit-based metrics for a pair of file.
 *
 * @author Rodrigo T. Kuroda
 */
public class PairFileCommitMetrics {

    /**
     * Par de arquivos referente as métricas
     */
    private final FilePair filePair;

    /**
     * Total de Committers (COMMTR): quantidade total de desenvolvedores que
     * commitaram um determinado par
     */
    private final long committers;

    /**
     * Total de Commits: COMM numero de commits feitos somente onde os dois
     * arquivos do par foram alterados.
     */
    private final long commits;

    /**
     * Minor Contributor (MNDEV): quantidade de desenvolvedores que fez mudanças
     * para o par de arquivos, mas que seu ownership é abaixo de 5% é
     * considerado minor contributor.
     */
    private final long minorContributors;

    /**
     * Major Contributor (MJDEV): quantidade de desenvolvedores que fez mudanças
     * para o par de arquivos, mas que seu ownership é acima de 5% é considerado
     * major contributor.
     */
    private final long majorContributors;

    /**
     * Soma dos commits dos dois arquivos (COMM_SUM): número de commits somando
     * todos os commits para cada um dos arquivos
     */
    private final long commitsSum;

    public PairFileCommitMetrics(FilePair filePair, long committers, long commits, long minorContributors, long majorContributors, long commitsSum) {
        this.filePair = filePair;
        this.committers = committers;
        this.commits = commits;
        this.minorContributors = minorContributors;
        this.majorContributors = majorContributors;
        this.commitsSum = commitsSum;
    }

    public FilePair getFilePair() {
        return filePair;
    }

    public long getCommitters() {
        return committers;
    }

    public long getCommits() {
        return commits;
    }

    public long getMinorContributors() {
        return minorContributors;
    }

    public long getMajorContributors() {
        return majorContributors;
    }

    public long getCommitsSum() {
        return commitsSum;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.filePair);
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

        final PairFileCommitMetrics other = (PairFileCommitMetrics) obj;
        return Objects.equals(this.filePair, other.getFilePair());
    }

}
