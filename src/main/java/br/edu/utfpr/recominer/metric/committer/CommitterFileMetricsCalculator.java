package br.edu.utfpr.recominer.metric.committer;

import br.edu.utfpr.recominer.batch.calculator.FileMetricDao;
import br.edu.utfpr.recominer.model.CodeChurn;
import br.edu.utfpr.recominer.model.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class CommitterFileMetricsCalculator {

    private final FileMetricDao fileDao;

    private final Set<Committer> majorContributorsInPreviousVersion;
    private final Map<File, Double> ownerExperience;
    private final Map<Committer, CommitterFileMetrics> committerFileMetricsList;

    public CommitterFileMetricsCalculator(FileMetricDao fileDao) {
        this.fileDao = fileDao;
        this.committerFileMetricsList = new HashMap<>();
        this.ownerExperience = new HashMap<>();
        this.majorContributorsInPreviousVersion = new HashSet<>();
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
    public CommitterFileMetrics calculeForVersion(File file, Committer committer) {
        //
        // TODO limitacao: arquivo pelo nome, nao pelo id.
        // Pelo id, podemos considerar os arquivos quando renomeados/movidos.
        // Pensar em uma estrategia para lidar com isso.
        //
        // TODO melhorar usando id do committer
        //
        final Long committerFileCommits = fileDao.calculeCommits(file, committer);
        final Long fileCommits = fileDao.calculeCommits(file);

        final double ownership = committerFileCommits.doubleValue() / fileCommits.doubleValue();

        final CodeChurn committerFileCodeChurn = fileDao.calculeCodeChurn(file, committer);
        final Long committerFileChanges = committerFileCodeChurn.getChanges();

        final CodeChurn fileCodeChurn = fileDao.calculeCodeChurn(file);
        final Long fileChanges = fileCodeChurn.getChanges();

        double experience = committerFileChanges.doubleValue() / fileChanges.doubleValue();

        if (ownerExperience.containsKey(file)) {
            ownerExperience.put(file, Math.max(experience, ownerExperience.get(file)));
        } else {
            ownerExperience.put(file, experience);
        }

        final CommitterFileMetrics committerFileMetrics = new CommitterFileMetrics(committer, file, ownership, experience);

        committerFileMetricsList.put(committer, committerFileMetrics);
        if (committerFileMetrics.getOwnership() > 0.05) { // maior que 5% = major
            majorContributorsInPreviousVersion.add(committer);
        }

        return committerFileMetrics;
    }
}
