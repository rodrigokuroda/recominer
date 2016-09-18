package br.edu.utfpr.recominer.web.dto;

import br.edu.utfpr.recominer.core.model.MachineLearningPrediction;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class CochangeDTO {

    private Integer id;
    private FileDTO file;

    public CochangeDTO(MachineLearningPrediction cochange) {
        this.id = cochange.getId();
        this.file = new FileDTO(cochange.getPredictedFile());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FileDTO getFile() {
        return file;
    }

    public void setFile(FileDTO file) {
        this.file = file;
    }

}
