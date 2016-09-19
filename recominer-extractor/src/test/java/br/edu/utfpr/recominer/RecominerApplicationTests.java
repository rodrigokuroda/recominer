package br.edu.utfpr.recominer;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringApplicationConfiguration(Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest({"spring.batch.job.enabled=false"})
@ActiveProfiles(profiles = "test")
public class RecominerApplicationTests {

    @Autowired
    private ApplicationContext appContext;

    @Test
    public void testContextLoads() {
        for (String beanDefinitionName : appContext.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }

        assertThat(this.appContext).isNotNull();
        assertThat(this.appContext.containsBean("calculatorStep")).isTrue();
        assertThat(this.appContext.containsBean("datasetStep")).isTrue();
    }

}
