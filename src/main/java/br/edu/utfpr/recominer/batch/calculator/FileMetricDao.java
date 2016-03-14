package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.metric.committer.Committer;
import br.edu.utfpr.recominer.model.CodeChurn;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.Issue;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public interface FileMetricDao {

    Set<Committer> selectCommitters(File file);

    Long calculeCommits(File file);

    Long calculeCommits(File file, Committer committer);

    CodeChurn calculeCodeChurn(File file);

    CodeChurn calculeCodeChurn(File file, Committer committer);

    Long calculeCommitters(File file, Commit commit);

    Long calculeCommits(File file, Commit commit);

    CodeChurn calculeCodeChurn(File file, Issue issue, Commit commit);

    Long calculeFileAgeInDays(File file, Commit commit);

    Long calculeTotalFileAgeInDays(File file, Commit commit);

    Committer selectLastCommitter(File file, Commit commit);

    Set<Issue> selectIssues(File file);
}