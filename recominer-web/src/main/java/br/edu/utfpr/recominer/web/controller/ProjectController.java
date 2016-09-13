package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import java.util.List;
import javax.inject.Inject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {

    @Inject
    private ProjectRepository repository;

    public ProjectController() {
    }

    @RequestMapping(value = "/projects", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> listProjects() {
        return repository.findAll();
    }
}
