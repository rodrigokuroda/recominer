package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.metric.committer.CommitterFileMetrics;
import br.edu.utfpr.recominer.metric.network.NetworkMetrics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author douglas
 */
public class FileIssueMetrics extends FileMetrics {

    public static final String HEADER
            = "file1;file2;"
            // metricas da issue
            + IssueMetrics.HEADER
            + NetworkMetrics.HEADER
            + CommitMetrics.HEADER
            + CommitterFileMetrics.HEADER
            // metricas de commit
            + "pv_isMajorContributor;" // committer é principal colaborador do arquivo
            + "pv_ownerExperience;" // experiencia do owner na versao anterior
            + "sameOwnership;" // 1 = ownership da release é igual ao da release anterior
//            + "committers;" // committers na release
            + "pv_totalCommitters;" // committers desde o começo ate a data final da relese
//            + "commits;" // commits do par de arquivos na release
            + "pv_totalCommits;" // todos commits do arquivo
            + "addedLines;deletedLines;changedLines;" // do arquivo, no commit de uma issue corrigida
            + "fileAge;" // idade do arquivo na versão em dias na versao em analise
            + "pv_totalFileAge;" // idade do arquivo em dias desde o primeiro commit
//            + "futureDefects;" // numero de defeitos do primeiro arquivo na proxima versao
            //            + "futureIssues;" // numero de issues do arquivo na proxima versao
            + "isFilePairChanged;" // o par mudou nesse commit? 0 = não, 1 = sim
            //+ "changedAfterReopened" // index (1a reabertura, 2a, 3a e assim sucessivamente) onde o arquivo B foi alterado após a issue ter sido reaberta
            ;

    public static final Map<String, Integer> headerIndexes;
    public static final Integer futureDefectsIndex;

    static {
        String[] headerNames = HEADER.split(";");
        headerIndexes = new LinkedHashMap<>();
        for (int i = 0; i < headerNames.length; i++) {
            headerIndexes.put(headerNames[i], i);
        }

        futureDefectsIndex = headerIndexes.get("futureDefects");
    }

    private final FileIssue fileIssue;
    private final String file2;
    private final IssueMetrics issueMetrics;
    private int changedAfterReopened;

    public FileIssueMetrics(String file, String file2, IssueMetrics issueMetrics, double... metrics) {
        super(file, metrics);
        this.issueMetrics = issueMetrics;
        this.file2 = file2;
        this.fileIssue = new FileIssue(getFile(), issueMetrics.getIssueNumber());
    }

    public FileIssueMetrics(String file, String file2, Commit commit, IssueMetrics issueMetrics, double... metrics) {
        super(file, metrics);
        this.issueMetrics = issueMetrics;
        this.file2 = file2;
        this.fileIssue = new FileIssueCommit(getFile(), issueMetrics.getIssueNumber(), commit);
    }

    public FileIssueMetrics(String file, String file2, Integer issue, double... metrics) {
        super(file, metrics);
        this.issueMetrics = new EmptyIssueMetrics();
        this.file2 = file2;
        this.fileIssue = new FileIssue(getFile(), issue);
    }

    public FileIssueMetrics(String file, String file2, Integer issue, List<Double> metrics) {
        super(file, metrics);
        this.issueMetrics = new EmptyIssueMetrics();
        this.file2 = file2;
        this.fileIssue = new FileIssue(getFile(), issue);
    }

    public String getHeader() {
        return HEADER;
    }

    public FileIssue getFileIssue() {
        return fileIssue;
    }

    public IssueMetrics getIssueMetrics() {
        return issueMetrics;
    }

    public int getChangedAfterReopened() {
        return changedAfterReopened;
    }

    public void setFileBChangedAfterReopened(int reopenedIndex) {
        this.changedAfterReopened = reopenedIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fileIssue).append(";")
                .append(file2).append(";");

        if (issueMetrics != null) {
            sb.append(issueMetrics);
        }

        if (getNetworkMetrics() != null) {
            sb.append(getNetworkMetrics());
        }

        if (getCommitMetrics() != null) {
            sb.append(getCommitMetrics());
        }

        if (getCommitterFileMetrics() != null) {
            sb.append(getCommitterFileMetrics());
        }

        for (double m : getMetrics()) {
            sb.append(m).append(";");
        }
        sb.append(getChanged());
        //sb.append(";").append(changedAfterReopened);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + fileIssue.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FileIssueMetrics)) {
            return false;
        }
        final FileIssueMetrics other = (FileIssueMetrics) obj;
        return Objects.equals(fileIssue, other.fileIssue);
    }
}
