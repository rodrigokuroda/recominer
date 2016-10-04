package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.PredictionFeedbackRepository;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import br.edu.utfpr.recominer.web.dto.BusinessMessageDTO;
import br.edu.utfpr.recominer.web.dto.CochangeDTO;
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
    private PredictionFeedbackRepository feedbackRepository;
    
    @RequestMapping(value = "/saveFeedback", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BusinessMessageDTO saveFeedback(@RequestBody CochangeDTO cochange) {
        final Project project = projectRepository.findOne(cochange.getFile().getProject().getId());
        feedbackRepository.setProject(project);
        
        PredictionFeedbackDTO feedback = cochange.getFeedback();
        feedback.setCochange(cochange);
        feedbackRepository.save(feedback.toEntity());
        return new BusinessMessageDTO("0", "The feedback was saved successfully.");
    }
}
