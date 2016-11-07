package br.edu.utfpr.recominer;

import br.edu.utfpr.recominer.batch.associationrule.AssociationRuleLog;
import br.edu.utfpr.recominer.batch.associationrule.AssociationRuleProcessor;
import br.edu.utfpr.recominer.batch.associationrule.AssociationRuleReader;
import br.edu.utfpr.recominer.batch.associationrule.AssociationRuleWriter;
import br.edu.utfpr.recominer.batch.calculator.CalculatorLog;
import br.edu.utfpr.recominer.batch.calculator.CalculatorProcessor;
import br.edu.utfpr.recominer.batch.calculator.CalculatorReader;
import br.edu.utfpr.recominer.batch.calculator.CalculatorWriter;
import br.edu.utfpr.recominer.batch.classificator.ClassificatorLog;
import br.edu.utfpr.recominer.batch.classificator.ClassificatorProcessor;
import br.edu.utfpr.recominer.batch.classificator.ClassificatorReader;
import br.edu.utfpr.recominer.batch.classificator.ClassificatorWriter;
import br.edu.utfpr.recominer.batch.dataset.DatasetLog;
import br.edu.utfpr.recominer.batch.dataset.DatasetProcessor;
import br.edu.utfpr.recominer.batch.dataset.DatasetReader;
import br.edu.utfpr.recominer.batch.dataset.DatasetWriter;
import br.edu.utfpr.recominer.batch.extractor.ExtractorProcessor;
import br.edu.utfpr.recominer.batch.extractor.ExtractorReader;
import br.edu.utfpr.recominer.batch.extractor.ExtractorWriter;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.model.ExtractorLog;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Configuration
@Profile({"dev", "test"})
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;
    
    @Autowired
    private ConfigurationValues config;

    private final RunIdIncrementer runIdIncrementer = new RunIdIncrementer();

    @Bean
    public BatchConfigurer configurer(@Qualifier("batchDataSource") DataSource dataSource) {
        return new DefaultBatchConfigurer(dataSource);
    }

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }

    @Bean
    public Job job(
            @Qualifier("extractorStep") Step extractorStep,
            @Qualifier("calculatorStep") Step calculatorStep,
            @Qualifier("datasetStep") Step datasetStep,
            @Qualifier("classificationStep") Step classificationStep,
            @Qualifier("associationRuleStep") Step associationRuleStep) {

        final SimpleJobBuilder job;

        if (config.skipExtractor()) {
            job = jobs.get("extractorJob")
                    .incrementer(runIdIncrementer)
                    .start(calculatorStep);
        } else {
            job = jobs.get("extractorJob")
                    .incrementer(runIdIncrementer)
                    .start(extractorStep)
                    .next(calculatorStep);
        }

        return job
                .next(datasetStep)
                .next(classificationStep)
                .next(associationRuleStep)
                .build();
    }

    @Bean
    protected Step extractorStep(ExtractorReader reader,
            ExtractorProcessor processor,
            ExtractorWriter writer) {
        return steps.get("extractorStep")
                .<Project, ExtractorLog>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant().retryLimit(0)
                .build();
    }

    @Bean
    protected Step calculatorStep(CalculatorReader reader,
            CalculatorProcessor processor,
            CalculatorWriter writer) {
        return steps.get("calculatorStep")
                .<Project, CalculatorLog>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant().retryLimit(0)
                .build();
    }

    @Bean
    protected Step datasetStep(DatasetReader reader,
            DatasetProcessor processor,
            DatasetWriter writer) {
        return steps.get("datasetStep")
                .<Project, DatasetLog>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    protected Step classificationStep(ClassificatorReader reader,
            ClassificatorProcessor processor,
            ClassificatorWriter writer) {
        return steps.get("classificationStep")
                .<Project, ClassificatorLog>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant().retryLimit(0)
                .build();
    }

    @Bean
    protected Step associationRuleStep(AssociationRuleReader reader,
            AssociationRuleProcessor processor,
            AssociationRuleWriter writer) {
        return steps.get("associationRuleStep")
                .<Project, AssociationRuleLog>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant().retryLimit(0)
                .build();
    }
}
