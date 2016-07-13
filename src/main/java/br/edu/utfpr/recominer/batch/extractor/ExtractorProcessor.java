package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import br.edu.utfpr.recominer.dao.Mysql;
import java.util.Date;
import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class ExtractorProcessor implements ItemProcessor {

    @Inject
    @Mysql
    private EntityManagerFactory factory;
    
    @Inject
    private GitProcessor gitProcessor;
    
    @Inject
    private CvsanalyProcessor cvsanalyProcessor;
    
    @Inject
    private BichoProcessor bichoProcessor;
    
    @Override
    public Object processItem(Object item) throws Exception {
        Project project = (Project) item;
        
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

        return project;
    }
}
