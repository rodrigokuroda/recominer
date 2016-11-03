package br.edu.utfpr.recominer;

import br.edu.utfpr.recominer.model.Configuration;
import br.edu.utfpr.recominer.repository.ConfigurationRepository;
import java.util.List;
import javax.batch.operations.JobRestartException;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Component
public class CommandLineApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(CommandLineApplication.class);

    @Inject
    private ConfigurationRepository configurationRepository;
    
    @Inject
    private JobLauncher jobLauncher;

    @Inject
    private Job extractorJob;

    @Override
    public void run(String... params) throws Exception {
        LOG.info("Starting Recominer Extractor...");
        final JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addLong("run.id", System.currentTimeMillis());

        // getting configurable parameters from database
        final List<Configuration> configurations = configurationRepository.findAll();
        for (Configuration configuration : configurations) {
            jobParametersBuilder.addParameter(configuration.getKey(), new JobParameter(configuration.getValue()));
        }
        
        for (String param : params) {
            String[] keyValue = param.split("=");
            jobParametersBuilder.addParameter(keyValue[0], new JobParameter(keyValue[1]));
        }

        // execute only if has no job in execution
        try {
            JobExecution jobExecution = jobLauncher.run(extractorJob, jobParametersBuilder.toJobParameters());
            final ExitStatus exitStatus = jobExecution.getExitStatus();

            LOG.info("Job completed with exit status {}: {}", exitStatus.getExitCode(), exitStatus.getExitDescription());
        } catch (JobInstanceAlreadyCompleteException ex) {
            // there are no job in execution or executed.
            LOG.warn("The job has been run before with the same parameters and completed successfully.", ex);
        } catch (JobRestartException ex) {
            LOG.error("The job has been run before and circumstances that preclude a restart.", ex);
        } catch (JobParametersInvalidException ex) {
            LOG.error("The parameters are not valid for this job.", ex);
        } catch (JobExecutionAlreadyRunningException ex) {
            LOG.error("The JobInstance identified by the properties already has an execution running.", ex);
        }
    }
}
