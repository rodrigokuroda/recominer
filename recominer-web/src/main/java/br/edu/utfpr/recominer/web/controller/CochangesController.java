package br.edu.utfpr.recominer.web.controller;

import br.edu.utfpr.recominer.core.model.AssociationRulePrediction;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.MachineLearningPrediction;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.AssociationRulePredictionRepository;
import br.edu.utfpr.recominer.core.repository.CommitRepository;
import br.edu.utfpr.recominer.core.repository.FileRepository;
import br.edu.utfpr.recominer.core.repository.MachineLearningPredictionRepository;
import br.edu.utfpr.recominer.core.repository.ProjectRepository;
import br.edu.utfpr.recominer.web.dto.CochangeDTO;
import br.edu.utfpr.recominer.web.dto.FileDTO;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    @Inject
    private AssociationRulePredictionRepository arPredictionRepository;

    public CochangesController() {
    }

    @RequestMapping(value = "/predictedCochanges", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CochangeDTO> predictedCochanges(@RequestBody FileDTO fileDTO) {
        final Project project = projectRepository.findOne(fileDTO.getProject().getId());

        commitRepository.setProject(project);
        fileRepository.setProject(project);
        mlPredictionRepository.setProject(project);
        arPredictionRepository.setProject(project);

        final Commit commit = commitRepository.findOne(fileDTO.getCommit().getId());
        final File file = fileRepository.findOne(fileDTO.getId());

        Map<CochangeDTO, CochangeDTO> cochanges = new LinkedHashMap<>();
        for (MachineLearningPrediction cochange : mlPredictionRepository.selectPredictedCochangesFor(commit, file)) {
            final CochangeDTO cochangeDTO = CochangeDTO.from(cochange);
            cochanges.put(cochangeDTO, cochangeDTO);
        }
        for (AssociationRulePrediction cochange : arPredictionRepository.selectPredictedCochangesFor(commit, file)) {
            final List<CochangeDTO> cochangeDTOList = CochangeDTO.from(cochange);
            for (CochangeDTO cochangeDTO : cochangeDTOList) {
                if (cochanges.containsKey(cochangeDTO)) {
                    // TODO append
                    cochanges.get(cochangeDTO); //.append(cochangeDTO);
                } else {
                    cochanges.put(cochangeDTO, cochangeDTO);
                }
            }
        }
        return new ArrayList<>(cochanges.keySet());
    }

    @RequestMapping(value = "/arPredictedCochanges", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CochangeDTO> arPredictedCochanges(@RequestBody FileDTO fileDTO) {
        final Project project = projectRepository.findOne(fileDTO.getProject().getId());

        commitRepository.setProject(project);
        fileRepository.setProject(project);
        mlPredictionRepository.setProject(project);
        arPredictionRepository.setProject(project);

        final Commit commit = commitRepository.findOne(fileDTO.getCommit().getId());
        final File file = fileRepository.findOne(fileDTO.getId());

        List<CochangeDTO> cochanges = new ArrayList<>();
        for (AssociationRulePrediction cochange : arPredictionRepository.selectPredictedCochangesFor(commit, file)) {
            cochanges.addAll(CochangeDTO.from(cochange));
        }
        return cochanges;
    }

    @RequestMapping(value = "/mlPredictedCochanges", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CochangeDTO> mlPredictedCochanges(@RequestBody FileDTO fileDTO) {
        final Project project = projectRepository.findOne(fileDTO.getProject().getId());

        commitRepository.setProject(project);
        fileRepository.setProject(project);
        mlPredictionRepository.setProject(project);
        arPredictionRepository.setProject(project);

        final Commit commit = commitRepository.findOne(fileDTO.getCommit().getId());
        final File file = fileRepository.findOne(fileDTO.getId());

        List<CochangeDTO> cochanges = new ArrayList<>();
        for (MachineLearningPrediction cochange : mlPredictionRepository.selectPredictedCochangesFor(commit, file)) {
            cochanges.add(CochangeDTO.from(cochange));
        }
        return cochanges.stream().sorted((CochangeDTO o1, CochangeDTO o2) -> {
            if ("C".equals(o1.getPredictionResult())
                    && "N".equals(o2.getPredictionResult())) {
                return -1;
            } else if ("N".equals(o1.getPredictionResult())
                    && "C".equals(o2.getPredictionResult())) {
                return 1;
            }
            if ("N".equals(o1.getPredictionResult())
                    && "N".equals(o2.getPredictionResult())) {
                if (o1.getProbability() < o2.getProbability()) {
                    return -1;
                } else if (o1.getProbability() > o2.getProbability()) {
                    return 1; 
                }
                return 0;
            }
            if ("C".equals(o1.getPredictionResult())
                    && "C".equals(o2.getPredictionResult())) {
                if (o1.getProbability() < o2.getProbability()) {
                    return 1;
                } else if (o1.getProbability() > o2.getProbability()) {
                    return -1; 
                }
                return 0;
            }
            return 0;
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = "/allFiles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CochangeDTO> allFiles(@RequestBody FileDTO fileDTO) {
        final Project project = projectRepository.findOne(fileDTO.getProject().getId());

        fileRepository.setProject(project);

        final List<File> files = fileRepository.listFiles(fileDTO.toEntity());

        List<CochangeDTO> cochanges = new ArrayList<>();
        for (File file : files) {
            cochanges.add(CochangeDTO.from(file));
        }
        return cochanges;
    }
}
