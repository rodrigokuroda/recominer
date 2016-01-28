package br.edu.utfpr.recominer.batch;

import br.edu.utfpr.recominer.dao.GenericDao;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import javax.batch.operations.JobOperator;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Runs the batch jobs.
 *
 * @author Rodrigo T. Kuroda
 */
@Singleton
@Dependent
public class BatchRunner {
    
    private final Logger log = LogManager.getLogger();
    
    private final AtomicLong executing = new AtomicLong();
    
    @Inject
    private EntityManagerFactory factory;

    @Schedule(hour = "*", persistent = false)
    public void startBichoJob() {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        
        // execute only if has no job in execution
        try {
            final JobExecution jobExecution = jobOperator.getJobExecution(executing.get());
            final BatchStatus batchStatus = jobExecution.getBatchStatus();
            if (batchStatus != BatchStatus.COMPLETED 
                    && batchStatus != BatchStatus.FAILED
                    && batchStatus != BatchStatus.STOPPED
                    && batchStatus != BatchStatus.ABANDONED) {
                log.warn("Already exists an job in execution. Job " + executing.get() + ", status " + jobExecution.getBatchStatus() + ".");
                return;
            }
        } catch (NoSuchJobExecutionException e) {
            // there are no job in execution or executed.
        }
        
        final Properties properties = new Properties();
        final GenericDao dao = new GenericDao(factory.createEntityManager());
        final List<BatchConfiguration> configurations = dao.selectAll(BatchConfiguration.class);
        for (BatchConfiguration configuration : configurations) {
            properties.put(configuration.getConfigurationKey(), configuration.getConfigurationValue());
        }

        long jobId = jobOperator.start("minerJob", properties);
        executing.set(jobId);
        
    }
}
