package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.QueryUtils;
import br.edu.utfpr.recominer.metric.committer.Committer;
import br.edu.utfpr.recominer.model.CodeChurn;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.Issue;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FileMetricInLastIssuesDao implements FileMetricDao {

    private final GenericDao dao;
    private final String projectName;

    private final String selectCommittersOfFile;
    private final String countCommitsByFilename;
    private final String selectIssuesCommitsFilesWhereFileChanged;

    // commons query fragment
    private final String from;
    private final String joinPeopleCommitters;
    private final String where;
    private final String fixedIssueOnly;
    private final String filterByCommitter;
    private final String orderByCommitDate;
    private final String filterByMaxFilesInCommit;
    private final String innerJoinLastNIssues;

    FileMetricInLastIssuesDao(GenericDao dao, String projectName) {
        this.dao = dao;
        this.projectName = projectName;

        from = "  FROM {0}_issues.issues i"
                + "  JOIN {0}_issues.changes c ON c.issue_id = i.id"
                + "  JOIN {0}_issues.issues_scmlog i2s ON i2s.issue_id = i.id"
                + "  JOIN {0}_vcs.scmlog s ON s.id = i2s.scmlog_id";

        joinPeopleCommitters = "  JOIN {0}_vcs.people p ON p.id = s.committer_id";

        fixedIssueOnly
                = " AND i.resolution = \"Fixed\""
                + " AND c.field = \"Resolution\""
                + " AND c.new_value = i.resolution";

        where = " WHERE com.file_path = ?"
                + "   AND com.date > i.submitted_on"
                + "   AND com.date < i.fixed_on"
                + "   AND EXISTS (SELECT 1 FROM {0}_issues.issues_fix_version ifv WHERE ifv.issue_id = i.id)"
                + fixedIssueOnly;

        orderByCommitDate = " ORDER BY com.date ASC";
        filterByMaxFilesInCommit = " AND s.num_files <= 20";

        innerJoinLastNIssues
                = " INNER JOIN "
                + " (SELECT ita.issue_id "
                + "    FROM {0}.issues_to_analyze ita "
                + "   ORDER BY ita.fixed_date DESC"
                + "   LIMIT ? OFFSET 0) AS i3 ON i3.issue_id = i.id";

        countCommitsByFilename
                = QueryUtils.getQueryForDatabase("SELECT COUNT(DISTINCT(s.id))"
                        + from
                        + joinPeopleCommitters
                        + where, projectName);

        selectCommittersOfFile
                = QueryUtils.getQueryForDatabase("SELECT DISTINCT p.id, p.name, p.email"
                        + from
                        + joinPeopleCommitters
                        + where, projectName);

        filterByCommitter = " AND p.name = ?";

        selectIssuesCommitsFilesWhereFileChanged
                = QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT com.commit_id, com.file_path, p.id, p.name, p.email, com.date. s.rev"
                        + "  FROM {0}_issues.issues i"
                        + "  JOIN {0}_issues.changes c ON c.issue_id = i.id"
                        + "  JOIN {0}_issues.issues_scmlog i2s ON i2s.issue_id = i.id"
                        + "  JOIN {0}_vcs.scmlog s ON s.id = i2s.scmlog_id"
                        + "  JOIN {0}.commits com ON com.commit_id = i2s.scmlog_id"
                        + "  JOIN {0}_vcs.people p ON p.id = com.committer_id"
                        + " WHERE 1 = 1", projectName)
                + "   AND s.date > i.submitted_on"
                + "   AND s.date < i.fixed_on"
                + filterByMaxFilesInCommit
                + orderByCommitDate;
    }

    @Override
    public Set<Committer> selectCommitters(File file) {
        List<Object> selectParams = new ArrayList<>();
        if (file == null) {
            throw new IllegalArgumentException("Pair file could not be null");
        }

        StringBuilder sql = new StringBuilder();
        sql.append(selectCommittersOfFile);

        selectParams.add(file);

        List<Object[]> committers = dao.selectNativeWithParams(
                sql.toString(), selectParams.toArray());

        Set<Committer> commitersList = new HashSet<>(committers.size());
        for (Object[] row : committers) {
            commitersList.add(getRowAsCommiter(row));
        }

        return commitersList;
    }

    private Committer getRowAsCommiter(Object[] row) {
        Committer committer;
        Integer committerId = (Integer) row[0];
        String committerName = (String) row[1];
        String committerEmail = (String) row[2];
        committer = new Committer(committerId, committerName, committerEmail);
        return committer;
    }

    @Override
    public Long calculeCommits(File file) {
        Long count = dao.selectNativeOneWithParams(countCommitsByFilename, file.getFileName());
        return count != null ? count : 0l;
    }

    @Override
    public Long calculeCommits(File file, Committer committer) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(countCommitsByFilename);
        selectParams.add(file);

        sql.append(filterByCommitter);
        selectParams.add(committer.getName());

        Long count = dao.selectNativeOneWithParams(sql.toString(), selectParams.toArray());

        return count != null ? count : 0l;
    }

    public Set<Commit> selectCommitsAndFilesWhereFileChanged(File file, Issue issue) {
        List<Object[]> rawFilesPath
                = dao.selectNativeWithParams(selectIssuesCommitsFilesWhereFileChanged, new Object[]{issue});

        Map<Commit, Commit> commits = new LinkedHashMap<>();

        for (Object[] row : rawFilesPath) {

            Integer commitId = (Integer) row[0];
            String fileName = (String) row[1];
            Integer committerId = (Integer) row[2];
            String committerName = (String) row[3];
            String committerEmail = (String) row[4];
            java.sql.Timestamp commitDate = (java.sql.Timestamp) row[5];
            String revision = (String) row[6];

            Committer committer = new Committer(committerId, committerName, committerEmail);

            Commit commit = new Commit(commitId, revision, committer, new Date(commitDate.getTime()));

            if (commits.containsKey(commit)) {
                commits.get(commit).getFiles().add(new File(fileName));
            } else {
                commit.getFiles().add(new File(fileName));
                commits.put(commit, commit);
            }
        }

        return commits.keySet();
    }

    @Override
    public CodeChurn calculeCodeChurn(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CodeChurn calculeCodeChurn(File file, Committer committer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long calculeCommitters(File file, Commit commit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long calculeCommits(File file, Commit commit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CodeChurn calculeCodeChurn(File file, Issue issue, Commit commit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long calculeFileAgeInDays(File file, Commit commit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long calculeTotalFileAgeInDays(File file, Commit commit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Committer selectLastCommitter(File file, Commit commit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Issue> selectIssues(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
