package br.edu.utfpr.recominer.batch.associationrule;

import br.edu.utfpr.recominer.core.model.Project;
import java.util.Date;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class AssociationRuleLog implements Persistable<Integer> {

    private Integer id;
    private Project project;
    private String type;
    private Date startDate;
    private Date endDate;

    public AssociationRuleLog(Project project, String type) {
        this.project = project;
        this.type = type;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    
    public void start() {
        this.startDate = new Date();
    }
    
    public void stop() {
        this.endDate = new Date();
    }
    
}
