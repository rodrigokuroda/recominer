package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.core.model.IssueTrackerSystem;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import br.edu.utfpr.recominer.model.svn.Scmlog;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Named
public class AssociationProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AssociationProcessor.class);
    private static final String SQL_DELIMITER = ";";
    private static final String RECOMINER_TABLES_SCRIPT_NAME = "recominer_tables.sql";
    private static final String OPTIMIZATION_DDL_SCRIPT_NAME = "optimization_ddl.sql";
    private static final String OPTIMIZATION_DML_SCRIPT_NAME = "optimization_dml.sql";

    @Inject
    private JdbcTemplate template;

    public void process(Object item) throws Exception {
        final Project project = (Project) item;

        executeSqlScript(project, RECOMINER_TABLES_SCRIPT_NAME);

        final List<Scmlog> commitsToAnalyze;

        final RowMapper<Scmlog> mapper = (ResultSet rs, int rowNum) -> 
                new Scmlog(rs.getInt("id"), rs.getDate("date"), rs.getString("message"));
        final String selectScmlog = "SELECT id, date, message FROM {0}_vcs.scmlog";

        if (project.getLastCommitDateAnalyzed() != null) {
            commitsToAnalyze = template.query(
                    QueryUtils.getQueryForDatabase(selectScmlog + " WHERE date > ?", project),
                    mapper,
                    project.getLastCommitDateAnalyzed());
        } else {
            commitsToAnalyze = template.query(
                    QueryUtils.getQueryForDatabase(selectScmlog, project),
                    mapper);
        }

        if (!commitsToAnalyze.isEmpty()) {
            executeSqlScript(project, OPTIMIZATION_DDL_SCRIPT_NAME);
            executeSqlScript(project, OPTIMIZATION_DML_SCRIPT_NAME);

            JiraAggregator jiraAggregation = new JiraAggregator(template, project);
            jiraAggregation.aggregate(commitsToAnalyze);

            final java.sql.Timestamp lastIssueUpdate = template.queryForObject(
                    QueryUtils.getQueryForDatabase("SELECT MAX(updated_on) FROM {0}_issues.issues", project),
                    java.sql.Timestamp.class);

            // setting date of last commit analyzed
            project.setLastCommitDateAnalyzed(commitsToAnalyze.get(commitsToAnalyze.size() - 1).getDate());
            project.setLastIssueUpdateAnalyzed(lastIssueUpdate);
        }
    }

    private void executeSqlScript(Project project, String resourceFileName) {
        LOG.info("Executing script {}.", resourceFileName);
        // loads script from resource folder in project
        final InputStream script = this.getClass().getClassLoader().getResourceAsStream(resourceFileName);

        // Create SQL scanner
        final Scanner scanner = new Scanner(script).useDelimiter(SQL_DELIMITER);

        // Loop through the SQL file statements
        while (scanner.hasNext()) {

            final String rawSql = QueryUtils.removeComments(scanner.next());

            // Setting schema for query
            final String sql = QueryUtils.getQueryForDatabase(rawSql, project);

            if (StringUtils.isNotBlank(sql)) {
                try {
                    // filter by last date of issue/commit update for continuous minering
                    String whereScmlog = "";
                    String whereIssue = "";
                    final List<Object> params = new ArrayList<>();
                    if (sql.contains("WHERE_SCMLOG")) {
                        if (project.getLastCommitDateAnalyzed() != null) {
                            whereScmlog = "AND s.date > ?";
                            params.add(project.getLastCommitDateAnalyzed());
                        } else {
                            whereScmlog = "";
                        }

                    } else if (sql.contains("WHERE_ISSUE")) {
                        if (project.getLastIssueUpdateAnalyzed() != null) {
                            whereIssue = "AND i.updated_on > ?";
                            params.add(project.getLastIssueUpdateAnalyzed());
                        } else {
                            whereIssue = "";
                        }
                    }

                    final String replace = sql
                            .replace("WHERE_SCMLOG", whereScmlog)
                            .replace("WHERE_ISSUE", whereIssue)
                            .replace("ISSUE_TRACKER_SYSTEM", 
                                    project.getIssueTracker()
                                            .getSystem()
                                            .toString()
                                            .toUpperCase());

                    template.update(replace, params.toArray());
                } catch (Exception e) {
                    LOG.warn("SQL executed with errors: {}", sql);
                    if (e != null && e.getMessage() != null
                            && (e.getMessage().contains("Duplicate column name")
                            || e.getMessage().contains("Duplicate entry"))) {
                        LOG.warn("Script was already executed: {}.", e.getMessage());
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

    private boolean isIssuesFromBugzilla(Project project) {
        return project.getIssueTracker().getSystem() == IssueTrackerSystem.BUGZILLA;
    }
}
