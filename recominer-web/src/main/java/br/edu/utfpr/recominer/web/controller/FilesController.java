package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.FileRepository;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import br.edu.utfpr.recominer.web.dto.CommitDTO;
import br.edu.utfpr.recominer.web.dto.FileDTO;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FilesController {

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private FileRepository repository;

    public FilesController() {
    }

    @RequestMapping(value = "/files", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileDTO> listFiles(@RequestBody CommitDTO commit) {
        final Project project = projectRepository.findOne(commit.getProject().getId());
        repository.setProject(project);
        List<FileDTO> files = new ArrayList<>();
        for (File file : repository.selectChangedFilesIn(commit.getCommit())) {
            files.add(new FileDTO(file));
        }
        return files;
    }
}
