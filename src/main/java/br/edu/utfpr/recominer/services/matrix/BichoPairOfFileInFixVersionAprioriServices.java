package br.edu.utfpr.recominer.services.matrix;

import br.edu.utfpr.recominer.dao.BichoDAO;
import br.edu.utfpr.recominer.dao.BichoFileDAO;
import br.edu.utfpr.recominer.dao.BichoPairFileDAO;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.FilePairAprioriOutput;
import br.edu.utfpr.recominer.model.matrix.EntityMatrix;
import br.edu.utfpr.recominer.model.matrix.EntityMatrixNode;
import br.edu.utfpr.recominer.services.metric.Cacher;
import br.edu.utfpr.recominer.util.OutLog;
import br.edu.utfpr.recominer.util.Util;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Rodrigo Kuroda
 */
public class BichoPairOfFileInFixVersionAprioriServices extends AbstractBichoMatrixServices {

    public BichoPairOfFileInFixVersionAprioriServices() {
        super(null, null);
    }

    public BichoPairOfFileInFixVersionAprioriServices(GenericBichoDAO dao, OutLog out) {
        super(dao, out);
    }

    public BichoPairOfFileInFixVersionAprioriServices(GenericBichoDAO dao, GenericDao genericDao, String repository, List<EntityMatrix> matricesToSave, Map<Object, Object> params, OutLog out) {
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

    public String getVersion() {
        return getStringParam("version");
    }

    public String getFutureVersion() {
        return getStringParam("futureVersion");
    }

    @Override
    public void run() {
        System.out.println(params);

        if (getRepository() == null) {
            throw new IllegalArgumentException("Parameter repository must be informed.");
        }

        log("\n --------------- "
                + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())
                + "\n --------------- \n");

        String version = getVersion();
        String futureVersion = getFutureVersion();
//        Set<FilePath> allDistinctFiles = new HashSet<>();

//        Pattern fileToConsiders = null;
//        if (getFilesToConsiders() != null && !getFilesToConsiders().isEmpty()) {
//            fileToConsiders = MatcherUtils.createExtensionIncludeMatcher(getFilesToConsiders());
//        }

        BichoDAO bichoDAO = new BichoDAO(dao, getRepository(), getMaxFilesPerCommit());
        BichoFileDAO bichoFileDAO = new BichoFileDAO(dao, getRepository(), getMaxFilesPerCommit());
        BichoPairFileDAO bichoPairFileDAO = new BichoPairFileDAO(dao, getRepository(), getMaxFilesPerCommit());
        Cacher cacher = new Cacher(bichoFileDAO);

        out.printLog("Maximum files per commit: " + getMaxFilesPerCommit());
        out.printLog("Minimum files per commit: " + getMinFilesPerCommit());

        final Map<FilePair, FilePairAprioriOutput> pairFiles = new HashMap<>();
        identifyFilePairs(pairFiles, bichoDAO, version, bichoFileDAO);

        out.printLog("Result: " + pairFiles.size());

//        Set<Integer> allConsideredIssues = new HashSet<>();
//        for (Map.Entry<FilePair, FilePairAprioriOutput> entrySet : pairFiles.entrySet()) {
//            FilePairAprioriOutput value = entrySet.getValue();
//            allConsideredIssues.addAll(value.getIssuesId());
//        }
//        // calculando o apriori
//        out.printLog("Calculing apriori...");
//        out.printLog("Issues considered in version " + version + ": " + allConsideredIssues.size());
//        int totalApriori = pairFiles.size();
//        int countApriori = 0;
//
//        final List<FilePairAprioriOutput> pairFileList = new ArrayList<>();
//
//        for (FilePair fileFile : pairFiles.keySet()) {
//            if (++countApriori % 100 == 0
//                    || countApriori == totalApriori) {
//                System.out.println(countApriori + "/" + totalApriori);
//            }
//
//            Long file1Issues = cacher.calculeNumberOfIssues(fileFile.getFile1(), version);
//            Long file2Issues = cacher.calculeNumberOfIssues(fileFile.getFile2(), version);
//
//            FilePairAprioriOutput filePairOutput = pairFiles.get(fileFile);
//
//            FilePairApriori apriori = new FilePairApriori(file1Issues, file2Issues,
//                    filePairOutput.getIssuesIdWeight(), allConsideredIssues.size());
//
//            fileFile.orderFilePairByConfidence(apriori);
//            filePairOutput.setFilePairApriori(apriori);
//
//            pairFileList.add(filePairOutput);
//        }
//        orderByFilePairSupportAndConfidence(pairFileList);
//
//        EntityMatrix matrix = new EntityMatrix();
//        matrix.setNodes(objectsToNodes(pairFileList, FilePairAprioriOutput.getToStringHeader()));
//        matricesToSave.add(matrix);

//        saveTop25Matrix(pairFileList);
    }

    protected static List<EntityMatrixNode> objectsToNodes(List<FilePairAprioriOutput> list, String header) {
        List<EntityMatrixNode> nodes = new ArrayList<>();
        nodes.add(new EntityMatrixNode(header));
        for (FilePairAprioriOutput value : list) {
            nodes.add(new EntityMatrixNode(value.toString()));
        }
        return nodes;
    }

}
