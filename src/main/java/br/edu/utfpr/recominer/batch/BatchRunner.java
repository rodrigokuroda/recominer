package br.edu.utfpr.recominer.batch;

import br.edu.utfpr.recominer.dao.GenericDao;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import javax.batch.operations.JobOperator;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

/**
 * Runs the batch jobs.
 *
 * @author Rodrigo T. Kuroda
 */
@Singleton
@Dependent
public class BatchRunner {
    
    private final AtomicLong executing = new AtomicLong();
    
    @Inject
    private EntityManagerFactory factory;

    @Schedule(second = "50", minute = "07", hour = "*/1", persistent = false)
    public void startBichoJob() {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        
        try {
            jobOperator.getJobInstance(executing.get());
            return;
        } catch (NoSuchJobExecutionException e) {
            // execute only if has no job in execution
            
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
}
