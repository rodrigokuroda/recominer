package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import java.util.List;
import javax.inject.Inject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PredictionController {

    @Inject
    private ProjectRepository repository;

    public PredictionController() {
    }

    @RequestMapping(value = "/projects", method = RequestMethod.POST)
    public List<Project> evaluate(@RequestBody(required = false) String password) {
        return repository.findAll();
    }
}
