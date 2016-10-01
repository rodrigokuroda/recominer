package br.edu.utfpr.recominer.batch.dataset;

import br.edu.utfpr.recominer.Application;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.CommitRepository;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@SpringApplicationConfiguration(Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@ActiveProfiles(profiles = "test")
public class DatasetStepIT {

    @Inject
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Inject
    private CommitRepository repository;

    @Inject
    private JdbcTemplate template;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private File test;

    @Before
    public void setUp() throws IOException {
        test = testFolder.newFolder("test");
        template.update("UPDATE recominer.project SET schema_prefix = 'avro_test' WHERE id = 1");
    }

    @After
    public void tearDown() throws IOException {
        testFolder.delete();
        template.update("UPDATE recominer.project SET schema_prefix = 'avro' WHERE id = 1");
    }

    @Test
    public void shouldCreateDatasetForTrainAndTest() {
        final Integer issueId = 1698;
        final Integer commitId = 4219;
        final Integer cochange = 4221;

        repository.setProject(new Project(1, "avro", "avro_test", null, null, null));

        final JobParameters params = new JobParametersBuilder()
                .addString("spring.batch.job.enabled", "false")
                .addString("workingDir", test.getAbsolutePath())
                .addString("filenameFilter", "CHANGES.txt")
                .toJobParameters();

        // Running calculation job step.
        JobExecution calculationJob = jobLauncherTestUtils.launchStep("calculatorStep");
        assertEquals("COMPLETED", calculationJob.getExitStatus().getExitCode());
        
        // Running dataset generation job step.
        JobExecution datasetJob = jobLauncherTestUtils.launchStep("datasetStep", params);
        assertEquals("COMPLETED", datasetJob.getExitStatus().getExitCode());

        assertTrue(test.exists());
        assertTrue(new File(test, "avro/" + issueId + "/" + commitId + "/test.csv").exists());
        assertTrue(new File(test, "avro/" + issueId + "/" + commitId + "/" + cochange + "/train.csv").exists());

        // TODO filtering files in select?
        //assertTrue(repository.selectNewCommitsForDataset().isEmpty());
    }

}
