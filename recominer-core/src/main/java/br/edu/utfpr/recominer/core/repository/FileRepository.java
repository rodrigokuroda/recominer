package br.edu.utfpr.recominer.core.repository;

import br.edu.utfpr.recominer.core.model.Cochange;
import br.edu.utfpr.recominer.core.model.CodeChurn;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.FilePair;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.model.PredictionFeedback;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import static br.edu.utfpr.recominer.core.util.QueryUtils.filterByIssues;
import java.sql.ResultSet;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Repository
public class FileRepository extends JdbcRepository<File, Integer> {

    private static final String ID_COLUMN = "file_id";
    private static final String TABLE_NAME = "files_commits";

    public static final RowMapper<File> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                File file = new File(rs.getInt("file_id"), rs.getString("file_path"));
                return file;
            };

    public static final RowUnmapper<File> ROW_UNMAPPER
            = (File file) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                return mapping;
            };

    // commons query fragment
    private final String FROM_TABLE;
    private final String JOIN_PEOPLE_COMMITER;
    private final String WHERE;

    private final String FILTER_BY_ISSUE_FIX_MAJOR_VERSION;

    private final String FILTER_BY_ISSUE_FIX_DATE;
    private final String FILTER_BY_BEFORE_ISSUE_FIX_DATE;
    private final String FILTER_BY_AFTER_ISSUE_FIX_DATE;

    private final String FILTER_BY_USER_NAME;
    private final String FIXED_ISSUES_ONLY;

    // complete queries (commons fragment + specific fragments for query)
    private final String COUNT_DISTINCT_COMMITERS_BY_FILE_NAME;

    private final String COUNT_COMMITS_BY_FILE_NAME;

    private final String COUNT_ISSUES_BY_FILENAME;

    public FileRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);

        FIXED_ISSUES_ONLY
                = " AND i.resolution = \"Fixed\""
                + " AND c.field = \"Resolution\""
                + " AND c.new_value = i.resolution";

        FROM_TABLE
                = "  FROM {0}_issues.issues i"
                + "  JOIN {0}_issues.changes c ON c.issue_id = i.id"
                + "  JOIN {0}.issues_scmlog i2s ON i2s.issue_id = i.id"
                + "  JOIN {0}_vcs.scmlog s ON s.id = i2s.scmlog_id"
                + "  JOIN {0}.commits com ON com.commit_id = i2s.scmlog_id";

        JOIN_PEOPLE_COMMITER = "  JOIN {0}_vcs.people p ON p.id = s.committer_id";

        WHERE = " WHERE com.file_path = ?"
                + "   AND com.date > i.submitted_on"
                + "   AND com.date < i.fixed_on"
                + "   AND EXISTS (SELECT 1 FROM {0}.issues_fix_version ifv WHERE ifv.issue_id = i.id)"
                + FIXED_ISSUES_ONLY;

        FILTER_BY_ISSUE_FIX_DATE
                = " AND c.changed_on BETWEEN ? AND ?";

        // avoid join, because has poor performance in this case
        FILTER_BY_ISSUE_FIX_MAJOR_VERSION
                = " AND i.id IN ("
                + " SELECT ifv.issue_id "
                + "   FROM {0}.issues_fix_version ifv "
                + "  WHERE ifv.major_fix_version = ?)";

        FILTER_BY_BEFORE_ISSUE_FIX_DATE
                = " AND c.changed_on <= ?";

        FILTER_BY_AFTER_ISSUE_FIX_DATE
                = " AND c.changed_on >= ?";

        FILTER_BY_USER_NAME
                = " AND p.name = ?";

        COUNT_ISSUES_BY_FILENAME
                = "SELECT COUNT(DISTINCT(i.id))"
                + FROM_TABLE
                + WHERE;

        COUNT_DISTINCT_COMMITERS_BY_FILE_NAME
                = "SELECT COUNT(DISTINCT(p.name))"
                + FROM_TABLE
                + JOIN_PEOPLE_COMMITER
                + WHERE;

        COUNT_COMMITS_BY_FILE_NAME
                = "SELECT COUNT(DISTINCT(s.id))"
                + FROM_TABLE
                + JOIN_PEOPLE_COMMITER
                + WHERE;

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

        Long count = jdbcOperations.queryForObject(
                sql.toString(),
                Long.class,
                selectParams.toArray());

        return count != null ? count : 0l;
    }

    public long calculeNumberOfIssues(File file) {
        StringBuilder sql = new StringBuilder();
        sql.append(COUNT_ISSUES_BY_FILENAME);

        sql.append(FIXED_ISSUES_ONLY);

        Long count = jdbcOperations.queryForObject(
                sql.toString(),
                Long.class,
                file.getFileName());

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

        Long count = jdbcOperations.queryForObject(
                sql.toString(),
                Long.class,
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

        Long count = jdbcOperations.queryForObject(
                sql.toString(),
                Long.class,
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

        Long count = jdbcOperations.queryForObject(sql.toString(),
                Long.class,
                selectParams.toArray());

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

        Long count = jdbcOperations.queryForObject(sql.toString(),
                Long.class,
                selectParams.toArray());

        return count != null ? count : 0l;
    }

    public CodeChurn calculeCodeChurn(File file, Commit commit) {
        return jdbcOperations.queryForObject(
                QueryUtils.getQueryForDatabase(
                        "SELECT SUM(lines_added) AS lines_added, SUM(lines_removed) AS lines_removed " // when change type is V, it is a moving action (2 records)
                        + " FROM {0}.files_commits "
                        + "WHERE file_id = ? "
                        + "  AND commit_id = ?"
                        + " GROUP BY CASE WHEN change_type = \"V\" THEN change_type ELSE id END", project),
                (ResultSet rs, int rowNum)
                -> new CodeChurn(
                        file,
                        rs.getLong("lines_added"),
                        rs.getLong("lines_removed")),
                file.getId(), commit.getId());
    }

    public Long calculeCommitters(File file, Commit commit) {
        return jdbcOperations.queryForObject(
                QueryUtils.getQueryForDatabase(
                        "SELECT COUNT(DISTINCT(committer_id)) as committers "
                        + " FROM {0}.files_commits fc "
                        + " JOIN {0}.commits c ON c.commit_id = fc.commit_id "
                        + "WHERE fc.file_id = ? AND fc.commit_id = ?", project),
                (ResultSet rs, int rowNum) -> rs.getLong("committers"),
                file.getId(), commit.getId());
    }

    public Long calculeCommits(File file, Commit commit) {
        return jdbcOperations.queryForObject(
                QueryUtils.getQueryForDatabase(
                        "SELECT COUNT(DISTINCT(commit_id)) as commits "
                        + " FROM {0}.files_commits "
                        + "WHERE file_id = ? "
                        + "  AND commit_id = ?", project),
                (ResultSet rs, int rowNum) -> rs.getLong("commits"),
                file.getId(), commit.getId());
    }

    public Long calculeTotalFileAgeInDays(File file, Commit commit) {
        return jdbcOperations.queryForObject(
                QueryUtils.getQueryForDatabase(
                        "SELECT MIN(c.date) as first_commit, "
                        + "     MAX(c.date) as last_commit "
                        + " FROM {0}.commits c "
                        + " JOIN {0}.files_commits fc ON fc.commit_id = c.commit_id "
                        + "WHERE fc.file_id = ? "
                        + "  AND c.date <= "
                        + "      (SELECT DISTINCT(c2.date) "
                        + "         FROM {0}.files_commits fc2 "
                        + "         JOIN {0}.commits c2 ON c2.commit_id = fc2.commit_id "
                        + "        WHERE fc2.file_id = ? "
                        + "          AND fc2.commit_id = ?)", project),
                (ResultSet rs, int rowNum) -> (long) ChronoUnit.DAYS.between(
                        rs.getTimestamp("first_commit").toLocalDateTime(),
                        rs.getTimestamp("last_commit").toLocalDateTime()),
                file.getId(), file.getId(), commit.getId());
    }

    public List<File> selectChangedFilesIn(Commit commit) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT f.file_id, f.file_path"
                        + "  FROM {0}.files_commits f"
                        + " WHERE f.commit_id = ?", project),
                ROW_MAPPER,
                commit.getId());
    }

    public List<File> selectProcessedChangedFilesIn(Commit commit) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT f.file_id, f.file_path"
                        + "  FROM {0}.files_commits f"
                        + "  JOIN (SELECT commit_id, file_id FROM {0}.ml_prediction  "
                        + "         UNION "
                        + "        SELECT commit_id, file_id FROM {0}.ar_prediction ap "
                        + "          JOIN {0}.fileset fs ON fs.id = ap.fileset_id) t ON t.commit_id = f.commit_id"
                        + "   AND t.file_id = f.file_id"
                        + " WHERE f.commit_id = ?", project),
                ROW_MAPPER,
                commit.getId());
    }

    public List<File> selectCalculatedChangedFilesIn(Commit commit) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT f.file_id, f.file_path"
                        + "  FROM {0}.files_commits f"
                        + "  JOIN {0}.file_metrics fm  ON fm.commit_id = f.commit_id AND fm.file_id = f.file_id "
                        + "   AND fm.file_id = f.file_id"
                        + " WHERE f.commit_id = ?", project),
                ROW_MAPPER,
                commit.getId());
    }

    public List<Cochange> selectCochangedFilesIn(Commit commit, File withFile) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT f.file_id, f.file_path"
                        + "  FROM {0}.files_commits f"
                        + " WHERE f.commit_id = ?"
                        + "   AND f.file_id <> ?", project),
                (ResultSet rs, int rowNum) -> new Cochange(new File(rs.getInt("file_id"), rs.getString("file_path")), commit),
                commit.getId(), withFile.getId());
    }

    public Long countFixedIssues(FilePair cochange) {
        return jdbcOperations.queryForObject(
                QueryUtils.getQueryForDatabase(
                        "SELECT * FROM {0}.files_commits fc "
                        + "  JOIN {0}.issues_scmlog i2s ON i2s.scmlog_id = fc.commit_id "
                        + " WHERE fc.file_id = ? "
                        + "   AND EXISTS (SELECT * FROM {0}.files_commits fc2 "
                        + "  JOIN {0}.issues_scmlog i2s2 ON i2s2.scmlog_id = fc2.commit_id "
                        + "  WHERE fc2.file_id = ? AND i2s2.issue_id = i2s.issue_id)", project),
                Long.class,
                cochange.getFile1().getId(), cochange.getFile2().getId());

    }

    public List<File> selectChangedFilesIn(Issue issue) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT fc.file_id, fc.file_path"
                        + "  FROM {0}.files_commits fc"
                        + "  JOIN {0}.issues_scmlog i2s ON i2s.scmlog_id = fc.commit_id"
                        + " WHERE i2s.issue_id = ?", project),
                (ResultSet rs, int rowNum) -> new File(rs.getInt("file_id"), rs.getString("file_path")),
                issue.getId());
    }

    public List<File> listFiles(File file) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT MAX(fc.file_id) as file_id, fc.file_path, "
                        + "     pfb.id AS prediction_feedback_id, "
                        + "     pfb.changed, "
                        + "     pfb.justification "
                        + "  FROM {0}.files_commits fc "
                        + "  JOIN {0}.issues_scmlog i2s ON i2s.scmlog_id = fc.commit_id "
                        + "  LEFT JOIN {0}.prediction_feedback pfb ON pfb.prediction_file_id = fc.file_id "
                        + " WHERE i2s.scmlog_id = (SELECT MAX(fc2.commit_id) FROM {0}.files_commits fc2 WHERE fc2.file_id = fc.file_id) "
                        + "   AND (fc.file_path LIKE \"%.java\" OR fc.file_path LIKE \"%.xml\")"
                        + "   AND fc.file_path <> ?"
                        + " GROUP BY fc.file_path, pfb.id "
                        + " ORDER BY fc.file_path", project),
                (ResultSet rs, int rowNum) -> {

                    final int feedbackId = rs.getInt("prediction_feedback_id");
                    final PredictionFeedback predictionFeedback = new PredictionFeedback(feedbackId == 0 ? null : feedbackId);
                    predictionFeedback.setChanged(rs.getBoolean("changed"));
                    predictionFeedback.setPredictionId(rs.getInt("file_id"));
                    predictionFeedback.setJustification(rs.getString("justification"));

                    return new File(rs.getInt("file_id"), rs.getString("file_path"), predictionFeedback);
                },
                file.getFileName());
    }

}
