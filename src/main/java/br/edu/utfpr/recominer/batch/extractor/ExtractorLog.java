/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.batch.aggregator.Project;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author kuroda
 */
@Entity
@Table(name = "extractor", schema = "recominer")
public class ExtractorLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Project project;
    
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date gitProcessStartDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date gitProcessEndDate;

    @Column
    private int gitProcessReturnCode;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date cvsanalyProcessStartDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date cvsanalyProcessEndDate;

    @Column
    private int cvsanalyProcessReturnCode;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date bichoProcessStartDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date bichoProcessEndDate;

    @Column
    private int bichoProcessReturnCode;

    public ExtractorLog() {
    }
    
    public ExtractorLog(Project project) {
        this.project = project;
    }

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

}
