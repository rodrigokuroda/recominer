package br.edu.utfpr.recominer.metric.committer;

import br.edu.utfpr.recominer.dao.BichoFileDAO;
import br.edu.utfpr.recominer.model.CodeChurn;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class CummulativeCommitterFileMetricsCalculator {

    private final BichoFileDAO bichoFileDAO;
    private final Map<File, Set<Commit>> cummulativeFileCommits;
    private final Map<File, CodeChurn> cummulativeFileCodeChurn;
    private final Map<Committer, Map<File, AtomicInteger>> cummulativeCommitterFileCommits;
    private final Map<Committer, Map<File, CodeChurn>> cummulativeCommitterFileCodeChurn;

    public CummulativeCommitterFileMetricsCalculator(BichoFileDAO bichoFileDAO) {
        this.bichoFileDAO = bichoFileDAO;
        cummulativeFileCommits = new HashMap<>();
        cummulativeFileCodeChurn = new HashMap<>();
        cummulativeCommitterFileCommits = new HashMap<>();
        cummulativeCommitterFileCodeChurn = new HashMap<>();
    }

    public CommitterFileMetrics calculeForCommit(File file, Committer committer, Commit commit, int numberOfLastIssues) {

        //
        // TODO limitacao: arquivo pelo nome, nao pelo id.
        // Pelo id, podemos considerar os arquivos quando renomeados/movidos.
        // Pensar em uma estrategia para lidar com isso.
        //
        // TODO melhorar usando id do committer
        //
        final int committerFileCommits;
        if (cummulativeCommitterFileCommits.containsKey(committer)) {
            final Map<File, AtomicInteger> committerFileListCommits = cummulativeCommitterFileCommits.get(committer);
            if (committerFileListCommits.containsKey(file)) {
                committerFileListCommits.get(file).incrementAndGet();
            } else {
                committerFileListCommits.put(file, new AtomicInteger(1));
            }
            committerFileCommits = committerFileListCommits.get(file).get();
        } else {
            final Map<File, AtomicInteger> committerFileListCommits = new HashMap<>();
            committerFileListCommits.put(file, new AtomicInteger(1));
            cummulativeCommitterFileCommits.put(committer, committerFileListCommits);
            committerFileCommits = committerFileListCommits.get(file).get();
        }

        final int fileCommits;
        if (cummulativeFileCommits.containsKey(file)) {
            cummulativeFileCommits.get(file).add(commit);
            fileCommits = cummulativeFileCommits.get(file).size();
        } else {
            final Set<Commit> fileCommitsSet = new HashSet<>();
            fileCommitsSet.add(commit);
            cummulativeFileCommits.put(file, fileCommitsSet);
            fileCommits = fileCommitsSet.size();
        }

        final double ownership = (double) committerFileCommits / (double) fileCommits;

        final CodeChurn committerFileCodeChurn = bichoFileDAO.sumCodeChurnByFilename(file, commit, committer);

        final long committerFileChanges;

        if (cummulativeCommitterFileCodeChurn.containsKey(committer)) {
            final Map<File, CodeChurn> committerFileListCodeChurn = cummulativeCommitterFileCodeChurn.get(committer);
            if (committerFileListCodeChurn.containsKey(file)) {
                committerFileListCodeChurn.get(file).add(committerFileCodeChurn);
            } else {
                committerFileListCodeChurn.put(file, committerFileCodeChurn);
            }
            committerFileChanges = committerFileListCodeChurn.get(file).getChanges();
        } else {
            final Map<File, CodeChurn> committerFileListCodeChurn = new HashMap<>();
            committerFileListCodeChurn.put(file, committerFileCodeChurn);
            cummulativeCommitterFileCodeChurn.put(committer, committerFileListCodeChurn);
            committerFileChanges = committerFileCodeChurn.getChanges();
        }

        final CodeChurn fileCodeChurn;
//        if (numberOfLastIssues > 0) {
//            fileCodeChurn = bichoFileDAO.sumCodeChurnByFilename(file, commit, numberOfLastIssues);
//        } else {
            fileCodeChurn = bichoFileDAO.sumCodeChurnByFilename(file, commit);
//        }
        final long fileChanges;

        if (cummulativeFileCodeChurn.containsKey(file)) {
            cummulativeFileCodeChurn.get(file).add(fileCodeChurn);
            fileChanges = cummulativeFileCodeChurn.get(file).getChanges();
        } else {
            cummulativeFileCodeChurn.put(file, fileCodeChurn);
            fileChanges = fileCodeChurn.getChanges();
        }

        double experience = (double) committerFileChanges / (double) fileChanges;

        return new CommitterFileMetrics(committer, file, ownership, experience);
    }

    public CommitterFileMetrics calculeForCommit(String file, Committer committer, Commit commit) {
        return calculeForCommit(new File(file), committer, commit, 0);
    }

    public CommitterFileMetrics calculeForCommit(String file, Committer committer, Commit commit, int numberOfLastIssues) {
        return calculeForCommit(new File(file), committer, commit, numberOfLastIssues);
    }
//
//    public CommitterFileMetrics calculeForIndex(File file, Committer committer, Integer index, Integer quantity) {
//        //
//        // TODO limitacao: arquivo pelo nome, nao pelo id.
//        // Pelo id, podemos considerar os arquivos quando renomeados/movidos.
//        // Pensar em uma estrategia para lidar com isso.
//        //
//        // TODO melhorar usando id do committer
//        //
//        final Long committerFileCommits = bichoFileDAO.calculeCommits(file.getFileName(), committer.getName(), index, quantity);
//        final Long fileCommits = bichoFileDAO.calculeCommits(file.getFileName(), index, quantity);
//
//        final double ownership = committerFileCommits.doubleValue() / fileCommits.doubleValue();
//
//        final CodeChurn committerFileCodeChurn = bichoFileDAO.sumCodeChurnByFilename(file.getFileName(), committer.getName(), index, quantity);
//        final Long committerFileChanges = committerFileCodeChurn.getChanges();
//
//        final CodeChurn fileCodeChurn = bichoFileDAO.sumCodeChurnByFilename(file.getFileName(), index, quantity);
//        final Long fileChanges = fileCodeChurn.getChanges();
//
//        double experience = committerFileChanges.doubleValue() / fileChanges.doubleValue();
//
//        return new CommitterFileMetrics(committer, file, ownership, experience);
//    }
//
//    public CommitterFileMetrics calculeForIndex(String file, Committer committer, Integer index, Integer quantity) {
//        return calculeForIndex(new File(file), committer, index, quantity);
//    }
}
