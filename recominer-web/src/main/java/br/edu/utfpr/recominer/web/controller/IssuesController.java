package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.IssueRepository;
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
    private IssueRepository repository;

    public IssuesController() {
    }

    @RequestMapping(value = "/issues", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Issue> listIssues(@RequestBody Project project) {
        repository.setProject(project);
        return repository.selectIssuesOfProject();
    }
}
