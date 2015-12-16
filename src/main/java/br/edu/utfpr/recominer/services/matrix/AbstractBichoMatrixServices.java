package br.edu.utfpr.recominer.services.matrix;

import br.edu.utfpr.recominer.dao.BichoDAO;
import br.edu.utfpr.recominer.dao.BichoFileDAO;
import br.edu.utfpr.recominer.dao.BichoPairFileDAO;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.metric.associationrule.AssociationRulePerformanceCalculator;
import br.edu.utfpr.recominer.metric.associationrule.AssociationRuleExtractor;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.FilePairApriori;
import br.edu.utfpr.recominer.model.FilePairAprioriOutput;
import br.edu.utfpr.recominer.model.FilePairOutput;
import br.edu.utfpr.recominer.model.FilePath;
import br.edu.utfpr.recominer.model.FilterByApriori;
import br.edu.utfpr.recominer.model.Issue;
import br.edu.utfpr.recominer.model.associationrule.AssociationRule;
import br.edu.utfpr.recominer.model.associationrule.Transaction;
import br.edu.utfpr.recominer.model.matrix.EntityMatrix;
import br.edu.utfpr.recominer.model.matrix.EntityMatrixNode;
import br.edu.utfpr.recominer.services.AbstractBichoServices;
import br.edu.utfpr.recominer.services.metric.Cacher;
import br.edu.utfpr.recominer.util.OrderFilePairAprioriOutputByConfidence;
import br.edu.utfpr.recominer.util.OrderFilePairAprioriOutputByFile1Issues;
import br.edu.utfpr.recominer.util.OrderFilePairAprioriOutputByNumberOfDefects;
import br.edu.utfpr.recominer.util.OrderFilePairAprioriOutputBySupport;
import br.edu.utfpr.recominer.util.OutLog;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author douglas
 */
public abstract class AbstractBichoMatrixServices extends AbstractBichoServices {

    private final String repository;
    protected final List<EntityMatrix> matricesToSave;

    public AbstractBichoMatrixServices(GenericBichoDAO dao, OutLog out) {
        super(dao, out);
        this.repository = null;
        this.matricesToSave = null;
    }

    public AbstractBichoMatrixServices(GenericBichoDAO dao, GenericDao genericDao, String repository, List<EntityMatrix> matricesToSave, Map<Object, Object> params, OutLog out) {
        super(dao, genericDao, params, out);
        this.repository = repository;
        this.matricesToSave = matricesToSave;
    }

    public String getRepository() {
        return repository;
    }

    public void saveMatrix(EntityMatrix entityMatrix, Class<?> serviceClass) {
        out.printLog("Salvando matriz com " + entityMatrix.getNodes().size() + " registros. Parametros: " + entityMatrix.getParams());

        for (Map.Entry<Object, Object> entrySet : params.entrySet()) {
            Object key = entrySet.getKey();
            Object value = entrySet.getValue();

            if (!entityMatrix.getParams().containsKey(key)) {
                entityMatrix.getParams().put(key, value);
            }
        }
        if (entityMatrix.getRepository() == null) {
            entityMatrix.setRepository(getRepository());
        }
        entityMatrix.setClassServicesName(serviceClass.getName());
        entityMatrix.setLog(out.getLog().toString());
        for (EntityMatrixNode node : entityMatrix.getNodes()) {
            node.setMatrix(entityMatrix);
        }
        entityMatrix.setStoped(new Date());
        entityMatrix.setComplete(true);
        // saving in jgitminer database
        genericDao.insert(entityMatrix);

        out.printLog("\nSalvamento dos dados concluído!");
    }

    @Override
    public abstract void run();

    protected static List<EntityMatrixNode> objectsToNodes(Collection<? extends Object> list, String header) {
        List<EntityMatrixNode> nodes = new ArrayList<>();
        nodes.add(new EntityMatrixNode(header));
        for (Object value : list) {
            nodes.add(new EntityMatrixNode(value.toString()));
        }
        return nodes;
    }

    protected void pairFiles(Map<FilePair, FilePairAprioriOutput> pairFiles, List<FilePath> commitedFiles, Issue issue, Statistics statistics) {
        for (int i = 0; i < commitedFiles.size(); i++) {
            for (int j = i + 1; j < commitedFiles.size(); j++) {
                FilePath file1 = commitedFiles.get(i);
                FilePath file2 = commitedFiles.get(j);
                if (!file1.getFilePath().equals(file2.getFilePath())) {
                    FilePair filePair = new FilePair(file1.getFilePath(), file2.getFilePath());
                    FilePairAprioriOutput filePairOutput;

                    if (pairFiles.containsKey(filePair)) {
                        filePairOutput = pairFiles.get(filePair);
                    } else {
                        filePairOutput = new FilePairAprioriOutput(filePair);
                        pairFiles.put(filePair, filePairOutput);
                    }

                    filePairOutput.addIssueId(issue.getId());
                    filePairOutput.addIssue(issue);

                    if ("Bug".equals(issue.getType())) {
                        filePairOutput.addDefectIssueId(issue.getId());
                        statistics.getAllDefectIssues().add(issue.getId());
                    }

                    filePairOutput.addCommitId(file1.getCommitId());
                    filePairOutput.addCommitId(file2.getCommitId());
                    // TODO refactor, replace CommitId with Commit
                    final Commit commitFile1 = new Commit(file1.getCommitId(), null, null);
                    final Commit commitFile2 = new Commit(file2.getCommitId(), null, null);
                    filePairOutput.addCommit(commitFile1);
                    filePairOutput.addCommit(commitFile2);

                    filePairOutput.addCommitFile1Id(file1.getCommitId());
                    filePairOutput.addCommitFile2Id(file2.getCommitId());

                    statistics.getAllConsideredIssues().add(issue);
                    statistics.getAllConsideredCommits().add(file1.getCommitId());
                    statistics.getAllConsideredCommits().add(file2.getCommitId());

                    statistics.getAllConsideredFiles().add(file1.getFilePath());
                    statistics.getAllConsideredFiles().add(file2.getFilePath());

                    statistics.addFileCommit(file1, commitFile1);
                    statistics.addFileCommit(file2, commitFile2);

                    statistics.addFileIssue(file1, issue);
                    statistics.addFileIssue(file2, issue);
                }
            }
        }
    }

    protected void pairFilesWithFile(String filename, Map<FilePair, FilePairAprioriOutput> pairFiles, List<FilePath> commitedFiles, Issue issue, Statistics statistics) {
        for (int i = 0; i < commitedFiles.size(); i++) {
            FilePath file1 = commitedFiles.get(i);
            if (!file1.getFilePath().equals(filename)) {
                continue;
            }
            for (int j = 0; j < commitedFiles.size(); j++) {
                FilePath file2 = commitedFiles.get(j);
                if (!file1.getFilePath().equals(file2.getFilePath())) {
                    FilePair filePair = new FilePair(file1.getFilePath(), file2.getFilePath());
                    FilePairAprioriOutput filePairOutput;

                    if (pairFiles.containsKey(filePair)) {
                        filePairOutput = pairFiles.get(filePair);
                    } else {
                        filePairOutput = new FilePairAprioriOutput(filePair);
                        pairFiles.put(filePair, filePairOutput);
                    }

                    filePairOutput.addIssueId(issue.getId());
                    filePairOutput.addIssue(issue);

                    if ("Bug".equals(issue.getType())) {
                        filePairOutput.addDefectIssueId(issue.getId());
                        statistics.getAllDefectIssues().add(issue.getId());
                    }

                    filePairOutput.addCommitId(file1.getCommitId());
                    filePairOutput.addCommitId(file2.getCommitId());
                    // TODO refactor, replace CommitId with Commit
                    final Commit commitFile1 = new Commit(file1.getCommitId(), null, null);
                    final Commit commitFile2 = new Commit(file2.getCommitId(), null, null);
                    filePairOutput.addCommit(commitFile1);
                    filePairOutput.addCommit(commitFile2);

                    filePairOutput.addCommitFile1Id(file1.getCommitId());
                    filePairOutput.addCommitFile2Id(file2.getCommitId());

                    statistics.getAllConsideredIssues().add(issue);
                    statistics.getAllConsideredCommits().add(file1.getCommitId());
                    statistics.getAllConsideredCommits().add(file2.getCommitId());

                    statistics.getAllConsideredFiles().add(file1.getFilePath());
                    statistics.getAllConsideredFiles().add(file2.getFilePath());

                    statistics.addFileCommit(file1, commitFile1);
                    statistics.addFileCommit(file2, commitFile2);

                    statistics.addFileIssue(file1, issue);
                    statistics.addFileIssue(file2, issue);
                }
            }
        }
    }

    protected void pairFiles(Map<Integer, Set<FilePath>> commitedFilesByIndex, Map<FilePair, FilePairAprioriOutput> pairFiles, Issue issue, Set<Integer> allDefectIssues, Set<Integer> allConsideredCommits) {
        List<Integer> openIndexes = new ArrayList<>(commitedFilesByIndex.keySet());
        Collections.sort(openIndexes);
        for (int openIndex = 0; openIndex < openIndexes.size(); openIndex++) {
            final int nextOpenIndex = openIndex + 1;

            Set<FilePath> commitedFilesI = commitedFilesByIndex.get(openIndex);
            Set<FilePath> commitedFilesJ = commitedFilesByIndex.get(nextOpenIndex);

            if ((nextOpenIndex) >= openIndexes.size()
                    || commitedFilesI == null
                    || commitedFilesJ == null) {
                break;
            }

            pairFiles(commitedFilesI, commitedFilesJ, pairFiles, issue, allDefectIssues, allConsideredCommits);
        }
    }

    protected void pairFiles(Set<FilePath> commitedFilesI, Set<FilePath> commitedFilesJ, Map<FilePair, FilePairAprioriOutput> pairFiles, Issue issue, Set<Integer> allDefectIssues, Set<Integer> allConsideredCommits) {
        for (FilePath fileI : commitedFilesI) {
            for (FilePath fileJ : commitedFilesJ) {
                if (!fileI.getFilePath().equals(fileJ.getFilePath())) {
                    FilePair filePair = new FilePair(fileI.getFilePath(), fileJ.getFilePath());
                    FilePairAprioriOutput filePairOutput;

                    if (pairFiles.containsKey(filePair)) {
                        filePairOutput = pairFiles.get(filePair);
                    } else {
                        filePairOutput = new FilePairAprioriOutput(filePair);
                        pairFiles.put(filePair, filePairOutput);
                    }

                    filePairOutput.addIssueId(issue.getId());

                    if ("Bug".equals(issue.getType())) {
                        filePairOutput.addDefectIssueId(issue.getId());
                        allDefectIssues.add(issue.getId());
                    }

                    filePairOutput.addCommitId(fileI.getCommitId());
                    filePairOutput.addCommitId(fileJ.getCommitId());

                    filePairOutput.addCommitFile1Id(fileI.getCommitId());
                    filePairOutput.addCommitFile2Id(fileJ.getCommitId());

                    allConsideredCommits.add(fileI.getCommitId());
                    allConsideredCommits.add(fileJ.getCommitId());
                }
            }
        }
    }

    protected void countFutureIssues(Map<FilePair, FilePairAprioriOutput> pairFiles, BichoPairFileDAO bichoPairFileDAO, String futureVersion) {
        final Set<FilePair> keySet = pairFiles.keySet();
        final int total = keySet.size();
        int progressCountFutureDefects = 0;
        for (FilePair fileFile : keySet) {
            if (++progressCountFutureDefects % 100 == 0 || progressCountFutureDefects == total) {
                System.out.println(progressCountFutureDefects + "/" + total);
            }
            Map<String, Set<Integer>> futureIssues = bichoPairFileDAO.selectIssues(fileFile.getFile1().getFileName(), fileFile.getFile2().getFileName(), futureVersion);
            final FilePairOutput pairFile = pairFiles.get(fileFile);
            Set<Integer> bugs = futureIssues.get("Bug");
            if (bugs != null) {
                pairFile.addFutureDefectIssuesId(bugs);
            }
            for (Set<Integer> issue : futureIssues.values()) {
                pairFile.addFutureIssuesId(issue);
            }
        }
    }

    protected void log(String log) {
        try {
            FileWriter w = new FileWriter(new java.io.File(System.getProperty("user.home") + "\\statistics.txt"), true);
            w.append(log);
            w.flush();
            w.close();
        } catch (Exception e) {
            System.err.println("Error to write log. " + e.getMessage());
        }
    }

    protected String generateStatistics(List<FilePairAprioriOutput> pairFileList) {
        Set<Integer> totalIssues = new HashSet<>();
        Set<Integer> totalCommits = new HashSet<>();
        Set<Integer> totalCommitsFile1 = new HashSet<>();
        Set<Integer> totalCommitsFile2 = new HashSet<>();
        Set<Integer> totalDefects = new HashSet<>();
        Set<Integer> totalFutureDefects = new HashSet<>();

        Set<String> allFiles = new HashSet<>();
        Set<String> allJavaFiles = new HashSet<>();
        Set<String> allXmlFiles = new HashSet<>();
        Set<String> allOtherFiles = new HashSet<>();

        for (FilePairAprioriOutput node : pairFileList) {
            totalIssues.addAll(node.getIssuesId());
            totalCommits.addAll(node.getCommitsId());

            totalCommitsFile1.addAll(node.getCommitsFile1Id());
            totalCommitsFile2.addAll(node.getCommitsFile2Id());

            totalFutureDefects.addAll(node.getFutureDefectIssuesId());
            totalDefects.add(node.getCommitsIdWeight());

            final String file1 = node.getFile().getFileName();

            allFiles.add(file1);
            if (file1.endsWith(".java")) {
                allJavaFiles.add(file1);
            } else if (file1.endsWith(".xml")) {
                allXmlFiles.add(file1);
            } else {
                allOtherFiles.add(file1);
            }

            final String file2 = node.getFile2().getFileName();

            allFiles.add(file2);
            if (file2.endsWith(".java")) {
                allJavaFiles.add(file2);
            } else if (file2.endsWith(".xml")) {
                allXmlFiles.add(file2);
            } else {
                allOtherFiles.add(file2);
            }
        }

        FilePairAprioriOutput summary = new FilePairAprioriOutput(new FilePair("Summary", String.valueOf(allFiles.size())));
        summary.addIssueId(totalIssues.size());
        summary.addCommitId(totalCommits.size());
        summary.addCommitFile1Id(totalCommitsFile1.size());
        summary.addCommitFile2Id(totalCommitsFile2.size());
        summary.addDefectIssueId(totalDefects.size());
        summary.addFutureDefectIssuesId(totalFutureDefects.size());

        return "Number of files: " + allFiles.size() + "\n"
                + "Number of files (JAVA): " + allJavaFiles.size() + "\n"
                + "Number of files (XML): " + allXmlFiles.size() + "\n"
                + "Number of files (Others): " + allOtherFiles.size() + "\n"
                + "Number of commits file 1: " + totalCommitsFile1.size() + "\n"
                + "Number of commits file 2: " + totalCommitsFile2.size() + "\n"
                + "Number of issues: " + totalIssues.size() + "\n"
                + "Number of commits: " + totalCommits.size() + "\n"
                + "Number of defect issues: " + totalDefects.size() + "\n"
                + "Number of future defect issues: " + totalFutureDefects.size() + "\n\n";
    }

    public void identifyFilePairs(Map<FilePair, FilePairAprioriOutput> pairFiles, BichoDAO bichoDAO, String versionString, BichoFileDAO bichoFileDAO) {
        // select a issue/pullrequest commenters
        Map<Issue, List<Commit>> issuesConsideredCommits = bichoDAO.selectIssuesAndType(versionString);
        identifyFilePairs(versionString, pairFiles, issuesConsideredCommits, bichoFileDAO);
    }

    public void identifyFilePairs(Map<FilePair, FilePairAprioriOutput> pairFiles, BichoDAO bichoDAO, BichoFileDAO bichoFileDAO) {
        // select a issue/pullrequest commenters
        Map<Issue, List<Commit>> issuesConsideredCommits = bichoDAO.selectIssuesAndType();
        identifyFilePairs("All", pairFiles, issuesConsideredCommits, bichoFileDAO);
    }

    public Set<Transaction<String>> identifyAssociationRules(BichoDAO bichoDAO, BichoFileDAO bichoFileDAO) {
        // select a issue/pullrequest commenters
        Map<Issue, List<Commit>> issuesConsideredCommits = bichoDAO.selectIssuesAndType();
        return identifyAssociationRules("All", null, issuesConsideredCommits, bichoFileDAO);
    }

    public void identifyFilePairsLastNMonth(Map<FilePair, FilePairAprioriOutput> pairFiles, BichoDAO bichoDAO, BichoFileDAO bichoFileDAO, int month) {
        // select a issue/pullrequest commenters
        Map<Issue, List<Commit>> issuesConsideredCommits = bichoDAO.selectIssuesAndTypeLastNMonth(month);
        identifyFilePairs("All", pairFiles, issuesConsideredCommits, bichoFileDAO);
    }

    public void identifyFilePairsLastNIssues(Map<FilePair, FilePairAprioriOutput> pairFiles, BichoDAO bichoDAO, BichoFileDAO bichoFileDAO, int quantity) {
        // select a issue/pullrequest commenters
        Map<Issue, List<Commit>> issuesConsideredCommits = bichoDAO.selectIssuesAndTypeLastNIssues(quantity);
        identifyFilePairs("All", pairFiles, issuesConsideredCommits, bichoFileDAO);
    }

    public Map<FilePair, FilePairAprioriOutput> identifyFilePairs(String version, Map<FilePair, FilePairAprioriOutput> pairFiles, Map<Issue, List<Commit>> issuesConsideredCommits, BichoFileDAO bichoFileDAO) {
        return identifyFilePairs(version, null, pairFiles, issuesConsideredCommits, bichoFileDAO);
    }

    public Map<FilePair, FilePairAprioriOutput> identifyFilePairs(String version, Statistics statistics, Map<FilePair, FilePairAprioriOutput> pairFiles, Map<Issue, List<Commit>> issuesConsideredCommits, BichoFileDAO bichoFileDAO) {
        Cacher cacher = new Cacher(bichoFileDAO);
        Map<FilterByApriori, Set<FilePair>> filePairPerFilter = new LinkedHashMap<>();
        for (FilterByApriori aprioriFilter : FilterByApriori.getFiltersForExperiment1()) {
            filePairPerFilter.put(aprioriFilter, new HashSet<>());
        }

        if (statistics == null) {
            statistics = new Statistics();
        }
        if (issuesConsideredCommits.isEmpty()) {
            return pairFiles;
        }

        out.printLog("Version: " + version);
        out.printLog("Issues (filtered): " + issuesConsideredCommits.size());
        int count = 1;
        // combina em pares todos os arquivos commitados em uma issue
        final int totalIssues = issuesConsideredCommits.keySet().size();
        int progressFilePairing = 0;
        for (Map.Entry<Issue, List<Commit>> entrySet : issuesConsideredCommits.entrySet()) {
            if (++progressFilePairing % 100 == 0 || progressFilePairing == totalIssues) {
                System.out.println(progressFilePairing + "/" + totalIssues);
            }
            Issue issue = entrySet.getKey();
            List<Commit> commits = entrySet.getValue();

            statistics.getAllIssues().add(issue);
            statistics.getAllCommits().addAll(commits);

            out.printLog("Issue #" + issue);
            out.printLog(count++ + " of the " + issuesConsideredCommits.size());
            out.printLog(commits.size() + " commits references the issue");
            List<FilePath> commitedFiles = filterAndAggregateAllFileOfIssue(commits, bichoFileDAO,
                    statistics);

            // empty
            if (commitedFiles.isEmpty()) {
                out.printLog("No file commited for issue #" + issue);
                statistics.getExcludedCommits().addAll(commits);
                statistics.getExcludedIssues().add(issue);
                continue;
            } else if (commitedFiles.size() == 1) {
                out.printLog("Only one file commited in one commit for issue #" + issue);
                statistics.getExcludedCommits().addAll(commits);
                statistics.getExcludedIssues().add(issue);
                continue;
            } else {
                final Map<String, Long> collected = commitedFiles.stream().collect(Collectors.groupingBy(c -> c.getFilePath(), Collectors.counting()));
                if (collected.size() == 1) {
                    out.printLog("One file only commited in many commits for issue #" + issue);
                    statistics.getExcludedCommits().addAll(commits);
                    statistics.getExcludedIssues().add(issue);
                    continue;
                }
            }
            out.printLog("Number of files commited and related with issue: " + commitedFiles.size());
            pairFiles(pairFiles, commitedFiles, issue, statistics);
        }

        Set<Integer> allConsideredIssues = new HashSet<>();
        for (FilePairAprioriOutput filePairAprioriOutput : pairFiles.values()) {
            allConsideredIssues.addAll(filePairAprioriOutput.getIssuesId());
        }

        calculeApriori(pairFiles, cacher, issuesConsideredCommits.keySet(), allConsideredIssues);

        for (Map.Entry<FilePair, FilePairAprioriOutput> entrySet : pairFiles.entrySet()) {
            FilePair filePair = entrySet.getKey();
            FilePairAprioriOutput apriori = entrySet.getValue();

            for (FilterByApriori filter : FilterByApriori.getFiltersForExperiment1()) {
                if (apriori.getFilePairApriori().fits(filter)) {
                    filePairPerFilter.get(filter).add(filePair);
                }
            }
        }

        statistics.getPairFiles().addAll(pairFiles.keySet());

        // amount of files that will be analysed
        out.printLog("Java files: " + statistics.getAllJavaFiles().size());
        out.printLog("XML files: " + statistics.getAllXmlFiles().size());
        out.printLog("Issues with one file: " + statistics.getExcludedIssues().size());//java.util.Arrays.toString(statistics.getExcludedIssues().toArray());

        try {
//            final File outputFile = new File("C:/Users/a562273/Desktop/" + bichoFileDAO.getRepository() + "_summary.csv");
            final File outputFile = new File("C:/Users/a562273/Desktop/summary.csv");
            final boolean exists = outputFile.exists();
            try (FileWriter fw = new FileWriter(outputFile, true)) {
                if (!exists) {
//                    fw.append("project;versionOrGroup;issues;excludedIssues;commits;excludedCommits;co-changes;files\r\n");
                    fw.append("project;data;# total issues;total issues;# issues;issues;commits;co-changes;files");
                    for (Map.Entry<FilterByApriori, Set<FilePair>> entrySet : filePairPerFilter.entrySet()) {
                        FilterByApriori key = entrySet.getKey();
                        fw.append(";").append(key.toString());
                    }
                    fw.append("\r\n");
                }
                fw.append(bichoFileDAO.getRepository()).append(";").append(version).append(";")
                        .append(String.valueOf(statistics.getAllIssues().size())).append(";")
                        .append(Arrays.toString(statistics.getAllIssues().toArray()).replace("[", "").replace("]", "")).append(";")
                        .append(String.valueOf(statistics.getAllConsideredIssues().size())).append(";")
                        .append(Arrays.toString(statistics.getAllConsideredIssues().toArray()).replace("[", "").replace("]", "")).append(";")
                        //.append(String.valueOf(excludedIssues.size())).append(";")
                        .append(String.valueOf(statistics.getAllConsideredCommits().size())).append(";")
                        //.append(String.valueOf(excludedCommits.size())).append(";")
                        .append(String.valueOf(pairFiles.size())).append(";")
                        .append(String.valueOf(statistics.getAllConsideredFiles().size()));
                for (Map.Entry<FilterByApriori, Set<FilePair>> entrySet : filePairPerFilter.entrySet()) {
                    Set<FilePair> value = entrySet.getValue();

                    fw.append(";").append(String.valueOf(value.size()));
                }

                fw.append("\r\n");
                        fw.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

//        try {
//            try (FileWriter fw = new FileWriter(new File("C:/Users/a562273/Desktop/" + bichoFileDAO.getRepository() + "_files.csv"))) {
//                fw.append("file;issues;commits\r\n");
//                for (String file : statistics.getAllJavaFiles()) {
//                    if (statistics.getFilesIssues().get(file) != null) {
//                        fw.append(file).append(";")
//                                .append(String.valueOf(statistics.getFilesIssues().get(file).size())).append(";")
//                                .append(String.valueOf(statistics.getFilesCommits().get(file).size()))
//                                .append("\r\n");
//                    }
//                }
//                for (String file : statistics.getAllXmlFiles()) {
//                    if (statistics.getFilesIssues().get(file) != null) {
//                        fw.append(file).append(";")
//                                .append(String.valueOf(statistics.getFilesIssues().get(file).size())).append(";")
//                                .append(String.valueOf(statistics.getFilesCommits().get(file).size()))
//                                .append("\r\n");
//                    }
//                }
//                fw.flush();
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        EntityMatrix matrixSummary = new EntityMatrix();
//        matrixSummary.setNodes(objectsToNodes(pairFiles.entrySet(), FilePairAprioriOutput.getToStringHeader()));
//        matrixSummary.setRepository(bichoFileDAO.getRepository());
//        matrixSummary.getParams().put("filename", "summary " + version);
////        matrixSummary.getParams().put("minOccurrencesInVersion", minOccurrencesInVersion);
//
//        saveMatrix(matrixSummary, getClass());

        return pairFiles;
    }

    public Set<Transaction<String>> identifyAssociationRules(String version, Statistics statistics, Map<Issue, List<Commit>> issuesConsideredCommits, BichoFileDAO bichoFileDAO) {
        Cacher cacher = new Cacher(bichoFileDAO);
        Map<FilterByApriori, Set<FilePair>> filePairPerFilter = new LinkedHashMap<>();
        for (FilterByApriori aprioriFilter : FilterByApriori.getFiltersForExperiment1()) {
            filePairPerFilter.put(aprioriFilter, new HashSet<>());
        }

        if (statistics == null) {
            statistics = new Statistics();
        }
        if (issuesConsideredCommits.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        out.printLog("Version: " + version);
        out.printLog("Issues (filtered): " + issuesConsideredCommits.size());
        int count = 1;
        // combina em pares todos os arquivos commitados em uma issue
        final int totalIssues = issuesConsideredCommits.keySet().size();
        int progressFilePairing = 0;
        final Set<Transaction<String>> transactions = new LinkedHashSet<>();
        final Set<Integer> allConsideredIssues = new HashSet<>();
        for (Map.Entry<Issue, List<Commit>> entrySet : issuesConsideredCommits.entrySet()) {
            if (++progressFilePairing % 100 == 0 || progressFilePairing == totalIssues) {
                System.out.println(progressFilePairing + "/" + totalIssues);
            }
            Issue issue = entrySet.getKey();
            List<Commit> commits = entrySet.getValue();

            statistics.getAllIssues().add(issue);
            statistics.getAllCommits().addAll(commits);

            out.printLog("Issue #" + issue);
            out.printLog(count++ + " of the " + issuesConsideredCommits.size());
            out.printLog(commits.size() + " commits references the issue");
            List<FilePath> commitedFiles = filterAndAggregateAllFileOfIssue(commits, bichoFileDAO,
                    statistics);

            // empty
            if (commitedFiles.isEmpty()) {
                out.printLog("No file commited for issue #" + issue);
                statistics.getExcludedCommits().addAll(commits);
                statistics.getExcludedIssues().add(issue);
                continue;
            } else if (commitedFiles.size() == 1) {
                out.printLog("Only one file commited in one commit for issue #" + issue);
                statistics.getExcludedCommits().addAll(commits);
                statistics.getExcludedIssues().add(issue);
                continue;
            } else {
                final Map<String, Long> collected = commitedFiles.stream().collect(Collectors.groupingBy(c -> c.getFilePath(), Collectors.counting()));
                if (collected.size() == 1) {
                    out.printLog("One file only commited in many commits for issue #" + issue);
                    statistics.getExcludedCommits().addAll(commits);
                    statistics.getExcludedIssues().add(issue);
                    continue;
                }
            }
            out.printLog("Number of files commited and related with issue: " + commitedFiles.size());
            transactions.add(extractTransaction(commitedFiles, issue, statistics));
            allConsideredIssues.add(issue.getId());
        }

        AssociationRuleExtractor<String> extractor = new AssociationRuleExtractor<>(transactions);
        Set<AssociationRule<String>> navigationRules = extractor.extractNavigationRules();
        Set<AssociationRule<String>> preventionRules = extractor.extractPreventionRules();
        Set<AssociationRule<String>> closureRules = extractor.extractClosureRules();
        AssociationRulePerformanceCalculator<String> calculatorNavigation = new AssociationRulePerformanceCalculator<>(transactions, navigationRules);
        AssociationRulePerformanceCalculator<String> calculatorPrevention = new AssociationRulePerformanceCalculator<>(transactions, preventionRules);
        AssociationRulePerformanceCalculator<String> calculatorClosure = new AssociationRulePerformanceCalculator<>(transactions, closureRules);

        calculatorNavigation.calculePrecision(3);
        calculatorPrevention.calculePrecision(3);

//         calculeApriori(transactions, cacher, issuesConsideredCommits.keySet(), allConsideredIssues);

//        for (Map.Entry<FilePair, FilePairAprioriOutput> entrySet : pairFiles.entrySet()) {
//            FilePair filePair = entrySet.getKey();
//            FilePairAprioriOutput apriori = entrySet.getValue();
//
//            for (FilterByApriori filter : FilterByApriori.getFiltersForExperiment1()) {
//                if (apriori.getFilePairApriori().fits(filter)) {
//                    filePairPerFilter.get(filter).add(filePair);
//                }
//            }
//        }

        // amount of files that will be analysed
        out.printLog("Java files: " + statistics.getAllJavaFiles().size());
        out.printLog("XML files: " + statistics.getAllXmlFiles().size());
        out.printLog("Issues with one file: " + statistics.getExcludedIssues().size());//java.util.Arrays.toString(statistics.getExcludedIssues().toArray());

        try {
//            final File outputFile = new File("C:/Users/a562273/Desktop/" + bichoFileDAO.getRepository() + "_summary.csv");
            final File outputFile = new File("C:/Users/a562273/Desktop/summary.csv");
            final boolean exists = outputFile.exists();
            try (FileWriter fw = new FileWriter(outputFile, true)) {
                if (!exists) {
//                    fw.append("project;versionOrGroup;issues;excludedIssues;commits;excludedCommits;co-changes;files\r\n");
                    fw.append("project;data;# total issues;total issues;# issues;issues;commits;co-changes;files");
                    for (Map.Entry<FilterByApriori, Set<FilePair>> entrySet : filePairPerFilter.entrySet()) {
                        FilterByApriori key = entrySet.getKey();
                        fw.append(";").append(key.toString());
                    }
                    fw.append("\r\n");
                }
                fw.append(bichoFileDAO.getRepository()).append(";").append(version).append(";")
                        .append(String.valueOf(statistics.getAllIssues().size())).append(";")
                        .append(Arrays.toString(statistics.getAllIssues().toArray()).replace("[", "").replace("]", "")).append(";")
                        .append(String.valueOf(statistics.getAllConsideredIssues().size())).append(";")
                        .append(Arrays.toString(statistics.getAllConsideredIssues().toArray()).replace("[", "").replace("]", "")).append(";")
                        //.append(String.valueOf(excludedIssues.size())).append(";")
                        .append(String.valueOf(statistics.getAllConsideredCommits().size())).append(";")
                        //.append(String.valueOf(excludedCommits.size())).append(";")
                        .append(String.valueOf(transactions.size())).append(";")
                        .append(String.valueOf(statistics.getAllConsideredFiles().size()));
                for (Map.Entry<FilterByApriori, Set<FilePair>> entrySet : filePairPerFilter.entrySet()) {
                    Set<FilePair> value = entrySet.getValue();

                    fw.append(";").append(String.valueOf(value.size()));
                }

                fw.append("\r\n");
                fw.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return transactions;
    }

    public Map<FilePair, FilePairAprioriOutput> identifyFilePairsByFile(String version, Map<FilePair, FilePairAprioriOutput> pairFiles, Map<String, Map<Issue, List<Commit>>> filesIssues, BichoFileDAO bichoFileDAO, int numberOfIssues) {
        return identifyFilePairsByFile(version, null, pairFiles, filesIssues, bichoFileDAO, numberOfIssues);
    }

    public void identifyFilePairsFromTopFilesInLastIssues(Map<FilePair, FilePairAprioriOutput> pairFiles, BichoDAO bichoDAO, BichoFileDAO bichoFileDAO, int numberOfLastIssues, int topPercent) {
        final Map<String, Long> topChangedFiles = bichoDAO.selectFilesNumberOfChangesInLastIssues(numberOfLastIssues);

        saveTopFiles(topChangedFiles, numberOfLastIssues, topPercent);

        final Map<String, Map<Issue, List<Commit>>> topChangedFilesInLastIssues = bichoDAO.selectTopChangedFilesInLastIssues(topChangedFiles, numberOfLastIssues, topPercent);
        identifyFilePairsByFile("All", pairFiles, topChangedFilesInLastIssues, bichoFileDAO, numberOfLastIssues);
    }

    private void saveTopFiles(final Map<String, Long> topChangedFiles, int numberOfLastIssues, int topPercent) {
        EntityMatrix entityMatrix = new EntityMatrix();
        Map<Object, Object> topFileParams = new LinkedHashMap<>();
        topFileParams.put("filename", "top " + topPercent + " percent files in last " + numberOfLastIssues + " fixed issues");
        topFileParams.put("numberOfLastIssuesFixed", numberOfLastIssues);
        topFileParams.put("percentOfTopFiles", topPercent);
        entityMatrix.addAllParams(topFileParams);
        List<EntityMatrixNode> nodes = new ArrayList<>(topChangedFiles.size());
        nodes.add(new EntityMatrixNode("file;issues"));
        for (Map.Entry<String, Long> entry : topChangedFiles.entrySet()) {
            nodes.add(new EntityMatrixNode(entry.getKey() + ";" + entry.getValue()));
        }
        entityMatrix.setNodes(nodes);
        saveMatrix(entityMatrix, BichoPairOfFileAprioriServices.class);
    }

    public Map<FilePair, FilePairAprioriOutput> identifyFilePairsByFile(String version, Statistics statistics, Map<FilePair, FilePairAprioriOutput> pairFiles, Map<String, Map<Issue, List<Commit>>> filesIssues, BichoFileDAO bichoFileDAO, int numberOfIssues) {
        Cacher cacher = new Cacher(bichoFileDAO);
        Map<FilterByApriori, Set<FilePair>> filePairPerFilter = new LinkedHashMap<>();
        for (FilterByApriori aprioriFilter : FilterByApriori.getFiltersForExperiment1()) {
            filePairPerFilter.put(aprioriFilter, new HashSet<>());
        }

        if (statistics == null) {
            statistics = new Statistics();
        }
        if (filesIssues.isEmpty()) {
            return pairFiles;
        }

        out.printLog("Version: " + version);
        out.printLog("Issues (filtered): " + filesIssues.size());
        int count = 1;
        // combina em pares todos os arquivos commitados em uma issue
        final int totalIssues = filesIssues.keySet().size();

        int progressFilePairing = 0;
        for (Map.Entry<String, Map<Issue, List<Commit>>> fileIssuesCommits : filesIssues.entrySet()) {
            String filename = fileIssuesCommits.getKey();
            Map<Issue, List<Commit>> issuesConsideredCommits = fileIssuesCommits.getValue();

            for (Map.Entry<Issue, List<Commit>> entrySet : issuesConsideredCommits.entrySet()) {
                if (++progressFilePairing % 100 == 0 || progressFilePairing == totalIssues) {
                    System.out.println("...." + progressFilePairing + "/" + totalIssues);
                }
                Issue issue = entrySet.getKey();
                List<Commit> commits = entrySet.getValue();

                statistics.getAllIssues().add(issue);
                statistics.getAllCommits().addAll(commits);

                out.printLog("Issue #" + issue);
                out.printLog(count++ + " of the " + issuesConsideredCommits.size());
                out.printLog(commits.size() + " commits references the issue");
                List<FilePath> commitedFiles = filterAndAggregateAllFileOfIssue(commits, bichoFileDAO, statistics);

                // empty
                if (commitedFiles.isEmpty()) {
                    out.printLog("No file commited for issue #" + issue);
                    statistics.getExcludedCommits().addAll(commits);
                    statistics.getExcludedIssues().add(issue);
                    continue;
                } else if (commitedFiles.size() == 1) {
                    out.printLog("Only one file commited in one commit for issue #" + issue);
                    statistics.getExcludedCommits().addAll(commits);
                    statistics.getExcludedIssues().add(issue);
                    continue;
                } else {
                    final Map<String, Long> collected = commitedFiles.stream().collect(Collectors.groupingBy(c -> c.getFilePath(), Collectors.counting()));
                    if (collected.size() == 1) {
                        out.printLog("One file only commited in many commits for issue #" + issue);
                        statistics.getExcludedCommits().addAll(commits);
                        statistics.getExcludedIssues().add(issue);
                        continue;
                    }
                }
                out.printLog("Number of files commited and related with issue: " + commitedFiles.size());
                System.out.println("");
                pairFilesWithFile(filename, pairFiles, commitedFiles, issue, statistics);
            }
        }

        Set<Integer> allConsideredIssues = new HashSet<>();
        for (FilePairAprioriOutput filePairAprioriOutput : pairFiles.values()) {
            allConsideredIssues.addAll(filePairAprioriOutput.getIssuesId());
        }

        calculeApriori(pairFiles, cacher, filesIssues, numberOfIssues);

        for (Map.Entry<FilePair, FilePairAprioriOutput> entrySet : pairFiles.entrySet()) {
            FilePair filePair = entrySet.getKey();
            FilePairAprioriOutput apriori = entrySet.getValue();

            for (FilterByApriori filter : FilterByApriori.getFiltersForExperiment1()) {
                if (apriori.getFilePairApriori().fits(filter)) {
                    filePairPerFilter.get(filter).add(filePair);
                }
            }
        }

        statistics.getPairFiles().addAll(pairFiles.keySet());

        // amount of files that will be analysed
        out.printLog("Java files: " + statistics.getAllJavaFiles().size());
        out.printLog("XML files: " + statistics.getAllXmlFiles().size());
        out.printLog("Issues with one file: " + statistics.getExcludedIssues().size());//java.util.Arrays.toString(statistics.getExcludedIssues().toArray());

        try {
//            final File outputFile = new File("C:/Users/a562273/Desktop/" + bichoFileDAO.getRepository() + "_summary.csv");
            final File outputFile = new File("C:/Users/a562273/Desktop/summary.csv");
            final boolean exists = outputFile.exists();
            try (FileWriter fw = new FileWriter(outputFile, true)) {
                if (!exists) {
//                    fw.append("project;versionOrGroup;issues;excludedIssues;commits;excludedCommits;co-changes;files\r\n");
                    fw.append("project;data;# total issues;total issues;# issues;issues;commits;co-changes;files");
                    for (Map.Entry<FilterByApriori, Set<FilePair>> entrySet : filePairPerFilter.entrySet()) {
                        FilterByApriori key = entrySet.getKey();
                        fw.append(";").append(key.toString());
                    }
                    fw.append("\r\n");
                }
                fw.append(bichoFileDAO.getRepository()).append(";").append(version).append(";")
                        .append(String.valueOf(statistics.getAllIssues().size())).append(";")
                        .append(Arrays.toString(statistics.getAllIssues().toArray()).replace("[", "").replace("]", "")).append(";")
                        .append(String.valueOf(statistics.getAllConsideredIssues().size())).append(";")
                        .append(Arrays.toString(statistics.getAllConsideredIssues().toArray()).replace("[", "").replace("]", "")).append(";")
                        //.append(String.valueOf(excludedIssues.size())).append(";")
                        .append(String.valueOf(statistics.getAllConsideredCommits().size())).append(";")
                        //.append(String.valueOf(excludedCommits.size())).append(";")
                        .append(String.valueOf(pairFiles.size())).append(";")
                        .append(String.valueOf(statistics.getAllConsideredFiles().size()));
                for (Map.Entry<FilterByApriori, Set<FilePair>> entrySet : filePairPerFilter.entrySet()) {
                    Set<FilePair> value = entrySet.getValue();

                    fw.append(";").append(String.valueOf(value.size()));
                }

                fw.append("\r\n");
                fw.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return pairFiles;
    }

    protected List<FilePath> filterAndAggregateAllFileOfIssue(List<Commit> commits, BichoFileDAO bichoFileDAO, Statistics statistics) {
        final Set<String> allFiles = statistics.getAllFiles();
        final Set<String> allTestJavaFiles = statistics.getAllTestJavaFiles();
        final Set<String> allFilteredFiles = statistics.getAllFilteredFiles();
        final Set<String> allJavaFiles = statistics.getAllJavaFiles();
        final Set<String> allXmlFiles = statistics.getAllXmlFiles();

        // monta os pares com os arquivos de todos os commits da issue
        List<FilePath> commitedFiles = new ArrayList<>();
        for (Commit commit : commits) {
            // select name of commited files
            List<FilePath> files = bichoFileDAO.selectFilesByCommitId(commit.getId());
            out.printLog(files.size() + " files in commit #" + commit.getId());
            for (FilePath file : files) {
                allFiles.add(file.getFilePath());
                if (file.getFilePath().endsWith("Test.java") || file.getFilePath().toLowerCase().endsWith("_test.java")) {
                    allTestJavaFiles.add(file.getFilePath());
                    allFilteredFiles.add(file.getFilePath());
                } else if (!file.getFilePath().endsWith(".java") && !file.getFilePath().endsWith(".xml")) {
                    allFilteredFiles.add(file.getFilePath());
                } else {
                    if (file.getFilePath().endsWith(".java")) {
                        allJavaFiles.add(file.getFilePath());
                    } else if (file.getFilePath().endsWith(".xml")) {
                        allXmlFiles.add(file.getFilePath());
                    }
                    commitedFiles.add(file);
                }
            }
        }
        return commitedFiles;
    }

    public void calculeApriori(final Map<FilePair, FilePairAprioriOutput> pairFiles, final Cacher cacher, final Set<Issue> issues, Set<Integer> allConsideredIssues) {
        System.out.println("Issues: " + issues.size());
        System.out.println("Considered issues: " + allConsideredIssues.size());
        int totalApriori = pairFiles.size();
        int countApriori = 0;
        for (FilePair fileFile : pairFiles.keySet()) {
            if (++countApriori % 100 == 0
                    || countApriori == totalApriori) {
                System.out.println(countApriori + "/" + totalApriori);
            }

            Long file1Issues = cacher.calculeNumberOfIssues(fileFile.getFile1(), issues);
            Long file2Issues = cacher.calculeNumberOfIssues(fileFile.getFile2(), issues);

            FilePairAprioriOutput filePairOutput = pairFiles.get(fileFile);

            FilePairApriori apriori = new FilePairApriori(fileFile, file1Issues, file2Issues,
                    filePairOutput.getIssuesIdWeight(), allConsideredIssues.size());

            filePairOutput.setFilePairApriori(apriori);
        }
    }

    public void calculeApriori(final Map<FilePair, FilePairAprioriOutput> pairFiles, final Cacher cacher, final Map<String, Map<Issue, List<Commit>>> filesIssues, int numberOfIssuesAnalysed) {
        System.out.println("Considered issues: " + numberOfIssuesAnalysed);
      int totalApriori = pairFiles.size();
      int countApriori = 0;
        for (FilePair fileFile : pairFiles.keySet()) {
            if (++countApriori % 100 == 0
                    || countApriori == totalApriori) {
                System.out.println(countApriori + "/" + totalApriori);
            }

            Long file1Issues = (long) cacher.calculeNumberOfIssuesInLastIssues(fileFile.getFile1().getFileName(), numberOfIssuesAnalysed);
            //filesIssues.get(fileFile.getFile1().getFileName()).size();
            Long file2Issues = (long) cacher.calculeNumberOfIssuesInLastIssues(fileFile.getFile2().getFileName(), numberOfIssuesAnalysed);
            // filesIssues.get(fileFile.getFile2().getFileName()).size();

            FilePairAprioriOutput filePairOutput = pairFiles.get(fileFile);

            FilePairApriori apriori = new FilePairApriori(fileFile, file1Issues, file2Issues,
                    filePairOutput.getIssuesIdWeight(), numberOfIssuesAnalysed);

            filePairOutput.setFilePairApriori(apriori);
        }
    }

    protected void orderByFilePairSupportAndConfidence(final List<FilePairAprioriOutput> pairFileList) {
        orderByFilePairSupport(pairFileList); // lower priority
        orderByFilePairConfidence(pairFileList); // higher priority
    }

    protected void orderByFilePairConfidenceAndSupport(final List<FilePairAprioriOutput> pairFileList) {
        orderByFilePairConfidence(pairFileList); // lower priority
        orderByFilePairSupport(pairFileList); // higher priority
    }

    protected void orderByFile1IssuesAndFilePairConfidenceAndSupport(final List<FilePairAprioriOutput> pairFileList) {
        orderByFilePairConfidence(pairFileList); // lower priority
        orderByFilePairSupport(pairFileList);
        orderByFile1Issues(pairFileList); // higher priority
    }

    protected void orderByFilePairSupportAndNumberOfDefects(final List<FilePairAprioriOutput> pairFileList) {
        orderByNumberOfDefects(pairFileList); // lower priority
        orderByFilePairSupport(pairFileList); // higher priority
    }

    protected void orderByNumberOfDefects(final List<FilePairAprioriOutput> pairFileList) {
        Collections.sort(pairFileList, new OrderFilePairAprioriOutputByNumberOfDefects());
    }

    protected void orderByFilePairSupport(final List<FilePairAprioriOutput> pairFileList) {
        Collections.sort(pairFileList, new OrderFilePairAprioriOutputBySupport());
    }

    protected void orderByFilePairConfidence(final List<FilePairAprioriOutput> pairFileList) {
        Collections.sort(pairFileList, new OrderFilePairAprioriOutputByConfidence());
    }

    private void orderByFile1Issues(List<FilePairAprioriOutput> pairFileList) {
        Collections.sort(pairFileList, new OrderFilePairAprioriOutputByFile1Issues());
    }

    private Transaction<String> extractTransaction(List<FilePath> commitedFiles, Issue issue, Statistics statistics) {
        return new Transaction<>(issue.getId().longValue(), commitedFiles.stream().map(f -> f.getFilePath()).collect(Collectors.toSet()));
    }
}
