package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.repository.IssueRepository;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import br.edu.utfpr.recominer.web.dto.IssueDTO;
import br.edu.utfpr.recominer.web.dto.ProjectDTO;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssuesController {

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private IssueRepository repository;

    public IssuesController() {
    }

    @RequestMapping(value = "/issues", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IssueDTO> listIssues(@RequestBody ProjectDTO project) {
        repository.setProject(projectRepository.findOne(project.getId()));
        List<IssueDTO> issues = new ArrayList<>();
        for (Issue issue : repository.selectProcessedIssuesOfProject(project.getTechnique())) {
            issues.add(new IssueDTO(issue));
        }
        return issues;
    }
}
