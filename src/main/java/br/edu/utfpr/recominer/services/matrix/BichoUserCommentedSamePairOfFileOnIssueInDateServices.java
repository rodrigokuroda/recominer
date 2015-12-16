package br.edu.utfpr.recominer.services.matrix;

import br.edu.utfpr.recominer.dao.BichoDAO;
import br.edu.utfpr.recominer.dao.BichoFileDAO;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.model.Commenter;
import br.edu.utfpr.recominer.model.FilePair;
import br.edu.utfpr.recominer.model.FilePath;
import br.edu.utfpr.recominer.model.UserToUserEdgeByCommentDirectional;
import br.edu.utfpr.recominer.model.UserToUserEdgeByFilePairIssueDirectional;
import br.edu.utfpr.recominer.model.matrix.EntityMatrix;
import br.edu.utfpr.recominer.util.OutLog;
import br.edu.utfpr.recominer.util.PairUtils;
import br.edu.utfpr.recominer.util.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author douglas
 */
public class BichoUserCommentedSamePairOfFileOnIssueInDateServices extends AbstractBichoMatrixServices {

    public BichoUserCommentedSamePairOfFileOnIssueInDateServices() {
        super(null, null);
    }

    public BichoUserCommentedSamePairOfFileOnIssueInDateServices(GenericBichoDAO dao, OutLog out) {
        super(dao, out);
    }

    public BichoUserCommentedSamePairOfFileOnIssueInDateServices(GenericBichoDAO dao, GenericDao genericDao, String repository, List<EntityMatrix> matricesToSave, Map<Object, Object> params, OutLog out) {
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

    public Date getBeginDate() {
        return getDateParam("beginDate");
    }

    public Date getEndDate() {
        return getDateParam("endDate");
    }

    @Override
    public void run() {
        System.out.println(params);

        if (getRepository() == null) {
            throw new IllegalArgumentException("Parâmetro Repository não pode ser nulo.");
        }

        Date beginDate = getBeginDate();
        Date endDate = getEndDate();

        Map<UserToUserEdgeByFilePairIssueDirectional, UserToUserEdgeByFilePairIssueDirectional> result = new HashMap<>();

//        Pattern fileToConsiders = null;
//        if (getFilesToConsiders() != null && !getFilesToConsiders().isEmpty()) {
//            fileToConsiders = MatcherUtils.createExtensionIncludeMatcher(getFilesToConsiders());
//        }
//        Pattern fileToIgnore = MatcherUtils.createExcludeMatcher(getFilesToIgnore());
        int maxFilePerCommit = 20;
        BichoDAO bichoDAO = new BichoDAO(dao, getRepository(), maxFilePerCommit);
        BichoFileDAO bichoFileDAO = new BichoFileDAO(dao, getRepository(), maxFilePerCommit);

        out.printLog("Maximum files per commit: " + getMaxFilesPerCommit());
        out.printLog("Minimum files per commit: " + getMinFilesPerCommit());

        // select a issue/pullrequest commenters
        Map<Integer, Set<Integer>> issuesCommits = bichoDAO.selectIssues(beginDate, endDate);
        
        out.printLog("Issues (filtered): " + issuesCommits.size());

        int count = 1;
        int totalFilePairsCount = 0;

        for (Map.Entry<Integer, Set<Integer>> entrySet : issuesCommits.entrySet()) {
            Integer issue = entrySet.getKey();
            Set<Integer> commits = entrySet.getValue();

            out.printLog("##################### NR: " + issue);
            out.printLog(count + " of the " + issuesCommits.size());

            out.printLog(commits.size() + " commits references the issue");

            // monta os pares com os arquivos de todos os commits da issue
            List<FilePath> commitedFiles = new ArrayList<>();
            for (Integer commit : commits) {

                // select name of commited files
                List<FilePath> files = bichoFileDAO.selectFilesByCommitId(commit);
                out.printLog(files.size() + " files in commit #" + commit);
                commitedFiles.addAll(files);
            }

            out.printLog("Number of files commited and related with issue: " + commitedFiles.size());

            Set<FilePair> pairFiles = new HashSet<>();
            int totalPullRequestFilePairsCount = 0;
            for (int i = 0; i < commitedFiles.size(); i++) {
                FilePath file1 = commitedFiles.get(i);
                for (int j = i + 1; j < commitedFiles.size(); j++) {
                    FilePath file2 = commitedFiles.get(j);
                    if (!file1.equals(file2)
                            && !Util.stringEquals(file1.getFilePath(), file2.getFilePath())) {
                        FilePair fileFile = new FilePair(file1.getFilePath(),
                                file2.getFilePath());
                        if (!pairFiles.contains(fileFile)) {
                            pairFiles.add(fileFile);

                            totalPullRequestFilePairsCount++;
                        }
                    }
                }
            }
            totalFilePairsCount += totalPullRequestFilePairsCount;
            out.printLog("Issue files pairs: " + totalPullRequestFilePairsCount);

            // seleciona os autores de cada comentario (mesmo repetido)
            List<Commenter> commenters = bichoDAO.selectCommentersByIssueOrderBySubmissionDate(issue);
            out.printLog("Issue comments" + commenters.size());

            Map<UserToUserEdgeByCommentDirectional, UserToUserEdgeByCommentDirectional> pairCommenter
                    = PairUtils.pairCommenters(commenters);
            commenters.clear();
            out.printLog("Creating matrix of users (" + pairCommenter.size()
                    + ") and pair file (" + pairFiles.size() + ")");
            for (UserToUserEdgeByCommentDirectional users : pairCommenter.values()) {
                for (FilePair files : pairFiles) {
                    UserToUserEdgeByFilePairIssueDirectional aux = new UserToUserEdgeByFilePairIssueDirectional(
                            users.getUser(),
                            files.getFile1().getFileName(),
                            files.getFile2().getFileName(),
                            users.getUser2(),
                            issue,
                            users.getWeigth());

                    if (result.containsKey(aux)) {
                        result.get(aux).inc();
                    } else {
                        result.put(aux, aux);
                    }
                }
            }

            count++;
            out.printLog("Temp user result: " + result.size());
        }
        out.printLog("Number of pair files: " + totalFilePairsCount);
        out.printLog("Result: " + result.size());

        EntityMatrix matrix = new EntityMatrix();
        matrix.setNodes(objectsToNodes(result.values(), this.getHeadCSV()));
        matricesToSave.add(matrix);
    }

    public String getHeadCSV() {
        return "user;file;file2;user2;weigth";
    }
}
