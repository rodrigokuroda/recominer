package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.AllPredictionFeedbackRepository;
import br.edu.utfpr.recominer.core.repository.ArPredictionFeedbackRepository;
import br.edu.utfpr.recominer.core.repository.FeedbackJustificationRepository;
import br.edu.utfpr.recominer.core.repository.MlPredictionFeedbackRepository;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import br.edu.utfpr.recominer.web.dto.BusinessMessageDTO;
import br.edu.utfpr.recominer.web.dto.CochangeDTO;
import br.edu.utfpr.recominer.web.dto.FeedbackJustificationDTO;
import br.edu.utfpr.recominer.web.dto.IssueDTO;
import br.edu.utfpr.recominer.web.dto.PredictionFeedbackDTO;
import java.util.Date;
import javax.inject.Inject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@RestController
public class PredictionFeedbackController {
    
    @Inject
    private ProjectRepository projectRepository;
    
    @Inject
    private ArPredictionFeedbackRepository arFeedbackRepository;
    
    @Inject
    private MlPredictionFeedbackRepository mlFeedbackRepository;
    
    @Inject
    private AllPredictionFeedbackRepository allFeedbackRepository;
    
    @Inject
    private FeedbackJustificationRepository feedbackJustificationRepository;
    
    @RequestMapping(value = "/saveArFeedback", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BusinessMessageDTO saveFeedback(@RequestBody IssueDTO issue) {
        FeedbackJustificationDTO feedback = issue.getFeedback();
        final Project project = projectRepository.findOne(issue.getProject().getId());
        arFeedbackRepository.setProject(project);
        feedbackJustificationRepository.setProject(project);
        final Date submitDate = new Date();
        
        for (CochangeDTO cochange : feedback.getCochanges()) {
            PredictionFeedbackDTO predictionFeedback = cochange.getFeedback();
            arFeedbackRepository.save(predictionFeedback.toEntity(cochange, submitDate));
        }
        feedbackJustificationRepository.save(feedback.toEntity(issue));
        
        
        return new BusinessMessageDTO("0", "Sua resposta foi salva com sucesso. Obrigado!");
    }
    
    
    @RequestMapping(value = "/saveMlFeedback", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BusinessMessageDTO saveMlFeedback(@RequestBody IssueDTO issue) {
        FeedbackJustificationDTO feedback = issue.getFeedback();
        final Project project = projectRepository.findOne(issue.getProject().getId());
        mlFeedbackRepository.setProject(project);
        feedbackJustificationRepository.setProject(project);
        final Date submitDate = new Date();
        
        for (CochangeDTO cochange : feedback.getCochanges()) {
            PredictionFeedbackDTO predictionFeedback = cochange.getFeedback();
            mlFeedbackRepository.save(predictionFeedback.toEntity(cochange, submitDate));
        }
        feedbackJustificationRepository.save(feedback.toEntity(issue));
        
        
        return new BusinessMessageDTO("0", "Sua resposta foi salva com sucesso. Obrigado!");
    }
    
    
    @RequestMapping(value = "/saveAllFeedback", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BusinessMessageDTO saveAllFeedback(@RequestBody IssueDTO issue) {
        FeedbackJustificationDTO feedback = issue.getFeedback();
        final Project project = projectRepository.findOne(issue.getProject().getId());
        allFeedbackRepository.setProject(project);
        feedbackJustificationRepository.setProject(project);
        final Date submitDate = new Date();
        for (CochangeDTO cochange : feedback.getCochanges()) {
            PredictionFeedbackDTO predictionFeedback = cochange.getFeedback();
            allFeedbackRepository.save(predictionFeedback.toEntity(cochange, submitDate));
        }
        feedbackJustificationRepository.save(feedback.toEntity(issue));
        
        
        return new BusinessMessageDTO("0", "Sua resposta foi salva com sucesso. Obrigado!");
    }
    
}
