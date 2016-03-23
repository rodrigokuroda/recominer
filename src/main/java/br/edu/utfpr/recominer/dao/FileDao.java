package br.edu.utfpr.recominer.dao;

import static br.edu.utfpr.recominer.dao.QueryUtils.filterByIssues;
import br.edu.utfpr.recominer.metric.committer.Committer;
import br.edu.utfpr.recominer.model.CodeChurn;
import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.File;
import br.edu.utfpr.recominer.model.FilePath;
import br.edu.utfpr.recominer.model.Issue;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import static br.edu.utfpr.recominer.dao.QueryUtils.filterByIssues;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FileDao {

    private final String repository;

    // commons query fragment
    private final String FROM_TABLE;
    private final String JOIN_PEOPLE_COMMITER;
    private final String WHERE;

    // filters
    private final String FILTER_BY_ISSUE;

    // limiting
    private final String ISSUE_BY_LIMIT_OFFSET_ORDER_BY_FIX_DATE;
    private final String SUM_ADD_AND_DEL_LINES_BY_FILE_NAME_AND_LIMIT_OFFSET;
    private final String COUNT_COMMITS_BY_FILE_NAME_AND_LIMIT_OFFSET;

    private final String FILTER_BY_ISSUE_FIX_MAJOR_VERSION;
    private final String FILTER_BY_BEFORE_ISSUE_FIX_MAJOR_VERSION_INCLUSIVE;
    private final String FILTER_BY_BEFORE_ISSUE_FIX_MAJOR_VERSION_EXCLUSIVE;
    private final String FILTER_BY_BEFORE_ISSUE_FIX_DATE_OF_ISSUE_ID;

    private final String FILTER_BY_COMMIT;

    private final String FILTER_BY_ISSUE_FIX_DATE;
    private final String FILTER_BY_BEFORE_ISSUE_FIX_DATE;
    private final String FILTER_BY_AFTER_ISSUE_FIX_DATE;

    private final String FILTER_BY_MAX_FILES_IN_COMMIT;
    private final String FILTER_BY_MIN_FILES_IN_COMMIT;

    private final String FILTER_BY_MIN_ISSUE_COMMENTS;

    private final String FILTER_BY_USER_NAME;
    private final String FIXED_ISSUES_ONLY;

    private final String FILTER_BY_ISSUES_THAT_HAS_AT_LEAST_ONE_COMMENT;

    private final String FILTER_BY_LIKE_FILE_NAME;
    private final String FILTER_BY_JAVA_EXTENSION;
    private final String FILTER_BY_XML_EXTENSION;
    private final String FILTER_BY_JAVA_OR_XML_EXTENSION;
    private final String FILTER_BY_JAVA_OR_XML_EXTENSION_AND_IS_NOT_TEST;

     private final String ORDER_BY_SUBMITTED_ON;
     private final String ORDER_BY_FIXED_ON;

    // complete queries (commons fragment + specific fragments for query)
    private final String COUNT_DISTINCT_COMMITERS_BY_FILE_NAME;

    private final String COUNT_COMMITS_BY_FILE_NAME;

    private final String COUNT_ISSUES_BY_FILENAME;

    private final String SUM_ADD_AND_DEL_LINES_BY_FILE_NAME;
    private final String GET_ADD_AND_DEL_LINES_BY_FILE_NAME_AND_ISSUE_AND_COMMIT;

    private final String SELECT_ISSUES;
    private final String SELECT_ISSUES_BY_FIX_MAJOR_VERSION;
    private final String SELECT_ISSUES_BY_FILENAME;

    private final String SELECT_FILES_PATH_BY_COMMIT_ID;

    private final String SELECT_COMMITTERS_OF_FILE_BY_DATE;
    private final String SELECT_COMMITTERS_OF_FILE_BY_FIX_MAJOR_VERSION;
    private final String SELECT_COMMITTERS_BY_FILENAME;
    private final String COUNT_COMMITTERS_OF_FILE;

    private final String COUNT_ISSUE_REOPENED_TIMES;

    private final String COUNT_ISSUES_TYPES;
    private final String SELECT_RELEASE_MIN_MAX_COMMIT_DATE;

    private final String SELECT_LAST_COMMITTER_BEFORE_COMMIT;
    private final String SELECT_LAST_COMMITTER_BEFORE_COMMIT_AND_BEFORE_INCLUSIVE_VERSION;
    private final String SELECT_LAST_COMMITTER_BEFORE_ISSUE_BY_ISSUES_ID;

    private final String SELECT_COMMIT_AND_FILES_BY_FILENAME_AND_ISSUE;
    private final String FILTER_BY_ISSUE_ID;

    private final GenericDao dao;
    private final String BEGIN_SUM_ADD_AND_DEL_LINES;
    private final String END_SUM_ADD_AND_DEL_LINES;

    public FileDao(GenericDao dao, String repository, Integer maxFilePerCommit) {
        this(dao, repository, maxFilePerCommit, 0);
    }

    public FileDao(GenericDao dao, String repository, Integer maxFilePerCommit, int numberOfLastIssues) {
        this.dao = dao;
        this.repository = repository;

        FIXED_ISSUES_ONLY
                = " AND i.resolution = \"Fixed\""
                + " AND c.field = \"Resolution\""
                + " AND c.new_value = i.resolution";

        if (numberOfLastIssues <= 0) {
            FROM_TABLE
                    = "  FROM {0}_issues.issues i"
                    + "  JOIN {0}_issues.changes c ON c.issue_id = i.id"
                    + "  JOIN {0}.issues_scmlog i2s ON i2s.issue_id = i.id"
                    + "  JOIN {0}_vcs.scmlog s ON s.id = i2s.scmlog_id"
                    // removed because its a one to many relationship, causing duplicated rows, for exameple to sum code churn
                    // replaced with "AND EXISTS (SELECT 1 FROM {0}.issues_fix_version ifv WHERE ifv.issue_id = i.id)"
                    //+ "  JOIN {0}.issues_fix_version ifv ON ifv.issue_id = i2s.issue_id"
                    + "  JOIN {0}.commits com ON com.commit_id = i2s.scmlog_id";
        } else {
            FROM_TABLE
                    = "  FROM {0}_issues.issues i"
                    + "  JOIN {0}_issues.changes c ON c.issue_id = i.id"
                    + "  JOIN {0}.issues_scmlog i2s ON i2s.issue_id = i.id"
                    + "  JOIN {0}_vcs.scmlog s ON s.id = i2s.scmlog_id"
                    // removed because its a one to many relationship, causing duplicated rows, for exameple to sum code churn
                    //+ "  JOIN {0}.issues_fix_version ifv ON ifv.issue_id = i2s.issue_id"
                    + "  JOIN {0}.commits com ON com.commit_id = i2s.scmlog_id"
                    + "  JOIN "
                    + " (SELECT ita.issue_id "
                    + "    FROM {0}.issues_to_analyze ita "
                    + "   ORDER BY ita.fixed_date DESC"
                    + "   LIMIT " + numberOfLastIssues + " OFFSET 0) AS i3 ON i3.issue_id = i.id";
        }

        JOIN_PEOPLE_COMMITER = "  JOIN {0}_vcs.people p ON p.id = s.committer_id";

        WHERE = " WHERE com.file_path = ?"
                + "   AND com.date > i.submitted_on"
                + "   AND com.date < i.fixed_on"
                + "   AND EXISTS (SELECT 1 FROM {0}.issues_fix_version ifv WHERE ifv.issue_id = i.id)"
                + FIXED_ISSUES_ONLY;

        FILTER_BY_ISSUE
                = " AND i.id = ? ";

        FILTER_BY_ISSUE_FIX_DATE
                = " AND c.changed_on BETWEEN ? AND ?";

        // avoid join, because has poor performance in this case
        FILTER_BY_ISSUE_FIX_MAJOR_VERSION
                = QueryUtils.getQueryForDatabase(
                        " AND i.id IN ("
                        + " SELECT ifv.issue_id "
                        + "   FROM {0}.issues_fix_version ifv "
                        + "  WHERE ifv.major_fix_version = ?)", repository);

        // avoid join, because has poor performance in this case
        FILTER_BY_BEFORE_ISSUE_FIX_MAJOR_VERSION_INCLUSIVE
                = QueryUtils.getQueryForDatabase(
                        " AND i.id IN ("
                        + " SELECT ifv.issue_id "
                        + "   FROM {0}.issues_fix_version ifv "
                        + "  WHERE ifv.major_fix_version IN ("
                        + "SELECT ifvo.major_fix_version"
                        + "  FROM {0}.issues_fix_version_order ifvo"
                        + " WHERE ifvo.version_order <= " // inclusive
                        + "(SELECT MAX(ifvo2.version_order)"
                        + "   FROM {0}.issues_fix_version_order ifvo2"
                        + "  WHERE ifvo2.major_fix_version = ?)))", repository);

        // avoid join, because has poor performance in this case
        FILTER_BY_BEFORE_ISSUE_FIX_MAJOR_VERSION_EXCLUSIVE
                = QueryUtils.getQueryForDatabase(
                        " AND i.id IN ("
                        + " SELECT ifv.issue_id "
                        + "   FROM {0}.issues_fix_version ifv "
                        + "  WHERE ifv.major_fix_version IN ("
                        + "SELECT ifvo.major_fix_version"
                        + "  FROM {0}.issues_fix_version_order ifvo"
                        + " WHERE ifvo.version_order < " // exclusive
                        + "(SELECT MIN(ifvo2.version_order)"
                        + "   FROM {0}.issues_fix_version_order ifvo2"
                        + "  WHERE ifvo2.major_fix_version = ?)))", repository);

        FILTER_BY_BEFORE_ISSUE_FIX_DATE_OF_ISSUE_ID
                = QueryUtils.getQueryForDatabase(
                        " AND c.changed_on <="
                        + "     (SELECT MAX(c2.changed_on)"
                        + "        FROM {0}_issues.changes c2"
                        + "       WHERE c2.issue_id = ?"
                        + "         AND c2.field = \"Resolution\""
                        + "         AND c2.new_value = \"Fixed\")", repository);

        FILTER_BY_COMMIT
                = " AND s.id = ?";

        FILTER_BY_BEFORE_ISSUE_FIX_DATE
                = " AND c.changed_on <= ?";

        FILTER_BY_AFTER_ISSUE_FIX_DATE
                = " AND c.changed_on >= ?";

        FILTER_BY_MAX_FILES_IN_COMMIT
                = " AND s.num_files <= " + maxFilePerCommit;

        FILTER_BY_MIN_FILES_IN_COMMIT
                = " AND s.num_files >= ? ";

        FILTER_BY_MIN_ISSUE_COMMENTS
                = " AND i.num_comments >= ? ";

        FILTER_BY_ISSUES_THAT_HAS_AT_LEAST_ONE_COMMENT
                = " AND i.num_comments > 0";

        FILTER_BY_USER_NAME
                = " AND p.name = ?";

        FILTER_BY_LIKE_FILE_NAME
                = " AND com.file_path LIKE ?";
        FILTER_BY_JAVA_EXTENSION
                = " AND com.file_path LIKE \"%.java\"";
        FILTER_BY_XML_EXTENSION
                = " AND com.file_path LIKE \"%.xml\"";
        FILTER_BY_JAVA_OR_XML_EXTENSION
                = " AND (com.file_path LIKE \"%.java\""
                + " OR com.file_path LIKE \"%.xml\")";
        FILTER_BY_JAVA_OR_XML_EXTENSION_AND_IS_NOT_TEST
                = " AND (com.file_path LIKE \"%.java\""
                + " OR com.file_path LIKE \"%.xml\")"
                + " AND (com.file_path NOT LIKE \"%Test.java\" "
                + " AND com.file_path NOT LIKE \"%_test.java\") ";

        FILTER_BY_ISSUE_ID
                = " AND i.id = ?";

        ORDER_BY_SUBMITTED_ON
                = " ORDER BY i.submitted_on";

        ORDER_BY_FIXED_ON
                = " ORDER BY i.fixed_on";

        COUNT_ISSUES_BY_FILENAME
                = QueryUtils.getQueryForDatabase(
                        "SELECT COUNT(DISTINCT(i.id))"
                        + FROM_TABLE
                        + WHERE, repository);

        COUNT_DISTINCT_COMMITERS_BY_FILE_NAME
                = QueryUtils.getQueryForDatabase("SELECT COUNT(DISTINCT(p.name))"
                        + FROM_TABLE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE, repository);

        COUNT_COMMITS_BY_FILE_NAME
                = QueryUtils.getQueryForDatabase("SELECT COUNT(DISTINCT(s.id))"
                        + FROM_TABLE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE, repository);

        BEGIN_SUM_ADD_AND_DEL_LINES = "SELECT COALESCE(SUM(added_lines), 0) AS added_lines, COALESCE(SUM(removed_lines), 0) AS removed_lines FROM (";
        END_SUM_ADD_AND_DEL_LINES = ") AS sum";
        SUM_ADD_AND_DEL_LINES_BY_FILE_NAME
                = QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT com.added_lines AS added_lines, "
                        + "       com.removed_lines AS removed_lines, i.id AS issue_id, s.id AS commit_id, p.id AS committer_id "
                        + FROM_TABLE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE, repository);

        GET_ADD_AND_DEL_LINES_BY_FILE_NAME_AND_ISSUE_AND_COMMIT
                = QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT com.added_lines, com.removed_lines, i.id AS issue_id, s.id AS commit_id "
                        + FROM_TABLE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE
                        + FILTER_BY_ISSUE
                        + FILTER_BY_COMMIT, repository);

        SELECT_ISSUES
                = QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT i.id"
                        + FROM_TABLE
                        + WHERE, repository);

        SELECT_ISSUES_BY_FIX_MAJOR_VERSION
                = QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT i.id"
                        + FROM_TABLE
                        + WHERE, repository)
                + FILTER_BY_ISSUE_FIX_MAJOR_VERSION;

        SELECT_ISSUES_BY_FILENAME
                = QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT i.id"
                        + FROM_TABLE
                        + WHERE, repository);

        SELECT_FILES_PATH_BY_COMMIT_ID
                = QueryUtils.getQueryForDatabase(
                        "SELECT com.file_id, com.file_path"
                        + "  FROM {0}.commits com"
                        + "  JOIN {0}_vcs.scmlog s ON s.id = com.commit_id"
                        + " WHERE com.commit_id = ?", repository)
                + FILTER_BY_MAX_FILES_IN_COMMIT;

        SELECT_COMMITTERS_OF_FILE_BY_DATE
                = QueryUtils.getQueryForDatabase("SELECT DISTINCT p.id, p.name, p.email"
                        + FROM_TABLE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE, repository);

        SELECT_COMMITTERS_OF_FILE_BY_FIX_MAJOR_VERSION
                = QueryUtils.getQueryForDatabase("SELECT DISTINCT p.id, p.name, p.email"
                        + FROM_TABLE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE, repository);

        SELECT_COMMITTERS_BY_FILENAME
                = QueryUtils.getQueryForDatabase("SELECT DISTINCT p.id, p.name, p.email"
                        + FROM_TABLE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE, repository);

        COUNT_COMMITTERS_OF_FILE
                = QueryUtils.getQueryForDatabase("SELECT COUNT(DISTINCT(p.name))"
                        + FROM_TABLE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE, repository);

        COUNT_ISSUE_REOPENED_TIMES
                = QueryUtils.getQueryForDatabase(
                        "SELECT COALESCE(COUNT(1), 0)"
                        + "  FROM {0}_issues.changes c"
                        + " WHERE c.new_value = ?"
                        + "   AND c.field = ?"
                        + "   AND c.issue_id = ?", repository);

        COUNT_ISSUES_TYPES
                = QueryUtils.getQueryForDatabase(""
                        + "SELECT COALESCE(COUNT(DISTINCT(i.id)), 0) AS count, i.type"
                        + FROM_TABLE
                        + WHERE
                        + FILTER_BY_ISSUE_FIX_MAJOR_VERSION
                        + " GROUP BY i.type", repository);

        SELECT_RELEASE_MIN_MAX_COMMIT_DATE
                = QueryUtils.getQueryForDatabase(
                        "SELECT MIN(com.date), MAX(com.date)"
                        + FROM_TABLE
                        + WHERE, repository);

        SELECT_LAST_COMMITTER_BEFORE_COMMIT_AND_BEFORE_INCLUSIVE_VERSION
                = QueryUtils.getQueryForDatabase("SELECT DISTINCT p.id, p.name, p.email"
                        + FROM_TABLE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE
                        + FILTER_BY_BEFORE_ISSUE_FIX_MAJOR_VERSION_INCLUSIVE
                        + " AND s.date < "
                        + "     (SELECT MAX(s2.date) FROM {0}_vcs.scmlog s2"
                        + "       WHERE s2.id = ?)"
                        + " ORDER BY s.date DESC LIMIT 1", repository);

        SELECT_LAST_COMMITTER_BEFORE_COMMIT
                = QueryUtils.getQueryForDatabase("SELECT DISTINCT p.id, p.name, p.email"
                        + FROM_TABLE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE
                        + FILTER_BY_BEFORE_ISSUE_FIX_MAJOR_VERSION_INCLUSIVE
                        + " AND s.date < "
                        + "     (SELECT MAX(s2.date) FROM {0}_vcs.scmlog s2"
                        + "       WHERE s2.id = ?)"
                        + " ORDER BY s.date DESC LIMIT 1", repository);

        SELECT_LAST_COMMITTER_BEFORE_ISSUE_BY_ISSUES_ID
                = QueryUtils.getQueryForDatabase("SELECT DISTINCT p.id, p.name, p.email"
                        + FROM_TABLE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE
                        + FILTER_BY_BEFORE_ISSUE_FIX_MAJOR_VERSION_INCLUSIVE
                        + " AND s.date < "
                        + "     (SELECT MAX(s2.date) FROM {0}_vcs.scmlog s2"
                        + "       WHERE s2.id = ?)", repository);

        SELECT_COMMIT_AND_FILES_BY_FILENAME_AND_ISSUE
                = QueryUtils.getQueryForDatabase("SELECT DISTINCT com.commit_id, com.file_path, p.id, p.name, p.email, com.date, com.rev"
                        + FROM_TABLE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE, repository)
                + FILTER_BY_ISSUE_ID;

        ISSUE_BY_LIMIT_OFFSET_ORDER_BY_FIX_DATE
                = " INNER JOIN "
                + " (SELECT DISTINCT i2.id "
                + "    FROM {0}_issues.issues i2 "
                + "    JOIN {0}_issues.changes c2 ON c2.issue_id = i2.id"
                + "    JOIN {0}.issues_scmlog i2s2 ON i2s2.issue_id = i2.id"
                + "    JOIN {0}_vcs.scmlog s2 ON s2.id = i2s2.scmlog_id"
                + "    JOIN {0}.commits com2 ON com2.commit_id = i2s2.scmlog_id"
                + "   WHERE i2.fixed_on IS NOT NULL"
                + "     AND s2.date > i2.submitted_on"
                + "     AND s2.date < i2.fixed_on"
                + "     AND i2.resolution = \"Fixed\""
                + "     AND c2.field = \"Resolution\""
                + "     AND c2.new_value = i2.resolution"
                + "     AND s2.num_files <= " + maxFilePerCommit
                + "     AND s2.num_files > 0 "
                + "     AND (com2.file_path LIKE \"%.java\" OR com2.file_path LIKE \"%.xml\") "
                + "     AND (com2.file_path NOT LIKE \"%Test.java\" OR com2.file_path NOT LIKE \"%_test.java\") "
                + "     AND EXISTS (SELECT 1 FROM {0}.issues_fix_version ifv2 WHERE ifv2.issue_id = i2.id)"
                + "   ORDER BY i2.fixed_on "
                + "   LIMIT ? OFFSET ?) AS i3 ON i3.id = i.id";

        SUM_ADD_AND_DEL_LINES_BY_FILE_NAME_AND_LIMIT_OFFSET
                = QueryUtils.getQueryForDatabase("SELECT COALESCE(SUM(com.added_lines), 0),"
                        + "       COALESCE(SUM(com.removed_lines), 0)"
                        + FROM_TABLE
                        + ISSUE_BY_LIMIT_OFFSET_ORDER_BY_FIX_DATE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE, repository);

        COUNT_COMMITS_BY_FILE_NAME_AND_LIMIT_OFFSET
                = QueryUtils.getQueryForDatabase("SELECT COUNT(DISTINCT(s.id))"
                        + FROM_TABLE
                        + ISSUE_BY_LIMIT_OFFSET_ORDER_BY_FIX_DATE
                        + JOIN_PEOPLE_COMMITER
                        + WHERE, repository);
    }

    // Issues //////////////////////////////////////////////////////////////////
    public long calculeNumberOfIssues(
            String filename, Date beginDate, Date endDate) {
        return calculeNumberOfIssues(filename,
                beginDate, endDate, true);
    }

    public long calculeNumberOfIssues(
            String filename, Date beginDate, Date endDate, boolean onlyFixed) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(COUNT_ISSUES_BY_FILENAME);
        selectParams.add(filename);

        if (beginDate != null && endDate != null) { // from begin to end
            sql.append(FILTER_BY_ISSUE_FIX_DATE);
            selectParams.add(beginDate);
            selectParams.add(endDate);
        } else if (beginDate != null) { // from begin
            sql.append(FILTER_BY_AFTER_ISSUE_FIX_DATE);
            selectParams.add(beginDate);
        } else if (endDate != null) { // until end
            sql.append(FILTER_BY_BEFORE_ISSUE_FIX_DATE);
            selectParams.add(endDate);
        }

        if (onlyFixed) {
            sql.append(FIXED_ISSUES_ONLY);
        }

        Long count = (Long) dao.selectNativeOneWithParams(
                sql.toString(),
                selectParams.toArray());

        return count != null ? count : 0l;
    }

    public long calculeNumberOfIssues(String filename) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(COUNT_ISSUES_BY_FILENAME);
        selectParams.add(filename);

        sql.append(FIXED_ISSUES_ONLY);

        Long count = (Long) dao.selectNativeOneWithParams(
                sql.toString(),
                selectParams.toArray());

        return count != null ? count : 0l;
    }

    public long calculeNumberOfIssues(
            String filename, String version) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(COUNT_ISSUES_BY_FILENAME);
        selectParams.add(filename);

        if (version != null) { // from begin to end
            sql.append(FILTER_BY_ISSUE_FIX_MAJOR_VERSION);
            selectParams.add(version);
        }

        sql.append(FIXED_ISSUES_ONLY);

        Long count = (Long) dao.selectNativeOneWithParams(
                sql.toString(),
                selectParams.toArray());

        return count != null ? count : 0l;
    }

    public long calculeNumberOfIssues(String filename, Set<Issue> issues) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(COUNT_ISSUES_BY_FILENAME);
        selectParams.add(filename);

        filterByIssues(issues, sql);

        sql.append(FIXED_ISSUES_ONLY);

        Long count = (Long) dao.selectNativeOneWithParams(
                sql.toString(),
                selectParams.toArray());

        return count != null ? count : 0l;
    }

    // COMMITTERS //////////////////////////////////////////////////////////////
    public long countDistinctCommittersByFilename(
            String filename, Date beginDate, Date endDate) {
        return countDistinctCommittersByFilename(filename,
                beginDate, endDate, true);
    }

    public long countDistinctCommittersByFilename(
            String filename, Date beginDate, Date endDate, boolean onlyFixed) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(COUNT_DISTINCT_COMMITERS_BY_FILE_NAME);

        selectParams.add(filename);

        if (beginDate != null && endDate != null) { // from begin to end
            sql.append(FILTER_BY_ISSUE_FIX_DATE);
            selectParams.add(beginDate);
            selectParams.add(endDate);
        } else if (beginDate != null) { // from begin
            sql.append(FILTER_BY_AFTER_ISSUE_FIX_DATE);
            selectParams.add(beginDate);
        } else if (endDate != null) { // until end
            sql.append(FILTER_BY_BEFORE_ISSUE_FIX_DATE);
            selectParams.add(endDate);
        }

        if (onlyFixed) {
            sql.append(FIXED_ISSUES_ONLY);
        }

        Long count = (Long) dao.selectNativeOneWithParams(sql.toString(), selectParams.toArray());

        return count != null ? count : 0l;
    }

    // COMMITS /////////////////////////////////////////////////////////////////
    public long countCommitsByFilename(
            String filename, Date beginDate, Date endDate, Collection<Integer> issues) {
        return countCommitsByFilename(filename, null, beginDate, endDate, issues);
    }

    public long countCommitsByFilename(
            String filename, String user, Date beginDate, Date endDate, Collection<Integer> issues) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(COUNT_COMMITS_BY_FILE_NAME);

        selectParams.add(filename);

        if (beginDate != null && endDate != null) { // from begin to end
            sql.append(FILTER_BY_ISSUE_FIX_DATE);
            selectParams.add(beginDate);
            selectParams.add(endDate);
        } else if (beginDate != null) { // from begin
            sql.append(FILTER_BY_AFTER_ISSUE_FIX_DATE);
            selectParams.add(beginDate);
        } else if (endDate != null) { // until end
            sql.append(FILTER_BY_BEFORE_ISSUE_FIX_DATE);
            selectParams.add(endDate);
        }

        if (user != null) {
            sql.append(FILTER_BY_USER_NAME);
            selectParams.add(user);
        }

        filterByIssues(issues, sql);

        Long count = (Long) dao.selectNativeOneWithParams(sql.toString(), selectParams.toArray());

        return count != null ? count : 0l;
    }

    // CORE CHURN //////////////////////////////////////////////////////////////
    public CodeChurn sumCodeChurnByFilename(
            String filename, Date beginDate, Date endDate) {
        return sumCodeChurnByFilename(filename, null, beginDate, endDate, null, true);
    }

    public CodeChurn sumCodeChurnByFilename(
            String filename, String user, Date beginDate, Date endDate) {
        return sumCodeChurnByFilename(filename, user, beginDate, endDate, null, true);
    }

    public CodeChurn sumCodeChurnByFilename(
            String filename, Date beginDate, Date endDate, Collection<Integer> issues) {
        return sumCodeChurnByFilename(filename, null, beginDate, endDate, issues, true);
    }

    public CodeChurn sumCodeChurnByFilename(
            String filename, String user, Date beginDate, Date endDate, Collection<Integer> issues) {
        return sumCodeChurnByFilename(filename, user, beginDate, endDate, issues, true);
    }

    public CodeChurn sumCodeChurnByFilename(
            String fileName, String user, Date beginDate, Date endDate,
            Collection<Integer> issues, boolean onlyFixed) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder(BEGIN_SUM_ADD_AND_DEL_LINES);
        sql.append(SUM_ADD_AND_DEL_LINES_BY_FILE_NAME);

        selectParams.add(fileName);

        if (beginDate != null) {
            sql.append(FILTER_BY_AFTER_ISSUE_FIX_DATE);
            selectParams.add(beginDate);
        }

        if (endDate != null) {
            sql.append(FILTER_BY_BEFORE_ISSUE_FIX_DATE);
            selectParams.add(endDate);
        }

        if (onlyFixed) {
            sql.append(FIXED_ISSUES_ONLY);
        }

        if (user != null) {
            sql.append(FILTER_BY_USER_NAME);
            selectParams.add(user);
        }

        filterByIssues(issues, sql);

        sql.append(END_SUM_ADD_AND_DEL_LINES);

        List<Object[]> sum = dao.selectNativeWithParams(sql.toString(),
                selectParams.toArray());

        return new CodeChurn(fileName,
                ((BigDecimal) sum.get(0)[0]).longValue(), ((BigDecimal) sum.get(0)[1]).longValue());
    }

    public CodeChurn sumCodeChurnByFilename(
            String filename, String version) {
        return sumCodeChurnByFilename(filename, null, version, null, true);
    }

    public CodeChurn sumCodeChurnByFilename(
            String filename, String user, String version) {
        return sumCodeChurnByFilename(filename, user, version, null, true);
    }

    public CodeChurn sumCodeChurnByFilename(
            String filename, String version, Collection<Integer> issues) {
        return sumCodeChurnByFilename(filename, null, version, issues, true);
    }

    public CodeChurn sumCodeChurnByFilename(
            String filename, String user, String version, Collection<Integer> issues) {
        return sumCodeChurnByFilename(filename, user, version, issues, true);
    }

    public CodeChurn sumCodeChurnByFilename(
            String fileName, String user, String version,
            Collection<Integer> issues, boolean onlyFixed) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder(BEGIN_SUM_ADD_AND_DEL_LINES);
        sql.append(SUM_ADD_AND_DEL_LINES_BY_FILE_NAME);

        selectParams.add(fileName);

        sql.append(FILTER_BY_ISSUE_FIX_MAJOR_VERSION);
        selectParams.add(version);

        if (user != null) {
            sql.append(FILTER_BY_USER_NAME);
            selectParams.add(user);
        }

        filterByIssues(issues, sql);

        sql.append(END_SUM_ADD_AND_DEL_LINES);

        List<Object[]> sum = dao.selectNativeWithParams(sql.toString(),
                selectParams.toArray());

        return new CodeChurn(fileName,
                ((BigDecimal) sum.get(0)[0]).longValue(), ((BigDecimal) sum.get(0)[1]).longValue());
    }

    // TODO remover outros métodos com mesmo nome
    public CodeChurn sumCodeChurnByFilename(
            File file, Commit commit, Committer committer) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder(BEGIN_SUM_ADD_AND_DEL_LINES);
        sql.append(SUM_ADD_AND_DEL_LINES_BY_FILE_NAME);
        selectParams.add(file.getFileName());

        sql.append(FILTER_BY_COMMIT);
        selectParams.add(commit.getId());

        // TODO está por nome, pode haver usuários iguais
        sql.append(FILTER_BY_USER_NAME);
        selectParams.add(committer.getName());

        sql.append(END_SUM_ADD_AND_DEL_LINES);

        List<Object[]> sum = dao.selectNativeWithParams(sql.toString(),
                selectParams.toArray());

        return new CodeChurn(file.getFileName(),
                ((BigDecimal) sum.get(0)[0]).longValue(), ((BigDecimal) sum.get(0)[1]).longValue());
    }

//    public CodeChurn sumCodeChurnByFilename(
//            File file, Commit commit, Committer committer, int numberOfLastIssues) {
//        List<Object> selectParams = new ArrayList<>();
//
//        StringBuilder sql = new StringBuilder();
//        sql.append(SUM_ADD_AND_DEL_LINES_BY_FILE_NAME);
//        selectParams.add(numberOfLastIssues);
//        selectParams.add(file.getFileName());
//
//        sql.append(FILTER_BY_COMMIT);
//        selectParams.add(commit.getId());
//
//        // TODO está por nome, pode haver usuários iguais
//        sql.append(FILTER_BY_USER_NAME);
//        selectParams.add(committer.getName());
//
//        sql.append(FIXED_ISSUES_ONLY);
//
//        List<Object[]> sum = dao.selectNativeWithParams(sql.toString(),
//                selectParams.toArray());
//
//        return new CodeChurn(file.getFileName(),
//                ((BigDecimal) sum.get(0)[0]).longValue(), ((BigDecimal) sum.get(0)[1]).longValue());
//    }

    // TODO remover outros métodos com mesmo nome
    public CodeChurn sumCodeChurnByFilename(
            File file, Commit commit) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder(BEGIN_SUM_ADD_AND_DEL_LINES);
        sql.append(SUM_ADD_AND_DEL_LINES_BY_FILE_NAME);
        selectParams.add(file.getFileName());

        sql.append(FILTER_BY_COMMIT);
        selectParams.add(commit.getId());

        sql.append(END_SUM_ADD_AND_DEL_LINES);

        List<Object[]> sum = dao.selectNativeWithParams(sql.toString(),
                selectParams.toArray());

        return new CodeChurn(file.getFileName(),
                ((BigDecimal) sum.get(0)[0]).longValue(), ((BigDecimal) sum.get(0)[1]).longValue());
    }

    public CodeChurn sumCodeChurnByFilename(
            File file, Commit commit, int numberOfLastIssues) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder(BEGIN_SUM_ADD_AND_DEL_LINES);
        sql.append(SUM_ADD_AND_DEL_LINES_BY_FILE_NAME);
        selectParams.add(numberOfLastIssues);
        selectParams.add(file.getFileName());

        sql.append(FILTER_BY_COMMIT);
        selectParams.add(commit.getId());

        sql.append(END_SUM_ADD_AND_DEL_LINES);

        List<Object[]> sum = dao.selectNativeWithParams(sql.toString(),
                selectParams.toArray());

        return new CodeChurn(file.getFileName(),
                ((BigDecimal) sum.get(0)[0]).longValue(), ((BigDecimal) sum.get(0)[1]).longValue());
    }

    public CodeChurn sumCodeChurnByFilename(
            String filename, String user, Integer index, Integer quantity) {
        return sumCodeChurnByFilename(filename, user, index, quantity, null, true);
    }

    public CodeChurn sumCodeChurnByFilename(
            String filename, Integer index, Integer quantity) {
        return sumCodeChurnByFilename(filename, null, index, quantity, null, true);
    }

    public CodeChurn sumCodeChurnByFilename(
            String fileName, String user, Integer index, Integer quantity,
            Collection<Integer> issues, boolean onlyFixed) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(SUM_ADD_AND_DEL_LINES_BY_FILE_NAME_AND_LIMIT_OFFSET);
        selectParams.add(quantity);
        selectParams.add(quantity * index);
        selectParams.add(fileName);

        if (onlyFixed) {
            sql.append(FIXED_ISSUES_ONLY);
        }

        if (user != null) {
            sql.append(FILTER_BY_USER_NAME);
            selectParams.add(user);
        }

        filterByIssues(issues, sql);

        List<Object[]> sum = dao.selectNativeWithParams(sql.toString(),
                selectParams.toArray());

        return new CodeChurn(fileName,
                ((BigDecimal) sum.get(0)[0]).longValue(), ((BigDecimal) sum.get(0)[1]).longValue());
    }

    public CodeChurn sumCummulativeCodeChurnByFilename(
            String filename, String version, Collection<Integer> issues) {
        return sumCodeChurnByFilename(filename, null, version, issues, true);
    }

    public CodeChurn sumCummulativeCodeChurnByFilename(
            String filename, String user, String version, Collection<Integer> issues) {
        return sumCodeChurnByFilename(filename, user, version, issues, true);
    }

    public CodeChurn sumCummulativeCodeChurnByFilename(
            String fileName, String user, String version,
            Collection<Integer> issues, boolean onlyFixed) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder(BEGIN_SUM_ADD_AND_DEL_LINES);
        sql.append(SUM_ADD_AND_DEL_LINES_BY_FILE_NAME);

        selectParams.add(fileName);

        sql.append(FILTER_BY_BEFORE_ISSUE_FIX_MAJOR_VERSION_INCLUSIVE);
        selectParams.add(version);

        if (onlyFixed) {
            sql.append(FIXED_ISSUES_ONLY);
        }

        if (user != null) {
            sql.append(FILTER_BY_USER_NAME);
            selectParams.add(user);
        }

        filterByIssues(issues, sql);

        sql.append(END_SUM_ADD_AND_DEL_LINES);

        List<Object[]> sum = dao.selectNativeWithParams(sql.toString(),
                selectParams.toArray());

        return new CodeChurn(fileName,
                ((BigDecimal) sum.get(0)[0]).longValue(), ((BigDecimal) sum.get(0)[1]).longValue());
    }

    // TODO remover e substituir pelo método de baixo
    public List<FilePath> selectFilesByCommitId(Integer commitId) {
// TODO comment for optimize performance
//        return selectFilesByCommitId(commitId, null);
//    }
//
//    public List<FilePath> selectFilesByCommitId(Integer commitId, String[] filesExtension) {
//        List<Object> selectParams = new ArrayList<>();
//
//        StringBuilder sql = new StringBuilder(SELECT_FILES_PATH_BY_ISSUE);

//        selectParams.add(commitId);
//
//        if (filesExtension != null) {
//            for (String extension : filesExtension) {
//                sql.append(FILTER_BY_LIKE_FILE_NAME);
//                selectParams.add("%" + extension);
//            }
//        }

        List<Object[]> rawFilesPath
//                = dao.selectNativeWithParams(sql.toString(), selectParams.toArray());
                = dao.selectNativeWithParams(SELECT_FILES_PATH_BY_COMMIT_ID, new Object[]{commitId});

        List<FilePath> files = new ArrayList<>();
        for (Object[] row : rawFilesPath) {
            FilePath filePath = new FilePath(commitId, (Integer) row[0], (String) row[1]);
            files.add(filePath);
        }

        return files;
    }

    public List<File> selectCommitFiles(Commit commit) {
        List<Object[]> rawFilesPath
                = dao.selectNativeWithParams(SELECT_FILES_PATH_BY_COMMIT_ID, new Object[]{commit.getId()});

        List<File> files = new ArrayList<>();
        for (Object[] row : rawFilesPath) {
            File file = new File((Integer) row[0], (String) row[1]);
            file.addCommit(commit);
            files.add(file);
        }

        return files;
    }

    public Set<Committer> selectCommitters(
            String file, Date beginDate, Date endDate) {
        return selectCommitters(file, beginDate, endDate, true);
    }

    public Set<Committer> selectCommitters(
            String file, Date beginDate, Date endDate, boolean onlyFixed) {

        List<Object> selectParams = new ArrayList<>();
        if (file == null) {
            throw new IllegalArgumentException("Pair file could not be null");
        }

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_COMMITTERS_OF_FILE_BY_DATE);

        selectParams.add(file);

        if (onlyFixed) {
            sql.append(FIXED_ISSUES_ONLY);
        }

        if (beginDate != null) {
            sql.append(FILTER_BY_AFTER_ISSUE_FIX_DATE);
            selectParams.add(beginDate);
        }

        if (endDate != null) {
            sql.append(FILTER_BY_BEFORE_ISSUE_FIX_DATE);
            selectParams.add(endDate);
        }

        List<Object[]> committers = dao.selectNativeWithParams(
                sql.toString(), selectParams.toArray());

        Set<Committer> commitersList = new HashSet<>(committers.size());
        for (Object[] row : committers) {
            commitersList.add(getRowAsCommiter(row));
        }

        return commitersList;
    }

    public Set<Committer> selectCommitters(String file, String fixVersion) {

        List<Object> selectParams = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_COMMITTERS_OF_FILE_BY_FIX_MAJOR_VERSION);
        sql.append(FIXED_ISSUES_ONLY);
        selectParams.add(file);
        selectParams.add(fixVersion);

        List<Object[]> committers = dao.selectNativeWithParams(
                sql.toString(), selectParams.toArray());

        Set<Committer> commitersList = new HashSet<>(committers.size());
        for (Object row[] : committers) {
            Committer committer = null;
            if (row != null) {
                Integer committerId = (Integer) row[0];
                String committerName = (String) row[1];
                String committerEmail = (String) row[2];

                committer = new Committer(committerId, committerName, committerEmail);
            }
            commitersList.add(committer);
        }

        return commitersList;
    }

    public Set<Committer> selectCommitters(String file, Set<Integer> issues) {

        List<Object> selectParams = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_COMMITTERS_BY_FILENAME);
        sql.append(FIXED_ISSUES_ONLY);
        selectParams.add(file);

        filterByIssues(issues, sql);

        List<Object[]> committers = dao.selectNativeWithParams(
                sql.toString(), selectParams.toArray());

        Set<Committer> commitersList = new HashSet<>(committers.size());
        for (Object row[] : committers) {
            Committer committer = null;
            if (row != null) {
                committer = getRowAsCommiter(row);
            }
            commitersList.add(committer);
        }

        return commitersList;
    }

    public Committer getRowAsCommiter(Object[] row) {
        Committer committer;
        Integer committerId = (Integer) row[0];
        String committerName = (String) row[1];
        String committerEmail = (String) row[2];
        committer = new Committer(committerId, committerName, committerEmail);
        return committer;
    }

    public Long calculeCommitters(
            String file, String fixVersion) {
        return calculeCommitters(file, null, fixVersion);
    }

    public Long calculeCommitters(
            String file, Integer issue, String fixVersion) {

        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(COUNT_COMMITTERS_OF_FILE);
        selectParams.add(file);

        sql.append(FILTER_BY_ISSUE_FIX_MAJOR_VERSION);
        selectParams.add(fixVersion);

        sql.append(FIXED_ISSUES_ONLY);

        if (issue != null) {
            sql.append(FILTER_BY_ISSUE);
            selectParams.add(issue);
        }

        Long committers = dao.selectNativeOneWithParams(
                sql.toString(), selectParams.toArray());

        return committers;
    }

    public Long calculeCummulativeCommitters(
            String file, Integer issue, String fixVersion) {

        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(COUNT_COMMITTERS_OF_FILE);
        selectParams.add(file);

        sql.append(FILTER_BY_BEFORE_ISSUE_FIX_MAJOR_VERSION_EXCLUSIVE);
        selectParams.add(fixVersion);

        sql.append(FIXED_ISSUES_ONLY);

        Long committers = dao.selectNativeOneWithParams(
                sql.toString(), selectParams.toArray());

        return committers;
    }

    public Long calculeCummulativeCommitters(
            String file, Integer issue, Set<Integer> issues) {

        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(COUNT_COMMITTERS_OF_FILE);
        selectParams.add(file);

        filterByIssues(issues, sql);

        sql.append(FIXED_ISSUES_ONLY);

        Long committers = dao.selectNativeOneWithParams(
                sql.toString(), selectParams.toArray());

        return committers;
    }

    public long calculeIssueReopenedTimes(Integer issue) {
        Long count = (Long) dao.selectNativeOneWithParams(COUNT_ISSUE_REOPENED_TIMES, new Object[]{"Reopened", "Status", issue});

        return count;
    }

    public Map<String, Long> calculeNumberOfIssuesGroupedByType(String filename, String version) {
        List<Object[]> rawFilesPath
                = dao.selectNativeWithParams(COUNT_ISSUES_TYPES, new Object[]{filename, version});

        Map<String, Long> result = new HashMap<>(rawFilesPath.size());
        for (Object[] row : rawFilesPath) {
            Long count = (Long) row[0];
            String type = (String) row[1];
            result.put(type, count);
        }
        return result;
    }

    public Map<String, Long> calculeNumberOfIssuesGroupedByType(String filename, Set<Integer> issues) {
        StringBuilder sql = new StringBuilder(COUNT_ISSUES_TYPES);
        filterByIssues(issues, sql);

        List<Object[]> rawFilesPath
                = dao.selectNativeWithParams(sql.toString(), new Object[]{filename});

        Map<String, Long> result = new HashMap<>(rawFilesPath.size());
        for (Object[] row : rawFilesPath) {
            Long count = (Long) row[0];
            String type = (String) row[1];
            result.put(type, count);
        }
        return result;
    }

    public CodeChurn calculeAddDelChanges(String filename, Integer issue, String version) {
        return calculeAddDelChanges(filename, issue, null, version);
    }

    public CodeChurn calculeAddDelChanges(String filename, Integer issue, Integer commit) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(GET_ADD_AND_DEL_LINES_BY_FILE_NAME_AND_ISSUE_AND_COMMIT);
        selectParams.add(filename);
        selectParams.add(issue);
        selectParams.add(commit);

        List<Object[]> sum = dao.selectNativeWithParams(sql.toString(),
                selectParams.toArray());
        Long additions = sum.get(0)[0] == null ? 0l : ((Number) sum.get(0)[0]).longValue();
        Long deletions = sum.get(0)[1] == null ? 0l : ((Number) sum.get(0)[1]).longValue();

        return new CodeChurn(filename, additions, deletions);
    }

    public CodeChurn calculeAddDelChanges(String filename, Integer issue, Integer commit, String version) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder(BEGIN_SUM_ADD_AND_DEL_LINES);

        sql.append(SUM_ADD_AND_DEL_LINES_BY_FILE_NAME);
        selectParams.add(filename);

        sql.append(FILTER_BY_ISSUE_FIX_MAJOR_VERSION);
        selectParams.add(version);

        if (commit != null) {
            sql.append(FILTER_BY_COMMIT);
            selectParams.add(commit);
        }

        if (issue != null) {
            sql.append(FILTER_BY_ISSUE);
            selectParams.add(issue);
        }

        sql.append(END_SUM_ADD_AND_DEL_LINES);

        List<Object[]> sum = dao.selectNativeWithParams(sql.toString(),
                selectParams.toArray());
        Long additions = sum.get(0)[0] == null ? 0l : ((Number) sum.get(0)[0]).longValue();
        Long deletions = sum.get(0)[1] == null ? 0l : ((Number) sum.get(0)[1]).longValue();

        return new CodeChurn(filename, additions, deletions);
    }

    public CodeChurn calculeAddDelChanges(String filename, Integer issue, Integer commit, Set<Integer> issues) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder(BEGIN_SUM_ADD_AND_DEL_LINES);

        sql.append(SUM_ADD_AND_DEL_LINES_BY_FILE_NAME);
        selectParams.add(filename);

        if (commit != null) {
            sql.append(FILTER_BY_COMMIT);
            selectParams.add(commit);
        }

        if (issue != null) {
            sql.append(FILTER_BY_ISSUE);
            selectParams.add(issue);
        }

        sql.append(END_SUM_ADD_AND_DEL_LINES);

        filterByIssues(issues, sql);

        List<Object[]> sum = dao.selectNativeWithParams(sql.toString(),
                selectParams.toArray());
        Long additions = sum.get(0)[0] == null ? 0l : ((Number) sum.get(0)[0]).longValue();
        Long deletions = sum.get(0)[1] == null ? 0l : ((Number) sum.get(0)[1]).longValue();

        return new CodeChurn(filename, additions, deletions);
    }

    public List<Integer> selectIssues(String filename) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_ISSUES_BY_FILENAME);
        selectParams.add(filename);

        sql.append(ORDER_BY_FIXED_ON);

        List<Integer> issues = dao.selectNativeWithParams(sql.toString(), selectParams.toArray());

        return issues;
    }

    public List<Integer> selectIssues(String filename, String fixVersion) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_ISSUES_BY_FIX_MAJOR_VERSION);
        selectParams.add(filename);
        selectParams.add(fixVersion);

        sql.append(ORDER_BY_SUBMITTED_ON);

        List<Integer> issues = dao.selectNativeWithParams(sql.toString(), selectParams.toArray());

        return issues;
    }

    public List<Integer> selectIssues(String filename, Set<Integer> issues) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_ISSUES);
        selectParams.add(filename);
        filterByIssues(issues, sql);
        sql.append(ORDER_BY_SUBMITTED_ON);

        List<Integer> issuesFileChanged = dao.selectNativeWithParams(sql.toString(), selectParams.toArray());

        return issuesFileChanged;
    }

    public long calculeCummulativeCommits(String file, String fixVersion) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(COUNT_COMMITS_BY_FILE_NAME);

        selectParams.add(file);

        sql.append(FILTER_BY_BEFORE_ISSUE_FIX_MAJOR_VERSION_EXCLUSIVE);
        selectParams.add(fixVersion);

        Long count = dao.selectNativeOneWithParams(sql.toString(), selectParams.toArray());

        return count != null ? count : 0l;
    }

    public long calculeCummulativeCommits(String file, Set<Integer> issues) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(COUNT_COMMITS_BY_FILE_NAME);

        selectParams.add(file);

        filterByIssues(issues, sql);

        Long count = dao.selectNativeOneWithParams(sql.toString(), selectParams.toArray());

        return count != null ? count : 0l;
    }

    public long calculeCommits(String file, String fixVersion) {
        return calculeCommitsByFixVersion(file, null, null, fixVersion, null);
    }

    public long calculeCommits(String file, Integer issue, String fixVersion) {
        return calculeCommitsByFixVersion(file, null, issue, fixVersion, null);
    }

    public long calculeCommits(String file, String fixVersion,
            Collection<Integer> issues) {
        return calculeCommitsByFixVersion(file, null, null, fixVersion, issues);
    }

    public long calculeCommits(String file, String user, String fixVersion) {
        return calculeCommitsByFixVersion(file, user, null, fixVersion, null);
    }

    public long calculeCommits(String file, String user,
            String fixVersion, Collection<Integer> issues) {
        return calculeCommitsByFixVersion(file, user, null, fixVersion, issues);
    }

    public long calculeCommitsByFixVersion(
            String file, String user, Integer issue, String fixVersion,
            Collection<Integer> issues) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(COUNT_COMMITS_BY_FILE_NAME);
        selectParams.add(file);

        sql.append(FILTER_BY_ISSUE_FIX_MAJOR_VERSION);
        selectParams.add(fixVersion);

        sql.append(FIXED_ISSUES_ONLY);

        if (issue != null) {
            sql.append(FILTER_BY_ISSUE);
            selectParams.add(issue);
        }

        if (user != null) {
            sql.append(FILTER_BY_USER_NAME);
            selectParams.add(user);
        }

        filterByIssues(issues, sql);

        Long count = dao.selectNativeOneWithParams(sql.toString(), selectParams.toArray());

        return count != null ? count : 0l;
    }

    public long calculeCommits(String file, Integer index, Integer quantity) {
        return calculeCommitsByIndex(file, null, null, index, quantity, null);
    }

    public long calculeCommits(String file, String user, Integer index, Integer quantity) {
        return calculeCommitsByIndex(file, user, null, index, quantity, null);
    }

    public long calculeCommitsByIndex(
            String file, String user, Integer issue, Integer index, Integer quantity,
            Collection<Integer> issues) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(COUNT_COMMITS_BY_FILE_NAME_AND_LIMIT_OFFSET);
        selectParams.add(quantity);
        selectParams.add(quantity * index);
        selectParams.add(file);

        sql.append(FIXED_ISSUES_ONLY);

        if (issue != null) {
            sql.append(FILTER_BY_ISSUE);
            selectParams.add(issue);
        }

        if (user != null) {
            sql.append(FILTER_BY_USER_NAME);
            selectParams.add(user);
        }

        filterByIssues(issues, sql);

        Long count = dao.selectNativeOneWithParams(sql.toString(), selectParams.toArray());

        return count != null ? count : 0l;
    }

    public int calculeFileAgeInDays(String filename, String fixVersion) {
        return calculeFileAgeInDays(filename, null, fixVersion);
    }

    public int calculeFileAgeInDays(String filename, Integer issue) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_RELEASE_MIN_MAX_COMMIT_DATE);
        sql.append(FIXED_ISSUES_ONLY);

        selectParams.add(filename);

        sql.append(FILTER_BY_BEFORE_ISSUE_FIX_DATE_OF_ISSUE_ID);
        selectParams.add(issue);

        List<Object[]> minMaxDateList = dao.selectNativeWithParams(sql.toString(), selectParams.toArray());
        Object[] minMaxDate = minMaxDateList.get(0);

        if (minMaxDate[0] == null || minMaxDate[1] == null) {
            return 0;
        }

        java.sql.Timestamp minDate = (java.sql.Timestamp) minMaxDate[0];
        java.sql.Timestamp maxDate = (java.sql.Timestamp) minMaxDate[1];

        LocalDate createdAt = new LocalDate(minDate.getTime());
        LocalDate finalDate = new LocalDate(maxDate.getTime());
        Days age = Days.daysBetween(createdAt, finalDate);

        return age.getDays();
    }

    public int calculeFileAgeInDays(String filename, Integer issue, String fixVersion) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_RELEASE_MIN_MAX_COMMIT_DATE);
        sql.append(FIXED_ISSUES_ONLY);

        selectParams.add(filename);

        sql.append(FILTER_BY_ISSUE_FIX_MAJOR_VERSION);
        selectParams.add(fixVersion);

        if (issue != null) {
            sql.append(FILTER_BY_BEFORE_ISSUE_FIX_DATE_OF_ISSUE_ID);
            selectParams.add(issue);
        }

        List<Object[]> minMaxDateList = dao.selectNativeWithParams(sql.toString(), selectParams.toArray());
        Object[] minMaxDate = minMaxDateList.get(0);

        if (minMaxDate[0] == null || minMaxDate[1] == null) {
            return 0;
        }

        java.sql.Timestamp minDate = (java.sql.Timestamp) minMaxDate[0];
        java.sql.Timestamp maxDate = (java.sql.Timestamp) minMaxDate[1];

        LocalDate createdAt = new LocalDate(minDate.getTime());
        LocalDate finalDate = new LocalDate(maxDate.getTime());
        Days age = Days.daysBetween(createdAt, finalDate);

        return age.getDays();
    }

    public int calculeFileAgeInDays(String filename, Integer issue, Set<Integer> issues) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_RELEASE_MIN_MAX_COMMIT_DATE);
        sql.append(FIXED_ISSUES_ONLY);

        selectParams.add(filename);

        if (issue != null) {
            sql.append(FILTER_BY_BEFORE_ISSUE_FIX_DATE_OF_ISSUE_ID);
            selectParams.add(issue);
        }

        filterByIssues(issues, sql);

        List<Object[]> minMaxDateList = dao.selectNativeWithParams(sql.toString(), selectParams.toArray());
        Object[] minMaxDate = minMaxDateList.get(0);

        if (minMaxDate[0] == null || minMaxDate[1] == null) {
            return 0;
        }

        java.sql.Timestamp minDate = (java.sql.Timestamp) minMaxDate[0];
        java.sql.Timestamp maxDate = (java.sql.Timestamp) minMaxDate[1];

        LocalDate createdAt = new LocalDate(minDate.getTime());
        LocalDate finalDate = new LocalDate(maxDate.getTime());
        Days age = Days.daysBetween(createdAt, finalDate);

        return age.getDays();
    }

    public int calculeTotalFileAgeInDays(String filename, String fixVersion) {
        return calculeTotalFileAgeInDays(filename, null, fixVersion);
    }

    public int calculeTotalFileAgeInDays(String filename, Integer issue, String fixVersion) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_RELEASE_MIN_MAX_COMMIT_DATE);
        sql.append(FIXED_ISSUES_ONLY);

        selectParams.add(filename);

        sql.append(FILTER_BY_BEFORE_ISSUE_FIX_MAJOR_VERSION_EXCLUSIVE);
        selectParams.add(fixVersion);

        if (issue != null) {
            sql.append(FILTER_BY_BEFORE_ISSUE_FIX_DATE_OF_ISSUE_ID);
            selectParams.add(issue);
        }

        List<Object[]> minMaxDateList = dao.selectNativeWithParams(sql.toString(), selectParams.toArray());
        Object[] minMaxDate = minMaxDateList.get(0);

        if (minMaxDate[0] == null || minMaxDate[1] == null) {
            return 0;
        }

        java.sql.Timestamp minDate = (java.sql.Timestamp) minMaxDate[0];
        java.sql.Timestamp maxDate = (java.sql.Timestamp) minMaxDate[1];

        LocalDate createdAt = new LocalDate(minDate.getTime());
        LocalDate finalDate = new LocalDate(maxDate.getTime());
        Days age = Days.daysBetween(createdAt, finalDate);

        return age.getDays();
    }

    public int calculeTotalFileAgeInDays(String filename, Integer issue) {
        List<Object> selectParams = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_RELEASE_MIN_MAX_COMMIT_DATE);
        sql.append(FIXED_ISSUES_ONLY);

        selectParams.add(filename);

        if (issue != null) {
            sql.append(FILTER_BY_BEFORE_ISSUE_FIX_DATE_OF_ISSUE_ID);
            selectParams.add(issue);
        }
//        filterByIssues(issues, sql);

        List<Object[]> minMaxDateList = dao.selectNativeWithParams(sql.toString(), selectParams.toArray());
        Object[] minMaxDate = minMaxDateList.get(0);

        if (minMaxDate[0] == null || minMaxDate[1] == null) {
            return 0;
        }

        java.sql.Timestamp minDate = (java.sql.Timestamp) minMaxDate[0];
        java.sql.Timestamp maxDate = (java.sql.Timestamp) minMaxDate[1];

        LocalDate createdAt = new LocalDate(minDate.getTime());
        LocalDate finalDate = new LocalDate(maxDate.getTime());
        Days age = Days.daysBetween(createdAt, finalDate);

        return age.getDays();
    }

    public Committer selectLastCommitter(String filename, Commit commit) {

        Object[] row
                = dao.selectNativeOneWithParams(SELECT_LAST_COMMITTER_BEFORE_COMMIT,
                        new Object[]{filename, commit.getId()});

        Committer committer = null;
        if (row != null) {
            Integer committerId = (Integer) row[0];
            String committerName = (String) row[1];
            String committerEmail = (String) row[2];

            committer = new Committer(committerId, committerName, committerEmail);
        }

        return committer;
    }

    public Committer selectLastCommitter(String filename, Commit commit, String fixVersion) {

        Object[] row
                = dao.selectNativeOneWithParams(SELECT_LAST_COMMITTER_BEFORE_COMMIT_AND_BEFORE_INCLUSIVE_VERSION,
                        new Object[]{filename, fixVersion, commit.getId()});

        Committer committer = null;
        if (row != null) {
            Integer committerId = (Integer) row[0];
            String committerName = (String) row[1];
            String committerEmail = (String) row[2];

            committer = new Committer(committerId, committerName, committerEmail);
        }

        return committer;
    }

    public Committer selectLastCommitter(String filename, Commit commit, Set<Integer> issues) {

        StringBuilder sql = new StringBuilder(SELECT_LAST_COMMITTER_BEFORE_ISSUE_BY_ISSUES_ID);
        filterByIssues(issues, sql);
        sql.append(" ORDER BY s.date DESC LIMIT 1");

        Object[] row
                = dao.selectNativeOneWithParams(sql.toString(),
                        new Object[]{filename, commit.getId()});

        Committer committer = null;
        if (row != null) {
            Integer committerId = (Integer) row[0];
            String committerName = (String) row[1];
            String committerEmail = (String) row[2];

            committer = new Committer(committerId, committerName, committerEmail);
        }

        return committer;
    }

    public Set<Commit> selectFilesAndCommitByFileAndIssue(String filename, Integer issue) {
        List<Object[]> rawFilesPath
                = dao.selectNativeWithParams(SELECT_COMMIT_AND_FILES_BY_FILENAME_AND_ISSUE, new Object[]{filename, issue});

        Map<Commit, Commit> commits = new LinkedHashMap<>();

        for (Object[] row : rawFilesPath) {

            Integer commitId = (Integer) row[0];
            String fileName = (String) row[1];
            Integer committerId = (Integer) row[2];
            String committerName = (String) row[3];
            String committerEmail = (String) row[4];
            java.sql.Timestamp commitDate = (java.sql.Timestamp) row[5];
            String hash = (String) row[6];

            Committer committer = new Committer(committerId, committerName, committerEmail);

            Commit commit = new Commit(commitId, hash, committer, new Date(commitDate.getTime()));

            if (commits.containsKey(commit)) {
                commits.get(commit).getFiles().add(new File(fileName));
            } else {
                commit.getFiles().add(new File(fileName));
                commits.put(commit, commit);
            }
        }

        return commits.keySet();
    }

    public String getRepository() {
        return repository;
    }

}
