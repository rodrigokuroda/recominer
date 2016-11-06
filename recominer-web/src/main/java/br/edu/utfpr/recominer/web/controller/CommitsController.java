package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.CommitRepository;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import br.edu.utfpr.recominer.web.dto.CommitDTO;
import br.edu.utfpr.recominer.web.dto.IssueDTO;
import java.util.ArrayList;
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
    private ProjectRepository projectRepository;

    @Inject
    private CommitRepository repository;

    public CommitsController() {
    }

    @RequestMapping(value = "/commits", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CommitDTO> listCommis(@RequestBody IssueDTO issueDTO) {
        final Project project = projectRepository.findOne(issueDTO.getProject().getId());
        repository.setProject(project);
        List<CommitDTO> commits = new ArrayList<>();
        for (Commit commit : repository.selectProcessedCommitsPerformedWhileIssueWasOpenedOf(issueDTO.getIssue())) {
            commits.add(new CommitDTO(commit));
        }
        return commits;
    }
}
