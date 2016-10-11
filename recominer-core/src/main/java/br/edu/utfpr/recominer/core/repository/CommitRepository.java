package br.edu.utfpr.recominer.core.repository;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Committer;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Repository
public class CommitRepository extends JdbcRepository<Commit, Integer> {

    public CommitRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "commit_id";
    private static final String TABLE_NAME = "commits";

    public static final RowMapper<Commit> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                Commit commit = new Commit(rs.getInt("commit_id"));
                commit.setRevision(rs.getString("rev"));
                commit.setCommitDate(rs.getDate("date"));
                commit.setCommitter(new Committer(rs.getInt("committer_id")));
                return commit;
            };
    public static final RowUnmapper<Commit> ROW_UNMAPPER
            = (Commit commit) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("commit_id", commit.getId());
                mapping.put("rev", commit.getRevision());
                mapping.put("date", commit.getCommitDate());
                mapping.put("committer", commit.getCommitter().getId());
                return mapping;
            };

    /**
     * Gets all commits associated with non-fixed issues. The commits must have
     * a number of maximum files configured in 'recominer.configuration' table,
     * as key 'max_files_per_commit'.
     *
     * @return Commits of non-fixed issues.
     */
    public List<Commit> selectNewCommits() {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date"
                        + "  FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}_issues.issues i ON i.id = i2s.issue_id "
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND i.fixed_on IS NULL"
                        + " ORDER BY c.date DESC",
                        project),
                ROW_MAPPER,
                "max_files_per_commit");
    }

    /**
     * Gets all commits associated with non-fixed issues and commits that have
     * not Dataset generated yet. The commits must have a number of maximum
     * files configured in 'recominer.configuration' table, as key
     * 'max_files_per_commit'.
     *
     * @return Commits of non-fixed issues.
     */
    public List<Commit> selectNewCommitsForDataset() {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date"
                        + "  FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}_issues.issues i ON i.id = i2s.issue_id "
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND i.fixed_on IS NULL"
                        + "   AND c.commit_id NOT IN (SELECT DISTINCT(cm.commit_id) FROM {0}.contextual_metrics cm) "
                        + " ORDER BY c.date DESC",
                        project),
                ROW_MAPPER,
                "max_files_per_commit");
    }

    /**
     * Gets all commits associated with non-fixed issues and commits that have
     * not Dataset generated yet. The commits must have a number of maximum
     * files configured in 'recominer.configuration' table, as key
     * 'max_files_per_commit'.
     * In addition, the commits that contains only the files with name will be 
     * excluded from list.
     *
     * @param filenameFilter
     * @return Commits of non-fixed issues.
     */
    public List<Commit> selectNewCommitsForDataset(Set<String> filenameFilter) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filenames", filenameFilter);
        parameters.addValue("max_files_per_commit", "max_files_per_commit");
        
        return namedJdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date"
                        + "  FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}_issues.issues i ON i.id = i2s.issue_id "
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + "  JOIN {0}.files_commits fc ON fc.commit_id = c.commit_id"
                        + "  JOIN {0}.files f ON f.id = fc.file_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = :max_files_per_commit)"
                        + "   AND i.fixed_on IS NULL"
                        + "   AND f.file_path NOT IN (:filenames)"
                        + "   AND c.commit_id NOT IN (SELECT DISTINCT(cm.commit_id) FROM {0}.contextual_metrics cm) "
                        + " ORDER BY c.date DESC",
                        project),
                parameters,
                ROW_MAPPER);
    }

    /**
     * Gets all commits associated with non-fixed issues and commits that have
     * not metrics calculated yet. The commits must have a number of maximum
     * files configured in 'recominer.configuration' table, as key
     * 'max_files_per_commit'.
     *
     * @return Commits of non-fixed issues.
     */
    public List<Commit> selectNewCommitsForCalculator() {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date"
                        + "  FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}_issues.issues i ON i.id = i2s.issue_id "
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND i.fixed_on IS NULL"
                        + "   AND (c.commit_id NOT IN (SELECT DISTINCT(cm.commit_id) FROM {0}.commit_metrics cm) "
                        + "     OR c.commit_id NOT IN (SELECT DISTINCT(fm.commit_id) FROM {0}.file_metrics fm) "
                        + "     OR c.commit_id NOT IN (SELECT DISTINCT(im.commit_id) FROM {0}.issues_metrics im)) "
                        + " ORDER BY c.date DESC",
                        project),
                ROW_MAPPER,
                "max_files_per_commit");
    }

    /**
     * Gets all commits associated with non-fixed issues and commits that have
     * not metrics calculated yet. The commits must have a number of maximum
     * files configured in 'recominer.configuration' table, as key
     * 'max_files_per_commit'.
     * In addition, the commits that contains only the files with name will be 
     * excluded from list.
     *
     * @param filenameFilter
     * @return Commits of non-fixed issues.
     */
    public List<Commit> selectNewCommitsForCalculator(Set<String> filenameFilter) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filenames", filenameFilter);
        parameters.addValue("max_files_per_commit", "max_files_per_commit");
        return namedJdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT DISTINCT c.commit_id, c.rev, c.committer_id, c.date"
                        + "  FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}_issues.issues i ON i.id = i2s.issue_id "
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + "  JOIN {0}.files_commits fc ON fc.commit_id = c.commit_id"
                        + "  JOIN {0}.files f ON f.id = fc.file_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = :max_files_per_commit)"
                        + "   AND f.file_path NOT IN (:filenames)"
                        + "   AND i.fixed_on IS NULL"
                        + "   AND (c.commit_id NOT IN (SELECT DISTINCT(cm.commit_id) FROM {0}.commit_metrics cm) "
                        + "     OR c.commit_id NOT IN (SELECT DISTINCT(fm.commit_id) FROM {0}.file_metrics fm) "
                        + "     OR c.commit_id NOT IN (SELECT DISTINCT(im.commit_id) FROM {0}.issues_metrics im)) "
                        + " ORDER BY c.date DESC",
                        project),
                parameters,
                ROW_MAPPER
        );
    }

    /**
     * Gets all commits associated with non-fixed issues and commits that have
     * not Association Rule predictions yet. The commits must have a number of
     * maximum files configured in 'recominer.configuration' table, as key
     * 'max_files_per_commit'.
     *
     * @return Commits of non-fixed issues.
     */
    public List<Commit> selectNewCommitsForAssociationRule() {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date"
                        + "  FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}_issues.issues i ON i.id = i2s.issue_id "
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND i.fixed_on IS NULL"
                        + "   AND c.commit_id NOT IN (SELECT DISTINCT(ar.commit_id) FROM {0}.ar_prediction ar) "
                        + " ORDER BY c.date DESC",
                        project),
                ROW_MAPPER,
                "max_files_per_commit");
    }

    /**
     * Gets all commits associated with non-fixed issues and commits that have
     * not Association Rule predictions yet. The commits must have a number of
     * maximum files configured in 'recominer.configuration' table, as key
     * 'max_files_per_commit'.
     *
     * @return Commits of non-fixed issues.
     */
    public List<Commit> selectNewCommitsForClassification() {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date"
                        + "  FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}_issues.issues i ON i.id = i2s.issue_id "
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND i.fixed_on IS NULL"
                        + "   AND c.commit_id NOT IN (SELECT DISTINCT(ml.commit_id) FROM {0}.ml_prediction ml) "
                        + " ORDER BY c.date DESC",
                        project),
                ROW_MAPPER,
                "max_files_per_commit");
    }

    public List<Commit> selectCommitsOf(Issue issue) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND i2s.issue_id = ?", project),
                ROW_MAPPER,
                "max_files_per_commit", issue.getId());
    }

    public List<Commit> selectCommitsOf(String issueKey) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND i2s.issue_id = (SELECT iej.issue_id FROM {0}_issues.issues_ext_jira iej WHERE iej.issue_key = ?)", project),
                ROW_MAPPER,
                "max_files_per_commit", issueKey);
    }

    public List<Commit> selectCommitsOf(Issue issue, File file) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}.files_commits fc ON i2s.scmlog_id = fc.commit_id"
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND i2s.issue_id = ?"
                        + "   AND fc.file_id = ?", project),
                ROW_MAPPER,
                "max_files_per_commit", issue.getId(), file.getId());
    }

    public List<Commit> selectCommitsInLastVersionOf(Issue issue, File file, Commit commit) {
        return jdbcOperations.query(
                QueryUtils.getQueryForDatabase(
                        "SELECT c.commit_id, c.rev, c.committer_id, c.date FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}.issues_scmlog i2s ON c.commit_id = i2s.scmlog_id "
                        + "  JOIN {0}.files_commits fc ON i2s.scmlog_id = fc.commit_id"
                        + "  JOIN {0}_vcs.scmlog s ON s.id = c.commit_id"
                        + "  JOIN {0}.issues_fix_version ifv ON i2s.issue_id = ifv.issue_id"
                        + " WHERE s.num_files BETWEEN 1 AND (SELECT config.value FROM recominer.configuration config WHERE config.key = ?)"
                        + "   AND i2s.issue_id = ?"
                        + "   AND fc.file_id = ?"
                        + "   AND ifv.minor_version IN "
                        + "       (SELECT ifvo2.version_order "
                        + "          FROM {0}.issues_fix_version_order ifvo2 "
                        + "         WHERE ifvo2.minor_fix_version = "
                        + "           (SELECT ifv2.minor_fix_version "
                        + "              FROM {0}.issues_fix_version ifv2"
                        + "             WHERE ifv2.issue_id IN "
                        + "               (SELECT i2s.issue_id "
                        + "                  FROM {0}.issues_scmlog i2s "
                        + "	            WHERE i2s.scmlog_id = ?"
                        + "                    OR i2s.issue_id = ?"
                        + "                )"
                        + "	  ) "
                        + ")", project),
                ROW_MAPPER,
                "max_files_per_commit", issue.getId(), file.getId(), commit.getId());
    }

    public Commit selectWithCommitter(Commit commit) {
        return jdbcOperations.queryForObject(
                getQueryForSchema(
                        "SELECT c.commit_id, c.rev, c.committer_id, p.name, p.email, c.date"
                        + "  FROM " + getTable().getSchemaAndName() + " c"
                        + "  JOIN {0}_vcs.people p ON p.id = c.committer_id "
                        + " WHERE c.commit_id = ?"),
                (ResultSet rs, int rowNum) -> {
                    Commit c = new Commit(rs.getInt("commit_id"));
                    c.setRevision(rs.getString("rev"));
                    c.setCommitDate(rs.getDate("date"));
                    c.setCommitter(new Committer(rs.getInt("committer_id"), rs.getString("name"), rs.getString("email")));
                    return c;
                },
                commit.getId());
    }

}
