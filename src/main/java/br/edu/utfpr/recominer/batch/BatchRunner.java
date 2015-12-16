package br.edu.utfpr.recominer.batch;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.batch.bicho.BichoReader;
import br.edu.utfpr.recominer.dao.GenericBichoDAO;
import java.util.List;
import java.util.Properties;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

/**
 * Runs the Bicho batch job every 5 minutes.
 *
 * @see BichoReader, BichoProcessor, BichoWriter
 * @author Rodrigo T. Kuroda
 */
@Singleton
public class BatchRunner {

    @Inject
    private GenericBichoDAO dao;

    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    public void startAggregatorJob() {
        // reads all VCS' projects available (database schemas)
        final List<Project> projects = dao.selectAll(Project.class);

        for (Project project : projects) {
            final Properties properties = new Properties();
            properties.put("project", project.getProjectName().toLowerCase());
            properties.put("issueTrackerUrl", project.getIssueTrackerUrl());
            properties.put("versionControlUrl", project.getVersionControlUrl());
            properties.put("projectIssueTrackerSystem", project.getIssueTrackerSystem());
            properties.put("lastCommitAnalysed", project.getLastCommitAnalysed().toString());

            JobOperator jobOperator = BatchRuntime.getJobOperator();
            jobOperator.start("minerJob", properties);
        }
    }
}
