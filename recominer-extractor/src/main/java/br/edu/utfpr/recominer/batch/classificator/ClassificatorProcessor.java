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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private final Logger log = LoggerFactory.getLogger(ClassificatorProcessor.class);
    private static final String DEFAULT_SCRIPT_LOCATION = "scripts/classification.R";

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

    @Value("#{jobParameters[classificarionScript]}")
    private String classificationScript;
    
    @Value("#{jobParameters[onlyOneRandomFileFromIssue]}")
    private String onlyOneRandomFileFromIssue;

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
            newCommits = commitRepository.selectNewCommitsForClassification();
            log.info("{} new commits to be processed.", newCommits.size());
        } else {
            newCommits = commitRepository.selectFirstCommitsOf(issueKey);
            log.info("Running classification for issue {}", issueKey);
        }
        
        // Load script location
        final String script;
        if (classificationScript != null
                && new java.io.File(classificationScript).exists()) {
            // use parameterized script, if configured
            script = classificationScript;
        } else {
            // copy script to external path outside jar, if necessary
            if (new java.io.File(DEFAULT_SCRIPT_LOCATION).exists()) {
                script = DEFAULT_SCRIPT_LOCATION;
            } else {
                script = exportResource("/" + DEFAULT_SCRIPT_LOCATION);
            }
        }

        int processedCommits = 0;
        for (Commit newCommit : newCommits) {
            log.info("{} of {} commits processed.", ++processedCommits, newCommits.size());
            // select changed files
            final List<File> changedFiles;
            if (StringUtils.isBlank(onlyOneRandomFileFromIssue)) {
                changedFiles = fileRepository.selectChangedFilesIn(newCommit);
            } else {
                // get randomly chosen file in CalculatorProcessor
                changedFiles = fileRepository.selectCalculatedChangedFilesIn(newCommit);
            }

            final Predicate<File> fileFilter = FileFilter.getFilterByFilename(filter);

            int processedFile = 0;
            final List<File> filesToProcessed = changedFiles.stream().filter(fileFilter).collect(Collectors.toList());
            for (File changedFile : filesToProcessed) {
                log.info("{} of {} commits processed.", ++processedFile, filesToProcessed.size());
                //List<FilePairIssueCommit> cochanges = filePairIssueCommitRepository.selectCochangesOf(changedFile, newCommit);

                final java.io.File generationPath = getWorkingDirectory();
                final java.io.File workingDirectory = new java.io.File(generationPath,
                        project.getProjectName() + "/"
                        + newCommit.getId().toString() + "/"
                        + changedFile.getId().toString()
                );

                RscriptCommand command = new RscriptCommand(project);
                ExternalProcess ep = new ExternalProcess(command);
                int returnCode = ep.startAndWaitFor(script,
                        generationPath.getAbsolutePath(),
                        project.getProjectName(),
                        newCommit.getId().toString(),
                        changedFile.getId().toString()
                );

                if (returnCode != 0) {
                    log.warn("R has returned code {}.", returnCode);
                } else {
                    log.debug("Classification executed successfully.");

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
                            final String predictionProbabilityString = result[3].replace("NA", "0");
                            Double predictionProbability = Double.valueOf(predictionProbabilityString);

                            MachineLearningPrediction prediction
                                    = new MachineLearningPrediction(
                                            changedFile, newCommit,
                                            cochange, predictionResult, "RandomForest", predictionProbability);

                            try {
                                predictionRepository.save(prediction);
                            } catch (org.springframework.dao.DuplicateKeyException ex) {
                                log.warn("Duplicated key for commit {}, file {}", newCommit.getId(),changedFile.getId());
                                log.warn("Exception was: ", ex);
                            }
                        }
                    } else {
                        log.warn("Results file {} does not exist.", resultTest.getAbsolutePath());
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

    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName The resource to copy outside
     * @return The path to the exported resource
     */
    public String exportResource(String resourceName) {
        //note that each / is a directory down in the "jar tree" been the jar the root of the tree
        InputStream stream = ClassificatorProcessor.class.getResourceAsStream(resourceName);
        if (stream == null) {
            throw new IllegalArgumentException("Cannot get resource \"" + resourceName + "\" from jar file.");
        }

        int readBytes;
        byte[] buffer = new byte[4096];
        OutputStream resStreamOut;
        
        final int firstIndexOfSeparator = resourceName.startsWith("/") ? 1 : 0;
        final int lastIndexOfSeparator = resourceName.lastIndexOf("/");
        final String parent = resourceName.substring(firstIndexOfSeparator, lastIndexOfSeparator);
        final java.io.File folder = new java.io.File(parent); // export to same location outside
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        final String name = resourceName.substring(lastIndexOfSeparator, resourceName.length());
        final java.io.File destination = new java.io.File(folder, name);
        try {
            resStreamOut = new java.io.FileOutputStream(destination);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (FileNotFoundException ex) {
            throw new IllegalArgumentException("Cannot find file destination: " + destination.getAbsolutePath() + ".", ex);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Cannot copy resource \"" + resourceName + "\" from jar file.", ex);
        }
        log.info("Classification script copied to {}.", destination.getAbsolutePath());
        return destination.getAbsolutePath();
    }
}
