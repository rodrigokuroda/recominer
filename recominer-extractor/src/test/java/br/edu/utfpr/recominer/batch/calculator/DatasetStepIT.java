package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.Application;
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
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
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
    private JdbcTemplate template;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private File test;

    @Before
    public void setUp() throws IOException {
        test = testFolder.newFolder("test");
    }

    @After
    public void tearDown() throws IOException {
        testFolder.delete();
    }

    @Test
    public void shouldCalculateMetricsForNewCommit() {
        final Integer issueId = 1644;
        final Integer commitId = 1519;

        final JobParameters params = new JobParameters();
        params.getParameters().put("spring.batch.job.enabled", new JobParameter("false"));
        params.getParameters().put("workingDir", new JobParameter("test"));

        // Running dataset generation job step.
        JobExecution datasetJob = jobLauncherTestUtils.launchStep("datasetStep", params);
        assertEquals("COMPLETED", datasetJob.getExitStatus().getExitCode());

        assertTrue(test.exists());
    }

}
