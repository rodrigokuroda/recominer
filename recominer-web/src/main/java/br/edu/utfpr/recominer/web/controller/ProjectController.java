package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import br.edu.utfpr.recominer.web.dto.ProjectDTO;
import java.util.ArrayList;
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
    public List<ProjectDTO> listProjects() {
        List<ProjectDTO> projects = new ArrayList<>();
        for (Project project : repository.findAll()) {
            projects.add(new ProjectDTO(project));
        }
        return projects;
    }
}
