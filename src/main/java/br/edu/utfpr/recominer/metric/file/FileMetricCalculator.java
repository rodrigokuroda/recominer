package br.edu.utfpr.recominer.metric.file;

import br.edu.utfpr.recominer.batch.calculator.FileMetricDao;
import br.edu.utfpr.recominer.metric.committer.Committer;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.Issue;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FileMetricCalculator {

    private final FileMetricDao dao;

    public FileMetricCalculator(final FileMetricDao dao) {
        this.dao = dao;
    }

    public FileMetrics calcule(final File file, final Issue issue, final Commit commit) {
        final Committer lastCommitter = dao.selectLastCommitter(file, commit);
        final boolean sameOwnership = commit.getCommitter().equals(lastCommitter);
        final FileMetrics fileMeasure = new FileMetrics(
                file, commit,
                dao.calculeCodeChurn(file, issue, commit),
                dao.calculeCommitters(file, commit),
                dao.calculeCommits(file, commit),
                dao.calculeFileAgeInDays(file, commit),
                dao.calculeTotalFileAgeInDays(file, commit)
        );
        return fileMeasure;
    }


}
