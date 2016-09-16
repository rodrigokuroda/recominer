package br.edu.utfpr.recominer.batch.dataset;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.model.ContextualMetrics;
import br.edu.utfpr.recominer.core.model.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class Dataset {

    private File file;
    private File cochange;
    private List<ContextualMetrics> metrics;
    private final Set<Commit> commits;

    public Dataset(File file, List<ContextualMetrics> metrics, Commit commit) {
        this.file = file;
        this.cochange = null;
        this.metrics = new ArrayList<>(metrics);
        
        this.commits = new HashSet<>();
        this.commits.add(commit);
    }

    public Dataset(File file, File cochange, List<ContextualMetrics> metrics, Set<Commit> commits) {
        this.file = file;
        this.cochange = cochange;
        this.metrics = new ArrayList<>(metrics);
        this.commits = commits;
    }

    public boolean add(ContextualMetrics e) {
        return metrics.add(e);
    }

    public boolean addAll(List<ContextualMetrics> metrics) {
        return this.metrics.addAll(metrics);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getCochange() {
        return cochange;
    }

    public void setCochange(File cochange) {
        this.cochange = cochange;
    }

    public List<ContextualMetrics> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<ContextualMetrics> metrics) {
        this.metrics = metrics;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("file1Id;file1;file2Id;file2;")
                .append(ContextualMetrics.HEADER)
                .append("cochanged")
                .append("\r\n");
        if (commits == null || commits.isEmpty()) {
            return sb.toString();
        }
        
        for (ContextualMetrics metric : metrics) {
            final boolean cochangeOccurred = commits.contains(metric.getCommitMetrics().getCommit());
            sb.append(file.getId())
                    .append(";")
                    .append(file)
                    .append(cochange == null ? "" : cochange.getId())
                    .append(";")
                    .append(cochange == null ? ";" : cochange)
                    .append(metric.toString())
                    .append(BooleanUtils.toInteger(cochangeOccurred))
                    .append("\r\n");
        }
        return sb.toString();
    }

}
