package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.Application;
import br.edu.utfpr.recominer.core.model.Project;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
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

    @Before
    public void setUp() {
    }

    @Test
    public void testCalculeIssueMetrics() {
        JobExecution launchStep = jobLauncherTestUtils.launchStep("calculatorStep");
    }

    @Test
    public void testSaveIssueMetrics() {
    }

}
