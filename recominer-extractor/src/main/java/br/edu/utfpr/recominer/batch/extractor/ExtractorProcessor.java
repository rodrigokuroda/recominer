package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.model.ExtractorLog;
import br.edu.utfpr.recominer.core.model.Project;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.batch.item.ItemProcessor;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class ExtractorProcessor implements ItemProcessor<Project, ExtractorLog> {

    @Inject
    private GitProcessor gitProcessor;

    @Inject
    private CvsanalyProcessor cvsanalyProcessor;

    @Inject
    private BichoProcessor bichoProcessor;
    
    @Inject
    private AssociationProcessor associationProcessor;

    @Override
    public ExtractorLog process(Project project) throws Exception {
        ExtractorLog log = new ExtractorLog(project);

        // update or clone the project from version control system
        log.setGitProcessStartDate(new Date());

        int gitProcessExitCode = gitProcessor.process(project);

        log.setGitProcessEndDate(new Date());
        log.setGitProcessReturnCode(gitProcessExitCode);

        // extract data from version control system
        log.setCvsanalyProcessStartDate(new Date());

        int cvsanalyProcessExitCode = cvsanalyProcessor.process(project);

        log.setCvsanalyProcessEndDate(new Date());
        log.setCvsanalyProcessReturnCode(cvsanalyProcessExitCode);

        // extract data from issue tracker
        log.setBichoProcessStartDate(new Date());

        int bichoProcessExitCode = bichoProcessor.process(project);

        log.setBichoProcessEndDate(new Date());
        log.setBichoProcessReturnCode(bichoProcessExitCode);
        
        log.setAssociationProcessStartDate(new Date());

        associationProcessor.process(project);

        log.setAssociationProcessEndDate(new Date());

        return log;
    }
}
