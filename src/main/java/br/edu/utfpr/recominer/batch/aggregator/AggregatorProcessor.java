package br.edu.utfpr.recominer.batch.aggregator;

import br.edu.utfpr.recominer.batch.bicho.IssueTrackerSystem;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.Mysql;
import br.edu.utfpr.recominer.model.issue.IssueScmlog;
import br.edu.utfpr.recominer.model.svn.Scmlog;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class AggregatorProcessor implements ItemProcessor {

    private static final Logger log = LogManager.getLogger();
    private static final String SQL_DELIMITER = ";";
    private static final String RECOMINER_TABLES_SCRIPT_NAME = "recominer_tables.sql";
    private static final String OPTIMIZATION_DDL_SCRIPT_NAME = "optimization_ddl.sql";
    private static final String OPTIMIZATION_DML_SCRIPT_NAME = "optimization_dml.sql";

    @Inject
    @Mysql
    private EntityManagerFactory factory;

    @Inject
    private JobContext jobContext;

    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;
        String projectName = project.getProjectName().toLowerCase();

        final Properties properties = new Properties();
        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, projectName + "_vcs");
        GenericDao dao = new GenericDao(factory.createEntityManager(properties));

        final Properties propertiesIssues = new Properties();
        propertiesIssues.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, projectName + "_issues");
        GenericDao daoIssues = new GenericDao(factory.createEntityManager(propertiesIssues));

        executeSqlScript(project, dao, RECOMINER_TABLES_SCRIPT_NAME);

        final List<Scmlog> commitsToAnalyze;
        if (project.getLastCommitDateAnalyzed() != null) {
            commitsToAnalyze = dao.executeNamedQueryWithParams("ScmlogAfterDate",
                    new String[]{"date"},
                    new Object[]{project.getLastCommitDateAnalyzed()},
                    Scmlog.class);
        } else {
            commitsToAnalyze = dao.executeNamedQuery("AllScmlog", Scmlog.class);
        }

        if (!commitsToAnalyze.isEmpty()) {

            final Map<IssueScmlog, IssueScmlog> issueAndCommitLinked = new HashMap<>();

            daoIssues.selectAll(IssueScmlog.class).forEach(is -> issueAndCommitLinked.put(is, is));

            if (project.getLastCommitDateAnalyzed() == null
                    && project.getLastIssueUpdateAnalyzed() == null) {
                executeSqlScript(project, dao, OPTIMIZATION_DDL_SCRIPT_NAME);
            }
            executeSqlScript(project, dao, OPTIMIZATION_DML_SCRIPT_NAME);

            final JiraAggregation jiraAggregation = new JiraAggregation(dao, project);
            jiraAggregation.aggregate(commitsToAnalyze);

            java.sql.Timestamp lastIssueUpdate = (java.sql.Timestamp) dao.selectNativeOneWithParams("SELECT MAX(updated_on) FROM " + projectName + "_issues.issues", new Object[0]);
            // setting date of last commit analyzed
            project.setLastCommitDateAnalyzed(commitsToAnalyze.get(commitsToAnalyze.size() - 1).getDate());
            project.setLastIssueUpdateAnalyzed(lastIssueUpdate);
        }

        return project;
    }
    
    private void executeSqlScript(Project project, GenericDao dao, String resourceFileName) {
        // loads script from resource folder in project
        InputStream script = this.getClass().getClassLoader().getResourceAsStream(resourceFileName);

        // Create SQL scanner
        final Scanner scanner = new Scanner(script).useDelimiter(SQL_DELIMITER);
        final String projectName = project.getProjectName().toLowerCase();

        // filter by last date of issue/commit update for continuous minering
        final String whereScmlog;
        final String whereIssue;
        final List<Object> params = new ArrayList<>();
        if (project.getLastCommitDateAnalyzed() != null) {
            whereScmlog = "AND s.date > ?";
            params.add(project.getLastCommitDateAnalyzed());
        } else {
            whereScmlog = "";
        }

        if (project.getLastIssueUpdateAnalyzed() != null) {
            whereIssue = "AND i.updated_on > ?";
            params.add(project.getLastIssueUpdateAnalyzed());
        } else {
            whereIssue = "";
        }
        
        // Loop through the SQL file statements
        while (scanner.hasNext()) {

            // Get statement from file
            final String rawStatement = scanner.next();
            
            // Replace all comments SQL command (starts with "--" and ends with 
            // "\n" or ";") to check if is a empty SQL command.
            if (!StringUtils.isBlank(rawStatement.replaceAll("-{2,}.*(\n|;)", ""))) {
                try {

                    final String replace = rawStatement
                            .replace("{0}", projectName)
                            .replace("{WHERE_SCMLOG}", whereScmlog)
                            .replace("{WHERE_ISSUE}", whereIssue)
                            .replace("{ISSUE_TRACKER_SYSTEM}", project.getIssueTracker().getSystem().toString().toUpperCase());

                    dao.executeNativeQuery(replace + SQL_DELIMITER, params.toArray());
                } catch (Exception e) {
                    if (e != null && e.getMessage() != null 
                            && (e.getMessage().contains("Duplicate column name") 
                            || e.getMessage().contains("Duplicate entry"))) {
                        log.warn(e.getMessage());
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
