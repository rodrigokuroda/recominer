package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.FileRepository;
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
    private FileRepository repository;

    public FilesController() {
    }

    @RequestMapping(value = "/files", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<File> listFiles(@RequestBody Project project, @RequestBody Commit commit) {
        repository.setProject(project);
        return repository.selectChangedFilesIn(commit);
    }
}
