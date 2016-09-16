package br.edu.utfpr.recominer;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan("br.edu.utfpr.recominer")
@ActiveProfiles("dev")
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

//    @Inject
//    private ConfigurationRepository configurationRepository;
//
//    @Inject
//    private JobLauncher jobLauncher;
//
//    @Inject
//    private Job extractorJob;

    public static void main(String[] args) {
        LOG.info("Starting Extractor...");
        SpringApplication.run(Application.class, args);
    }

//    @Scheduled(initialDelay = INITIAL_DELAY, fixedDelay = FIXED_DELAY)
//    public void startBichoJob() {
//        final JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
//        jobParametersBuilder.addLong("run.id", 1l);
//        
//        // getting configurable parameters from database
//        final Iterable<Configuration> configurations = configurationRepository.findAll();
//        for (Configuration configuration : configurations) {
//            jobParametersBuilder.addParameter(configuration.getKey(), new JobParameter(configuration.getValue()));
//        }
//
//        // execute only if has no job in execution
//        try {
//            JobExecution jobExecution = jobLauncher.run(extractorJob, jobParametersBuilder.toJobParameters());
//            final ExitStatus exitStatus = jobExecution.getExitStatus();
//
//            log.info("Job completed with exit status " + exitStatus.getExitCode() + ": " + exitStatus.getExitDescription());
//        } catch (JobInstanceAlreadyCompleteException e) {
//            // there are no job in execution or executed.
//            log.warn("The job has been run before with the same parameters and completed successfully.");
//        } catch (JobRestartException ex) {
//            log.error("The job has been run before and circumstances that preclude a restart.", ex);
//        } catch (JobParametersInvalidException ex) {
//            log.error("The parameters are not valid for this job.", ex);
//        } catch (JobExecutionAlreadyRunningException ex) {
//            log.error("The JobInstance identified by the properties already has an execution running.", ex);
//        }
//    }
    @Bean
    BatchConfigurer configurer(@Qualifier("batchDataSource") DataSource dataSource) {
        return new DefaultBatchConfigurer(dataSource);
    }
    
    @Bean
    org.springframework.batch.test.JobLauncherTestUtils jobLauncherTestUtils() {
        return new org.springframework.batch.test.JobLauncherTestUtils();
    }
}
