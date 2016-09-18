package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.MachineLearningPrediction;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.CommitRepository;
import br.edu.utfpr.recominer.core.repository.FileRepository;
import br.edu.utfpr.recominer.core.repository.MachineLearningPredictionRepository;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import br.edu.utfpr.recominer.web.dto.CochangeDTO;
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
public class CochangesController {

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private CommitRepository commitRepository;

    @Inject
    private FileRepository fileRepository;

    @Inject
    private MachineLearningPredictionRepository mlPredictionRepository;

    public CochangesController() {
    }

    @RequestMapping(value = "/predictedCochanges", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CochangeDTO> listCommis(@RequestBody FileDTO fileDTO) {
        final Project project = projectRepository.findOne(fileDTO.getProject().getId());

        projectRepository.setProject(project);
        commitRepository.setProject(project);
        fileRepository.setProject(project);
        mlPredictionRepository.setProject(project);

        final Commit commit = commitRepository.findOne(fileDTO.getCommit().getId());
        final File file = fileRepository.findOne(fileDTO.getId());

        List<CochangeDTO> cochanges = new ArrayList<>();
        for (MachineLearningPrediction cochange : mlPredictionRepository.selectPredictedCochangesFor(commit, file)) {
            cochanges.add(new CochangeDTO(cochange));
        }
        return cochanges;
    }
}