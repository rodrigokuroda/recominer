package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.CommitRepository;
import java.util.List;
import javax.inject.Inject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommitsController {

    @Inject
    private CommitRepository repository;

    public CommitsController() {
    }

    @RequestMapping(value = "/commits", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Commit> listCommis(@RequestBody Project project, @RequestBody Issue issue) {
        repository.setProject(project);
        return repository.selectCommitsOf(issue);
    }
}
