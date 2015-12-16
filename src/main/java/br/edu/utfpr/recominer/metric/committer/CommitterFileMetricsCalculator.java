package br.edu.utfpr.recominer.metric.committer;

import br.edu.utfpr.recominer.dao.BichoFileDAO;
import br.edu.utfpr.recominer.model.CodeChurn;
import br.edu.utfpr.recominer.model.File;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class CommitterFileMetricsCalculator {

    private final BichoFileDAO bichoFileDAO;

    public CommitterFileMetricsCalculator(BichoFileDAO bichoFileDAO) {
        this.bichoFileDAO = bichoFileDAO;
    }

//    public CommitterFileMetrics calculeForCommit(File file, Commit commit, String fixVersion) {
//        final Committer committer = commit.getCommiter();
//        //
//        // TODO limitacao: arquivo pelo nome, nao pelo id.
//        // Pelo id, podemos considerar os arquivos quando renomeados/movidos.
//        // Pensar em uma estrategia para lidar com isso.
//        //
//        // TODO melhorar usando id do committer
//        //
//        final Long committerFileCommits = bichoFileDAO.calculeCommits(file.getFileName(), committer.getName(), fixVersion);
//        final Long fileCommits = bichoFileDAO.calculeCommits(file.getFileName(), fixVersion);
//
//        final double ownership = committerFileCommits.doubleValue() / fileCommits.doubleValue();
//
//        final CodeChurn committerFileCodeChurn = bichoFileDAO.sumCodeChurnByFilename(file.getFileName(), committer.getName(), fixVersion);
//        final Long committerFileChanges = committerFileCodeChurn.getChanges();
//
//        final CodeChurn fileCodeChurn = bichoFileDAO.sumCodeChurnByFilename(file.getFileName(), fixVersion);
//        final Long fileChanges = fileCodeChurn.getChanges();
//
//        double experience = committerFileChanges.doubleValue() / fileChanges.doubleValue();
//
//        return new CommitterFileMetrics(committer, file, ownership, experience);
//    }
    public CommitterFileMetrics calculeForVersion(File file, Committer committer, String fixVersion) {
        //
        // TODO limitacao: arquivo pelo nome, nao pelo id.
        // Pelo id, podemos considerar os arquivos quando renomeados/movidos.
        // Pensar em uma estrategia para lidar com isso.
        //
        // TODO melhorar usando id do committer
        //
        final Long committerFileCommits = bichoFileDAO.calculeCommits(file.getFileName(), committer.getName(), fixVersion);
        final Long fileCommits = bichoFileDAO.calculeCommits(file.getFileName(), fixVersion);

        final double ownership = committerFileCommits.doubleValue() / fileCommits.doubleValue();

        final CodeChurn committerFileCodeChurn = bichoFileDAO.sumCodeChurnByFilename(file.getFileName(), committer.getName(), fixVersion);
        final Long committerFileChanges = committerFileCodeChurn.getChanges();

        final CodeChurn fileCodeChurn = bichoFileDAO.sumCodeChurnByFilename(file.getFileName(), fixVersion);
        final Long fileChanges = fileCodeChurn.getChanges();

        double experience = committerFileChanges.doubleValue() / fileChanges.doubleValue();

        return new CommitterFileMetrics(committer, file, ownership, experience);
    }

    public CommitterFileMetrics calculeForVersion(String file, Committer committer, String fixVersion) {
        return calculeForVersion(new File(file), committer, fixVersion);
    }

    public CommitterFileMetrics calculeForIndex(File file, Committer committer, Integer index, Integer quantity) {
        //
        // TODO limitacao: arquivo pelo nome, nao pelo id.
        // Pelo id, podemos considerar os arquivos quando renomeados/movidos.
        // Pensar em uma estrategia para lidar com isso.
        //
        // TODO melhorar usando id do committer
        //
        final Long committerFileCommits = bichoFileDAO.calculeCommits(file.getFileName(), committer.getName(), index, quantity);
        final Long fileCommits = bichoFileDAO.calculeCommits(file.getFileName(), index, quantity);

        final double ownership = committerFileCommits.doubleValue() / fileCommits.doubleValue();

        final CodeChurn committerFileCodeChurn = bichoFileDAO.sumCodeChurnByFilename(file.getFileName(), committer.getName(), index, quantity);
        final Long committerFileChanges = committerFileCodeChurn.getChanges();

        final CodeChurn fileCodeChurn = bichoFileDAO.sumCodeChurnByFilename(file.getFileName(), index, quantity);
        final Long fileChanges = fileCodeChurn.getChanges();

        double experience = committerFileChanges.doubleValue() / fileChanges.doubleValue();

        return new CommitterFileMetrics(committer, file, ownership, experience);
    }

    public CommitterFileMetrics calculeForIndex(String file, Committer committer, Integer index, Integer quantity) {
        return calculeForIndex(new File(file), committer, index, quantity);
    }
}
