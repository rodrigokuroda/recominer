package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.FeedbackJustificationRepository;
import br.edu.utfpr.recominer.core.repository.ArPredictionFeedbackRepository;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import br.edu.utfpr.recominer.web.dto.BusinessMessageDTO;
import br.edu.utfpr.recominer.web.dto.CochangeDTO;
import br.edu.utfpr.recominer.web.dto.FeedbackJustificationDTO;
import br.edu.utfpr.recominer.web.dto.IssueDTO;
import br.edu.utfpr.recominer.web.dto.PredictionFeedbackDTO;
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
    private ArPredictionFeedbackRepository feedbackRepository;
    
    @Inject
    private FeedbackJustificationRepository feedbackJustificationRepository;
    
    @RequestMapping(value = "/saveFeedback", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BusinessMessageDTO saveFeedback(@RequestBody IssueDTO issue) {
        FeedbackJustificationDTO feedback = issue.getFeedback();
        final Project project = projectRepository.findOne(issue.getProject().getId());
        feedbackRepository.setProject(project);
        feedbackJustificationRepository.setProject(project);
        
        for (CochangeDTO cochange : feedback.getCochanges()) {
            PredictionFeedbackDTO predictionFeedback = cochange.getFeedback();
            feedbackRepository.save(predictionFeedback.toEntity(cochange));
        }
        feedbackJustificationRepository.save(feedback.toEntity(issue));
        
        
        return new BusinessMessageDTO("0", "Sua resposta foi salva com sucesso. Obrigado!");
    }
    
}
