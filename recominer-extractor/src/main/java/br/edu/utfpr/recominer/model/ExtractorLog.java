package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.core.model.Project;
import java.io.Serializable;
import java.util.Date;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class ExtractorLog implements Persistable<Integer>, Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Project project;
    private Date gitProcessStartDate;
    private Date gitProcessEndDate;
    private int gitProcessReturnCode;
    private Date cvsanalyProcessStartDate;
    private Date cvsanalyProcessEndDate;
    private int cvsanalyProcessReturnCode;
    private Date bichoProcessStartDate;
    private Date bichoProcessEndDate;
    private int bichoProcessReturnCode;
    private Date associationProcessStartDate;
    private Date associationProcessEndDate;

    public ExtractorLog() {
    }

    public ExtractorLog(Project project) {
        this.project = project;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getGitProcessStartDate() {
        return gitProcessStartDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setGitProcessStartDate(Date gitProcessStartDate) {
        this.gitProcessStartDate = gitProcessStartDate;
    }

    public Date getGitProcessEndDate() {
        return gitProcessEndDate;
    }

    public void setGitProcessEndDate(Date gitProcessEndDate) {
        this.gitProcessEndDate = gitProcessEndDate;
    }

    public int getGitProcessReturnCode() {
        return gitProcessReturnCode;
    }

    public void setGitProcessReturnCode(int gitProcessReturnCode) {
        this.gitProcessReturnCode = gitProcessReturnCode;
    }

    public Date getCvsanalyProcessStartDate() {
        return cvsanalyProcessStartDate;
    }

    public void setCvsanalyProcessStartDate(Date cvsanalyProcessStartDate) {
        this.cvsanalyProcessStartDate = cvsanalyProcessStartDate;
    }

    public Date getCvsanalyProcessEndDate() {
        return cvsanalyProcessEndDate;
    }

    public void setCvsanalyProcessEndDate(Date cvsanalyProcessEndDate) {
        this.cvsanalyProcessEndDate = cvsanalyProcessEndDate;
    }

    public int getCvsanalyProcessReturnCode() {
        return cvsanalyProcessReturnCode;
    }

    public void setCvsanalyProcessReturnCode(int cvsanalyProcessReturnCode) {
        this.cvsanalyProcessReturnCode = cvsanalyProcessReturnCode;
    }

    public Date getBichoProcessStartDate() {
        return bichoProcessStartDate;
    }

    public void setBichoProcessStartDate(Date bichoProcessStartDate) {
        this.bichoProcessStartDate = bichoProcessStartDate;
    }

    public Date getBichoProcessEndDate() {
        return bichoProcessEndDate;
    }

    public void setBichoProcessEndDate(Date bichoProcessEndDate) {
        this.bichoProcessEndDate = bichoProcessEndDate;
    }

    public int getBichoProcessReturnCode() {
        return bichoProcessReturnCode;
    }

    public void setBichoProcessReturnCode(int bichoProcessReturnCode) {
        this.bichoProcessReturnCode = bichoProcessReturnCode;
    }

    public Date getAssociationProcessStartDate() {
        return associationProcessStartDate;
    }

    public void setAssociationProcessStartDate(Date associationProcessStartDate) {
        this.associationProcessStartDate = associationProcessStartDate;
    }

    public Date getAssociationProcessEndDate() {
        return associationProcessEndDate;
    }

    public void setAssociationProcessEndDate(Date associationProcessEndDate) {
        this.associationProcessEndDate = associationProcessEndDate;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

}
