package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.Application;
import br.edu.utfpr.recominer.model.IssuesMetrics;
import br.edu.utfpr.recominer.repository.IssuesMetricsRepository;
import javax.inject.Inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@SpringApplicationConfiguration(Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@ActiveProfiles(profiles = "test")
public class IssueMetricCalculatorTest {

    @Inject
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Inject
    private JdbcTemplate template;

    @Test
    public void shouldCalculateMetricsForNewCommit() {
        final Integer issueId = 1644;
        final Integer commitId = 1519;

        // Delete to recalculate
        int rowsAffected = template.update(
                "DELETE FROM avro_test.issues_metrics WHERE issue_id = ? AND commit_id = ?",
                issueId, commitId);
        assertEquals(1, rowsAffected);

        // Running calculation job step.
        JobExecution calculationJob = jobLauncherTestUtils.launchStep("calculatorStep");
        assertEquals("COMPLETED", calculationJob.getExitStatus().getExitCode());

        IssuesMetrics metric = template.queryForObject(
                "SELECT * FROM avro_test.issues_metrics WHERE issue_id = ? AND commit_id = ?",
                IssuesMetricsRepository.ROW_MAPPER,
                issueId,
                commitId);
        assertNotNull(metric);
    }

    @Test
    public void testSaveIssueMetrics() {
    }

}
