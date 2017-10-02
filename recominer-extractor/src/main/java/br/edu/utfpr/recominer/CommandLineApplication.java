package br.edu.utfpr.recominer;

import java.util.Arrays;
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
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import br.edu.utfpr.recominer.model.Configuration;
import br.edu.utfpr.recominer.repository.ConfigurationRepository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Component
@Profile(value = "prod")
public class CommandLineApplication implements CommandLineRunner, ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(CommandLineApplication.class);

    @Inject
    private ConfigurationRepository configurationRepository;
    
    @Inject
    private JobLauncher jobLauncher;

    @Inject
    private Job extractorJob;

    @Override
    public void run(String... params) throws Exception {
        LOG.info("Starting Recominer Extractor with command-line arguments: {}", Arrays.toString(params));
        
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

	@Override
	public void run(ApplicationArguments args) throws Exception {
		LOG.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
		LOG.info("NonOptionArgs: {}", args.getNonOptionArgs());
		LOG.info("OptionNames: {}", args.getOptionNames());

        for (String name : args.getOptionNames()){
            LOG.info("arg-" + name + "=" + args.getOptionValues(name));
        }
	}
}
