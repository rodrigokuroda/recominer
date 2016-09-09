package br.edu.utfpr.recominer.batch.calculator;

import br.edu.utfpr.recominer.core.model.Project;
import java.io.Serializable;
import java.util.Date;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
public class CalculatorLog implements Persistable<Integer>, Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Project project;
    private String metric;
    private Date startDate;
    private Date endDate;

    public CalculatorLog(Project project, String metric) {
        this.project = project;
        this.metric = metric;
    }

    public CalculatorLog(Integer id, Project project, String metric) {
        this.id = id;
        this.project = project;
        this.metric = metric;
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

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
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
