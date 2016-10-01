package br.edu.utfpr.recominer.batch.classificator;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.MachineLearningPrediction;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.CommitRepository;
import br.edu.utfpr.recominer.core.repository.FileRepository;
import br.edu.utfpr.recominer.core.repository.MachineLearningPredictionRepository;
import br.edu.utfpr.recominer.externalprocess.ExternalProcess;
import br.edu.utfpr.recominer.filter.FileFilter;
import br.edu.utfpr.recominer.repository.FilePairIssueCommitRepository;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
@StepScope
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

    @Value("#{jobParameters[filenameFilter]}")
    private String filter;

    @Value("#{jobParameters[workingDir]}")
    private String workingDir;
    
    @Value("#{jobParameters[issueKey]}")
    private String issueKey;

    @Override
    public ClassificatorLog process(Project project) throws Exception {
        commitRepository.setProject(project);
        fileRepository.setProject(project);
        filePairIssueCommitRepository.setProject(project);
        predictionRepository.setProject(project);

        ClassificatorLog classificatorLog = new ClassificatorLog(project, "RandomForest");
        classificatorLog.start();

        final List<Commit> newCommits;
        if (StringUtils.isBlank(issueKey)) {
            newCommits = commitRepository.selectNewCommitsForCalculator();
        } else {
            newCommits = commitRepository.selectCommitsOf(issueKey);
        }
        
        int processedCommits = 0;
        for (Commit newCommit : newCommits) {
            LOG.info(++processedCommits + " of " + newCommits.size() + " commits processed.");
            final List<File> changedFiles = fileRepository.selectChangedFilesIn(newCommit);

            final Predicate<File> fileFilter = FileFilter.getFilterByFilename(filter);

            int processedFile = 0;
            final List<File> filesToProcessed = changedFiles.stream().filter(fileFilter).collect(Collectors.toList());
            for (File changedFile : filesToProcessed) {
                LOG.info(++processedFile + " of " + filesToProcessed.size() + " commits processed.");
                //List<FilePairIssueCommit> cochanges = filePairIssueCommitRepository.selectCochangesOf(changedFile, newCommit);

                final java.io.File generationPath = getWorkingDirectory();
                final java.io.File workingDirectory = new java.io.File(generationPath,
                        project.getProjectName() + "/"
                        + newCommit.getId().toString() + "/"
                        + changedFile.getId().toString()
                );

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

                RscriptCommand command = new RscriptCommand(project);
                ExternalProcess ep = new ExternalProcess(command);
                int returnCode = ep.startAndWaitFor(script,
                        generationPath.getAbsolutePath(),
                        project.getProjectName(),
                        newCommit.getId().toString(),
                        changedFile.getId().toString()
                );

                if (returnCode != 0) {
                    LOG.warn("R return code " + returnCode);
                } else {
                    LOG.debug("Classification executed successfully.");

                    final java.io.File resultTest = new java.io.File(workingDirectory,
                            "resultsTest.csv"
                    );

                    if (resultTest.exists()) {
                        Scanner resultReader = new Scanner(resultTest);
                        resultReader.useDelimiter("\r\n");
                        while (resultReader.hasNextLine()) {
                            String resultLine = resultReader.nextLine().replace("\"", "");
                            String[] result = resultLine.split(";");

                            File cochange = new File(Integer.valueOf(result[0]), result[1]);
                            String predictionResult = result[2].replace("\"", "");

                            MachineLearningPrediction prediction
                                    = new MachineLearningPrediction(
                                            changedFile, newCommit,
                                            cochange, predictionResult, "RandomForest");

                            try {
                                predictionRepository.save(prediction);
                            } catch (org.springframework.dao.DuplicateKeyException ex) {
                                LOG.warn("Duplicated key for commit " + newCommit.getId() + ", file " + changedFile.getId(), ex);
                            }
                        }
                    } else {
                        LOG.warn("Results file does not exist.");
                    }
                }
            }
        }
        classificatorLog.stop();

        return classificatorLog;
    }

    private java.io.File getWorkingDirectory() {
        if (workingDir == null) {
            workingDir = "generated";
        }
        return new java.io.File(workingDir);
    }
}
