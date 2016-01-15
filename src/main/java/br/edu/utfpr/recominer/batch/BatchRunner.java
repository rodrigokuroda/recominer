package br.edu.utfpr.recominer.batch;

import br.edu.utfpr.recominer.batch.bicho.BichoReader;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import javax.batch.operations.JobOperator;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Runs the Bicho batch job every 5 minutes.
 *
 * @see BichoReader, BichoProcessor, BichoWriter
 * @author Rodrigo T. Kuroda
 */
@Singleton
@Startup
public class BatchRunner {
    
    private AtomicLong executing = new AtomicLong();

    @Schedule(second = "*", minute = "*", hour = "*/1", persistent = false)
    public void startAggregatorJob() {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        
        try {
            jobOperator.getJobInstance(executing.get());
            return;
        } catch (NoSuchJobExecutionException e) {
            // execute only if has no job in execution
            long jobId = jobOperator.start("minerJob", new Properties());
            executing.set(jobId);
        }
        
    }
}
