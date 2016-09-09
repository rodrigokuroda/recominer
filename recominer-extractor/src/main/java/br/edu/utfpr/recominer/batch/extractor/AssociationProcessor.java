package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.core.model.IssueTrackerSystem;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.model.svn.Scmlog;
import br.edu.utfpr.recominer.core.util.QueryUtils;
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

/**
 *
 * @author Rodrigo T. Kuroda
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
        final String projectName = project.getProjectName().toLowerCase();

        executeSqlScript(project, RECOMINER_TABLES_SCRIPT_NAME);

        final List<Scmlog> commitsToAnalyze;
        if (project.getLastCommitDateAnalyzed() != null) {
            commitsToAnalyze = template.query(
                    QueryUtils.getQueryForDatabase("SELECT id, date, message FROM {0}_vcs.scmlog WHERE date > ?", projectName),
                    (ResultSet rs, int rowNum) -> new Scmlog(rs.getInt(1), rs.getDate(2), rs.getString(3)),
                    project.getLastCommitDateAnalyzed());
        } else {
            commitsToAnalyze = template.query(
                    QueryUtils.getQueryForDatabase("SELECT id, date, message FROM {0}_vcs.scmlog", projectName),
                    (ResultSet rs, int rowNum) -> new Scmlog(rs.getInt(1), rs.getDate(2), rs.getString(3)));
        }

        if (!commitsToAnalyze.isEmpty()) {
            if (project.getLastCommitDateAnalyzed() == null
                    && project.getLastIssueUpdateAnalyzed() == null) {
                executeSqlScript(project, OPTIMIZATION_DDL_SCRIPT_NAME);
            }
            executeSqlScript(project, OPTIMIZATION_DML_SCRIPT_NAME);

            JiraAggregation jiraAggregation = new JiraAggregation(template, project);
            jiraAggregation.aggregate(commitsToAnalyze);

            final java.sql.Timestamp lastIssueUpdate = template.queryForObject(
                    "SELECT MAX(updated_on) FROM " + projectName + "_issues.issues", java.sql.Timestamp.class);

            // setting date of last commit analyzed
            project.setLastCommitDateAnalyzed(commitsToAnalyze.get(commitsToAnalyze.size() - 1).getDate());
            project.setLastIssueUpdateAnalyzed(lastIssueUpdate);
        }
    }

    private void executeSqlScript(Project project, String resourceFileName) {
        LOG.info("Executing " + resourceFileName);
        // loads script from resource folder in project
        final InputStream script = this.getClass().getClassLoader().getResourceAsStream(resourceFileName);

        // Create SQL scanner
        final Scanner scanner = new Scanner(script).useDelimiter(SQL_DELIMITER);
        final String projectName = project.getProjectName().toLowerCase();

        // Loop through the SQL file statements
        while (scanner.hasNext()) {

            // Get statement from file, replacing all comments SQL command 
            // (starts with "--" and ends with "\n" or ";") 
            // in order to check if is a empty SQL command.
            final String rawSql = scanner.next().replaceAll("-{2,}.*(\n|;)", "");

            // Setting schema for query
            final String sql = rawSql.replace("{0}", project.getProjectName());

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
                            .replace("{WHERE_SCMLOG}", whereScmlog)
                            .replace("{WHERE_ISSUE}", whereIssue)
                            .replace("{ISSUE_TRACKER_SYSTEM}", project.getIssueTracker().getSystem().toString().toUpperCase());

                    template.update(replace, params.toArray());
                } catch (Exception e) {
                    LOG.warn(sql);
                    if (e != null && e.getMessage() != null
                            && (e.getMessage().contains("Duplicate column name")
                            || e.getMessage().contains("Duplicate entry"))) {
                        LOG.warn(e.getMessage());
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
