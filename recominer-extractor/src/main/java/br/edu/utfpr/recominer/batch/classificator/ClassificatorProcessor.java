package br.edu.utfpr.recominer.batch.classificator;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import br.edu.utfpr.recominer.model.FilePairIssueCommit;
import br.edu.utfpr.recominer.core.model.MachineLearningPrediction;
import br.edu.utfpr.recominer.core.repository.CommitRepository;
import br.edu.utfpr.recominer.repository.FilePairIssueCommitRepository;
import br.edu.utfpr.recominer.core.repository.FileRepository;
import br.edu.utfpr.recominer.core.repository.MachineLearningPredictionRepository;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
public class ClassificatorProcessor implements ItemProcessor<Project, ClassificatorLog> {

    private static final Logger LOG = LoggerFactory.getLogger(ClassificatorProcessor.class);

    @Inject
    private CommitRepository commitRepository;

    @Inject
    private FileRepository fileRepository;

    @Inject
    private FilePairIssueCommitRepository filePairIssueCommitRepository;
    
    @Inject
    private MachineLearningPredictionRepository predictionRepository;

    @Override
    public ClassificatorLog process(Project project) throws Exception {
        commitRepository.setProject(project);
        fileRepository.setProject(project);
        filePairIssueCommitRepository.setProject(project);
        predictionRepository.setProject(project);

        ClassificatorLog classificatorLog = new ClassificatorLog(project, "RandomForest");
        classificatorLog.start();

        final List<Commit> newCommits = commitRepository.selectNewCommits();
        for (Commit newCommit : newCommits) {
            final List<File> changedFiles = fileRepository.selectChangedFilesIn(newCommit);

            final Predicate<File> fileFilter = f -> !f.getFileName().equals("CHANGES.txt");

            for (File changedFile : changedFiles.stream().filter(fileFilter).collect(Collectors.toList())) {
                List<FilePairIssueCommit> cochanges = filePairIssueCommitRepository.selectCochangesOf(changedFile, newCommit);

                final java.io.File generationPath = new java.io.File("D:\\");
                final java.io.File workingDirectory = new java.io.File(generationPath, 
                        project.getProjectName() + "/"
                        + newCommit.getId().toString() + "/"
                        + changedFile.getId().toString()
                );

                for (FilePairIssueCommit cochange : cochanges) {
                    String script;
                    try {
                        script = Paths.get(RscriptCommand.class
                                .getClassLoader()
                                .getResource("scripts/classification.R")
                                .toURI())
                                .toFile()
                                .getAbsolutePath();
                    } catch (URISyntaxException ex) {
                        LOG.error("Error to load R script for classification.", ex);
                        throw ex;
                    }
                    final java.io.File train = new java.io.File(workingDirectory,
                            cochange.getFilePair().getFile2().getId().toString() + "/train.csv");
                    final java.io.File test = new java.io.File(workingDirectory, "test.csv");

                    if (train.exists()
                            && test.exists()) {
                        LOG.info("Running classification on file " + changedFile.getId() + " of new commit " + newCommit.getId());
                        
                        RscriptCommand command = new RscriptCommand(project);
                        ExternalProcess ep = new ExternalProcess(command);
                        int returnCode = ep.startAndWaitFor(script,
                                generationPath.getAbsolutePath(),
                                project.getProjectName(),
                                newCommit.getId().toString(),
                                changedFile.getId().toString(),
                                cochange.getFilePair().getFile2().getId().toString()
                        );

                        if (returnCode != 0) {
                            LOG.warn("R return code " + returnCode);
                        }
                    } else {
                        LOG.warn("Train or test dataset does not exists for project "
                                + project.getProjectName()
                                + ", commit " + newCommit.getId()
                                + ", file " + changedFile.getId()
                                + ", cochange " + cochange.getFilePair().getFile2().getId().toString());
                    }
                }

                final java.io.File resultTest = new java.io.File(workingDirectory,
                        "resultTest.csv"
                );
                
                if (resultTest.exists()) {
                    Scanner resultReader = new Scanner(resultTest);
                    resultReader.useDelimiter("\r\n");
                    while (resultReader.hasNextLine()) {
                        String resultLine = resultReader.next();
                        String[] result = resultLine.split(";");

                        File cochange = new File(Integer.valueOf(result[0]), result[1]);
                        String predictionResult = result[3];

                        MachineLearningPrediction prediction = 
                                new MachineLearningPrediction(
                                        changedFile, newCommit, 
                                        cochange, predictionResult, "RandomForest");

                        predictionRepository.save(prediction);
                    }
                }
            }
        }
        classificatorLog.stop();

        return classificatorLog;
    }
}
