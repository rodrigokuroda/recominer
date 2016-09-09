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
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@Configuration
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    private final RunIdIncrementer runIdIncrementer = new RunIdIncrementer();

    @Bean
    public Job job(@Qualifier("extractorStep") Step extractorStep,
            @Qualifier("calculatorStep") Step calculatorStep,
            @Qualifier("datasetStep") Step datasetStep,
            @Qualifier("classificationStep") Step classificationStep,
            @Qualifier("associationRuleStep") Step associationRuleStep) {
        return jobs.get("extractorJob")
                .incrementer(runIdIncrementer)
                //                .start(extractorStep)
                .start(calculatorStep)
                .next(datasetStep)
                .next(classificationStep)
                .next(associationRuleStep)
                .build();
    }

    @Bean
    protected Step extractorStep(ExtractorReader reader,
            ExtractorProcessor processor,
            ExtractorWriter writer) {
        return steps.get("step1")
                .<Project, ExtractorLog>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    protected Step calculatorStep(CalculatorReader reader,
            CalculatorProcessor processor,
            CalculatorWriter writer) {
        return steps.get("step2")
                .<Project, CalculatorLog>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    protected Step datasetStep(DatasetReader reader,
            DatasetProcessor processor,
            DatasetWriter writer) {
        return steps.get("step3")
                .<Project, DatasetLog>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    protected Step classificationStep(ClassificatorReader reader,
            ClassificatorProcessor processor,
            ClassificatorWriter writer) {
        return steps.get("step4")
                .<Project, ClassificatorLog>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    protected Step associationRuleStep(AssociationRuleReader reader,
            AssociationRuleProcessor processor,
            AssociationRuleWriter writer) {
        return steps.get("step5")
                .<Project, AssociationRuleLog>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
