package br.edu.utfpr.recominer.services.matrix;

import br.edu.utfpr.recominer.dao.BichoDAO;
import br.edu.utfpr.recominer.dao.BichoFileDAO;
import br.edu.utfpr.recominer.dao.BichoPairFileDAO;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.FilePairAprioriOutput;
import br.edu.utfpr.recominer.model.FilterByApriori;
import br.edu.utfpr.recominer.model.FilterFilePairByReleaseOcurrence;
import br.edu.utfpr.recominer.model.Issue;
import br.edu.utfpr.recominer.model.Project;
import br.edu.utfpr.recominer.model.ProjectFilePairReleaseOcurrence;
import br.edu.utfpr.recominer.model.Version;
import br.edu.utfpr.recominer.model.matrix.EntityMatrix;
import br.edu.utfpr.recominer.services.metric.Cacher;
import br.edu.utfpr.recominer.util.OutLog;
import br.edu.utfpr.recominer.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Rodrigo Kuroda
 */
public class BichoPairOfFileGroupingByNumberOfIssuesServices extends AbstractBichoMatrixServices {

    public BichoPairOfFileGroupingByNumberOfIssuesServices() {
        super(null, null);
    }

    public BichoPairOfFileGroupingByNumberOfIssuesServices(GenericBichoDAO dao, OutLog out) {
        super(dao, out);
    }

    public BichoPairOfFileGroupingByNumberOfIssuesServices(GenericBichoDAO dao, GenericDao genericDao, String repository, List<EntityMatrix> matricesToSave, Map<Object, Object> params, OutLog out) {
        super(dao, genericDao, repository, matricesToSave, params, out);
    }

    private Integer getMaxFilesPerCommit() {
        return Util.stringToInteger(params.get("maxFilesPerCommit") + "");
    }

    private Integer getMinFilesPerCommit() {
        return Util.stringToInteger(params.get("minFilesPerCommit") + "");
    }

    private boolean isOnlyFixed() {
        return "true".equalsIgnoreCase(params.get("mergedOnly") + "");
    }

    public List<String> getFilesToIgnore() {
        return getStringLinesParam("filesToIgnore", true, false);
    }

    public List<String> getFilesToConsiders() {
        return getStringLinesParam("filesToConsiders", true, false);
    }

    public Integer getQuantity() {
        return getIntegerParam("quantity");
    }

    public Integer getGroupsQuantity() {
        return getIntegerParam("groupsQuantity");
    }

    public Integer getFutureQuantity() {
        return getIntegerParam("futureQuantity");
    }

    @Override
    public void run() {
        System.out.println(params);

        if (getRepository() == null) {
            throw new IllegalArgumentException("Parameter repository must be informed.");
        }

        BichoDAO bichoDAO = new BichoDAO(dao, getRepository(), getMaxFilesPerCommit());
        BichoFileDAO bichoFileDAO = new BichoFileDAO(dao, getRepository(), getMaxFilesPerCommit());
        BichoPairFileDAO bichoPairFileDAO = new BichoPairFileDAO(dao, getRepository(), getMaxFilesPerCommit());

        out.printLog("Maximum files per commit: " + getMaxFilesPerCommit());
        out.printLog("Minimum files per commit: " + getMinFilesPerCommit());

        final int quantity;
        final long totalIssues;
        if (getGroupsQuantity() != null && getGroupsQuantity() > 0) {
            totalIssues = bichoDAO.calculeNumberOfAllFixedIssues();
            quantity = Double.valueOf(Math.ceil(totalIssues / getGroupsQuantity().doubleValue())).intValue();
            out.printLog("Total issues: " + totalIssues);
            out.printLog("Quantity per group (" + getGroupsQuantity() + "): " + quantity);
        } else if (getQuantity() != null && getQuantity() > 0) {
            quantity = getQuantity();
            totalIssues = quantity;
        } else {
            throw new IllegalArgumentException("Parameter quantity or group quantity is required.");
        }

        // select a issue/pullrequest commenters
        final List<Map<Issue, List<Commit>>> subdividedIssuesCommits = bichoDAO.selectAllIssuesAndTypeSubdividedBy(quantity);

        StringBuilder sb = new StringBuilder();
        for (Map<Issue, List<Commit>> subdividedIssuesCommit : subdividedIssuesCommits) {
            for (Issue issue : subdividedIssuesCommit.keySet()) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(issue.getId());
            }
            sb.append("\r\n");
        }
        System.out.println(sb.toString());
        out.printLog("Issues (filtered): " + subdividedIssuesCommits.size());

        final Cacher cacher = new Cacher(bichoFileDAO);

        // Mapping indexes with encapsulated indexes for perfomance
        final Map<Integer, Version> indexesMap = new HashMap<>(subdividedIssuesCommits.size());
        // Using version to encapsulate version and reuse the functionalities provided for version
        final List<Version> indexes = new ArrayList<>(subdividedIssuesCommits.size());
        for (int i = 0; i < subdividedIssuesCommits.size() - 1; i++) {
            final Version version = new Version(String.valueOf(i));
            indexes.add(version);
            indexesMap.put(i, version);
        }

        final Set<FilterByApriori> filtersForExperiment1 = FilterByApriori.getFiltersForExperiment1();
        final Map<FilterByApriori, Set<ProjectFilePairReleaseOcurrence>> releaseOccurrencesMap = new LinkedHashMap<>();
        final Set<ProjectFilePairReleaseOcurrence> summaries = new LinkedHashSet<>();

        for (FilterByApriori aprioriFilter : filtersForExperiment1) {
            releaseOccurrencesMap.put(aprioriFilter, new HashSet<>());

        }

        // TODO parametrizar
        // TODO confirmar/verificar de acordo com a planilha
        final List<FilterFilePairByReleaseOcurrence> filters = FilterFilePairByReleaseOcurrence.getSuggestedFilters();
        final Project project = new Project(getRepository());
//        final int minFilePairOccurrences = 1;
//        final int minOccurrencesInVersion = 1;

        final ProjectFilePairReleaseOcurrence summary = new ProjectFilePairReleaseOcurrence(project,
                indexes,
                // minFilePairOccurrences, minOccurrencesInVersion,
                filters, filtersForExperiment1);
        summaries.add(summary);

        final Map<FilterByApriori, ProjectFilePairReleaseOcurrence> projectVersionFilePairReleaseOcurrence
                = new LinkedHashMap<>();
        for (FilterByApriori aprioriFilter : filtersForExperiment1) {
            final ProjectFilePairReleaseOcurrence projectFilePairReleaseOcurrence = new ProjectFilePairReleaseOcurrence(project,
                    indexes,
                    // minFilePairOccurrences, minOccurrencesInVersion,
                    filters, filtersForExperiment1);
            projectVersionFilePairReleaseOcurrence.put(aprioriFilter, projectFilePairReleaseOcurrence);
            releaseOccurrencesMap.get(aprioriFilter).add(projectFilePairReleaseOcurrence);
        }

        // combina em pares todos os arquivos commitados em uma issue
        //final Statistics statistics = new Statistics();
        for (int index = 0; index < subdividedIssuesCommits.size() - 1; index++) {
            final Map<FilePair, FilePairAprioriOutput> pairFiles = new HashMap<>();
            final Map<Issue, List<Commit>> issuesCommits = subdividedIssuesCommits.get(index);
            final Set<Issue> issues = issuesCommits.keySet();
            final Set<Issue> futureIssues = subdividedIssuesCommits.get(index + 1).keySet();

            identifyFilePairs("group " + getGroupsQuantity() + " - " + String.valueOf(index), pairFiles, issuesCommits, bichoFileDAO);

//            out.printLog("Result: " + pairFiles.size());
//
//            out.printLog("Index: " + index + "/" + (subdividedIssuesCommits.size() - 1));
//            out.printLog("Counting future defects...");
//            final int total = pairFiles.keySet().size();
//            int progressCountFutureDefects = 0;
//            for (FilePair fileFile : pairFiles.keySet()) {
//                if (++progressCountFutureDefects % 100 == 0
//                        || progressCountFutureDefects == total) {
//                    System.out.println(progressCountFutureDefects + "/" + total);
//                }
//                Map<String, Set<Integer>> futureFilePairIssues = bichoPairFileDAO.selectIssues(
//                        fileFile.getFile1().getFileName(), fileFile.getFile2().getFileName(),
//                        futureIssues);
//
//                if (futureFilePairIssues.get("Bug") != null) {
//                    pairFiles.get(fileFile).addFutureDefectIssuesId(futureFilePairIssues.get("Bug"));
//                }
//                Set<Integer> allIssuesId = new HashSet<>();
//                for (Map.Entry<String, Set<Integer>> entrySet : futureFilePairIssues.entrySet()) {
//                    Set<Integer> value = entrySet.getValue();
//                    allIssuesId.addAll(value);
//                }
//                pairFiles.get(fileFile).addFutureIssuesId(allIssuesId);
//            }
//
//            Set<Integer> allConsideredIssues = new HashSet<>();
//            for (Map.Entry<FilePair, FilePairAprioriOutput> entrySet : pairFiles.entrySet()) {
//                FilePairAprioriOutput value = entrySet.getValue();
//                allConsideredIssues.addAll(value.getIssuesId());
//            }
//            // calculando o apriori
//            out.printLog("Calculing apriori...");
//            out.printLog("Issues index " + index + " (x 100): " + allConsideredIssues.size());
//
//
//            calculeApriori(pairFiles, cacher, issues, allConsideredIssues);
//
//            final Map<FilterByApriori, Set<FilePairAprioriOutput>> output = new HashMap<>();
//            for (FilterByApriori aprioriFilter : filtersForExperiment1) {
//                output.put(aprioriFilter, new HashSet<>());
//            }
//
//            for (Map.Entry<FilePair, FilePairAprioriOutput> entrySet : pairFiles.entrySet()) {
//                FilePair filePair = entrySet.getKey();
//                FilePairAprioriOutput value = entrySet.getValue();
//                final FilePairApriori apriori = value.getFilePairApriori();
//
//                summary.addVersionForFilePair(filePair, indexesMap.get(index), value.getIssues().size());
//                for (FilterByApriori aprioriFilter : filtersForExperiment1) {
//                    if (apriori.fits(aprioriFilter)) {
//                        output.get(aprioriFilter).add(value);
//                        projectVersionFilePairReleaseOcurrence.get(aprioriFilter).addVersionForFilePair(filePair, indexesMap.get(index), value.getIssues().size());
//                        projectVersionFilePairReleaseOcurrence.get(aprioriFilter).addPairFileForAprioriFilter(filePair, aprioriFilter);
//                        summary.addPairFileForAprioriFilter(filePair, aprioriFilter);
//                    }
//                }
//
//            }
//            for (FilterByApriori aprioriFilter : filtersForExperiment1) {
//                EntityMatrix matrix = new EntityMatrix();
//                final List<FilePairAprioriOutput> matrixNodes = new ArrayList<>(output.get(aprioriFilter));
//                orderByFilePairConfidenceAndSupport(matrixNodes);
//
//                matrix.setNodes(objectsToNodes(matrixNodes, FilePairAprioriOutput.getToStringHeader()));
//                matrix.setRepository(project.getName());
//                matrix.getParams().put("filename", indexesMap.get(index).getVersion());
//                matrix.getParams().put("additionalFilename", aprioriFilter.toString());
////                matrix.getParams().put("minOccurrencesInVersion", minOccurrencesInVersion);
//                matrix.getParams().put("aprioriFilter", aprioriFilter.toString());
//                matrix.getParams().put("index", indexesMap.get(index).getVersion());
//                matrix.getParams().put("project", project.getName());
//
//                saveMatrix(matrix, getClass());
//            }
        }
//
//        // statistics for groups
////        try {
////            final File outputFile = new File("C:/Users/a562273/Desktop/" + bichoFileDAO.getRepository() + "_files.csv");
////            final boolean exists = outputFile.exists();
////            try (FileWriter fw = new FileWriter(outputFile, true)) {
////                if (!exists) {
//////                    fw.append("version;issues;excludedIssues;commits;excludedCommits;co-changes;files\r\n");
////                    fw.append("version;total issues;issues per part;issues;commits;co-changes;files\r\n");
////                }
////                fw.append(String.valueOf(getGroupsQuantity())).append(";")
////                        .append(String.valueOf(totalIssues)).append(";")
////                        .append(String.valueOf(quantity)).append(";")
////                        .append(String.valueOf(statistics.getAllIssues().size())).append(";")
////                        //.append(String.valueOf(excludedIssues.size())).append(";")
////                        .append(String.valueOf(statistics.getAllCommits().size())).append(";")
////                        //.append(String.valueOf(excludedCommits.size())).append(";")
////                        .append(String.valueOf(statistics.getPairFiles().size())).append(";")
////                        .append(String.valueOf(statistics.getAllJavaFiles().size() + statistics.getAllXmlFiles().size()))
////                        .append("\r\n");
////                fw.flush();
////            }
////        } catch (IOException ex) {
////            ex.printStackTrace();
////        }
//
//        releaseOccurrencesMap.entrySet().stream().map((entrySet) -> {
//            FilterByApriori aprioriFilter = entrySet.getKey();
//            Set<ProjectFilePairReleaseOcurrence> releaseOccurrences = entrySet.getValue();
//            EntityMatrix matrixSummary = new EntityMatrix();
//            matrixSummary.setNodes(objectsToNodes(releaseOccurrences, ProjectFilePairReleaseOcurrence.getHeader() + ";" + releaseOccurrences.iterator().next().getDynamicHeader()));
//            matrixSummary.setRepository(project.getName());
//            matrixSummary.getParams().put("filename", "summary " + aprioriFilter.toString());
////            matrixSummary.getParams().put("minOccurrencesInVersion", minOccurrencesInVersion);
//            matrixSummary.getParams().put("aprioriFilter", aprioriFilter.toString());
//            return matrixSummary;
//        }).forEachOrdered((matrixSummary) -> {
//
//            saveMatrix(matrixSummary, getClass());
//        });
//
//        EntityMatrix matrixSummary = new EntityMatrix();
//        matrixSummary.setNodes(objectsToNodes(summaries, ProjectFilePairReleaseOcurrence.getHeader() + ";" + summaries.iterator().next().getDynamicHeader()));
//        matrixSummary.setRepository(project.getName());
//        matrixSummary.getParams().put("filename", "summary");
////        matrixSummary.getParams().put("minOccurrencesInVersion", minOccurrencesInVersion);
//
//        saveMatrix(matrixSummary, getClass());
    }
}
