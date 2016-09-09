package br.edu.utfpr.recominer.metric.network;

import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.statistic.DescriptiveStatisticsHelper;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class NetworkMetrics implements Persistable<Integer> {

    public static final String HEADER
            = "networkId;"
            + "networkIssueId;"
            + "networkCommitId;"// + "brcAvg;brcSum;brcMax;"
//            + "btwSum;"
//            + "btwAvg;"
            + "btwMdn;"
//            + "btwMax;"
//            + "clsSum;"
//            + "clsAvg;"
            + "clsMdn;"
//            + "clsMax;"
//            + "dgrSum;"
//            + "dgrAvg;"
            + "dgrMdn;"
//            + "dgrMax;"
            // + "egvSum;egvAvg;egvMax;"
            //+ "egoBtwSum;egoBtwAvg;egoBtwMdn;egoBtwMax;"
            //+ "egoSizeSum;egoSizeAvg;egoSizeMdn;egoSizeMax;"
            //+ "egoTiesSum;egoTiesAvg;egoTiesMdn;egoTiesMax;"
            // + "egoPairsSum;egoPairsAvg;egoPairsMax;"
            //+ "egoDensitySum;egoDensityAvg;egoDensityMdn;egoDensityMax;"
//            + "efficiencySum;"
//            + "efficiencyAvg;"
            + "efficiencyMdn;"
//            + "efficiencyMax;"
//            + "efvSizeSum;"
//            + "efvSizeAvg;"
            + "efvSizeMdn;"
//            + "efvSizeMax;"
//            + "constraintSum;"
//            + "constraintAvg;"
            + "constraintMdn;"
//            + "constraintMax;"
//            + "hierarchySum;"
//            + "hierarchyAvg;"
            + "hierarchyMdn;"
//            + "hierarchyMax;"
            + "size;"
            + "ties;"
            + "density;"
            + "diameter;";

    private Integer id;
    private Issue issue;
    private Commit commit;
    private final MetricStatistic betweennessStatistics;
    private final MetricStatistic closenessStatistics;
    private final MetricStatistic degreeStatistics;
    private final MetricStatistic efficiencyStatistics;
    private final MetricStatistic effectiveSizeStatistics;
    private final MetricStatistic constraintStatistics;
    private final MetricStatistic hierarchyStatistics;
    private final GlobalMeasure pairFileGlobal;

    public NetworkMetrics() {
        this.betweennessStatistics = new MetricStatistic();
        this.closenessStatistics = new MetricStatistic();
        this.degreeStatistics = new MetricStatistic();
        this.efficiencyStatistics = new MetricStatistic();
        this.effectiveSizeStatistics = new MetricStatistic();
        this.constraintStatistics = new MetricStatistic();
        this.hierarchyStatistics = new MetricStatistic();
        this.pairFileGlobal = new GlobalMeasure(0, 0);
    }

    public NetworkMetrics(Issue issue,
            Commit commit,
            DescriptiveStatisticsHelper betweennessStatistics,
            DescriptiveStatisticsHelper closenessStatistics,
            DescriptiveStatisticsHelper degreeStatistics,
            DescriptiveStatisticsHelper efficiencyStatistics,
            DescriptiveStatisticsHelper effectiveSizeStatistics,
            DescriptiveStatisticsHelper constraintStatistics,
            DescriptiveStatisticsHelper hierarchyStatistics,
            GlobalMeasure pairFileGlobal) {
        this.issue = issue;
        this.commit = commit;
        this.betweennessStatistics = new MetricStatistic(betweennessStatistics);
        this.closenessStatistics = new MetricStatistic(closenessStatistics);
        this.degreeStatistics = new MetricStatistic(degreeStatistics);
        this.efficiencyStatistics = new MetricStatistic(efficiencyStatistics);
        this.effectiveSizeStatistics = new MetricStatistic(effectiveSizeStatistics);
        this.constraintStatistics = new MetricStatistic(constraintStatistics);
        this.hierarchyStatistics = new MetricStatistic(hierarchyStatistics);
        this.pairFileGlobal = pairFileGlobal;
    }

    @Override
    public boolean isNew() {
        return id == null || id == 0;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public double getBetweennessSum() {
        return betweennessStatistics.getSum();
    }

    public void setBetweennessSum(double sum) {
        betweennessStatistics.setSum(sum);
    }

    public double getBetweennessMean() {
        return betweennessStatistics.getMean();
    }

    public void setBetweennessMean(double mean) {
        betweennessStatistics.setMean(mean);
    }

    public double getBetweennessMedian() {
        return betweennessStatistics.getMedian();
    }

    public void setBetweennessMedian(double median) {
        betweennessStatistics.setMedian(median);
    }

    public double getBetweennessMaximum() {
        return betweennessStatistics.getMaximum();
    }

    public void setBetweennessMaximum(double maximum) {
        betweennessStatistics.setMaximum(maximum);
    }

    public double getClosenessSum() {
        return closenessStatistics.getSum();
    }

    public void setClosenessSum(double sum) {
        closenessStatistics.setSum(sum);
    }

    public double getClosenessMean() {
        return closenessStatistics.getMean();
    }

    public void setClosenessMean(double mean) {
        closenessStatistics.setMean(mean);
    }

    public double getClosenessMedian() {
        return closenessStatistics.getMedian();
    }

    public void setClosenessMedian(double median) {
        closenessStatistics.setMedian(median);
    }

    public double getClosenessMaximum() {
        return closenessStatistics.getMaximum();
    }

    public void setClosenessMaximum(double maximum) {
        closenessStatistics.setMaximum(maximum);
    }

    public double getConstraintSum() {
        return constraintStatistics.getSum();
    }

    public void setConstraintSum(double sum) {
        constraintStatistics.setSum(sum);
    }

    public double getConstraintMean() {
        return constraintStatistics.getMean();
    }

    public void setConstraintMean(double mean) {
        constraintStatistics.setMean(mean);
    }

    public double getConstraintMedian() {
        return constraintStatistics.getMedian();
    }

    public void setConstraintMedian(double median) {
        constraintStatistics.setMedian(median);
    }

    public double getConstraintMaximum() {
        return constraintStatistics.getMaximum();
    }

    public void setConstraintMaximum(double maximum) {
        constraintStatistics.setMaximum(maximum);
    }

    public double getDegreeSum() {
        return degreeStatistics.getSum();
    }

    public void setDegreeSum(double sum) {
        degreeStatistics.setSum(sum);
    }

    public double getDegreeMean() {
        return degreeStatistics.getMean();
    }

    public void setDegreeMean(double mean) {
        degreeStatistics.setMean(mean);
    }

    public double getDegreeMedian() {
        return degreeStatistics.getMedian();
    }

    public void setDegreeMedian(double median) {
        degreeStatistics.setMedian(median);
    }

    public double getDegreeMaximum() {
        return degreeStatistics.getMaximum();
    }

    public void setDegreeMaximum(double maximum) {
        degreeStatistics.setMaximum(maximum);
    }

    public double getEffectiveSizeSum() {
        return effectiveSizeStatistics.getSum();
    }

    public void setEffectiveSizeSum(double sum) {
        effectiveSizeStatistics.setSum(sum);
    }

    public double getEffectiveSizeMean() {
        return effectiveSizeStatistics.getMean();
    }

    public void setEffectiveSizeMean(double mean) {
        effectiveSizeStatistics.setMean(mean);
    }

    public double getEffectiveSizeMedian() {
        return effectiveSizeStatistics.getMedian();
    }

    public void setEffectiveSizeMedian(double median) {
        effectiveSizeStatistics.setMedian(median);
    }

    public double getEffectiveSizeMaximum() {
        return effectiveSizeStatistics.getMaximum();
    }

    public void setEffectiveSizeMaximum(double maximum) {
        effectiveSizeStatistics.setMaximum(maximum);
    }

    public double getEfficiencySum() {
        return efficiencyStatistics.getSum();
    }

    public void setEfficiencySum(double sum) {
        efficiencyStatistics.setSum(sum);
    }

    public double getEfficiencyMean() {
        return efficiencyStatistics.getMean();
    }

    public void setEfficiencyMean(double mean) {
        efficiencyStatistics.setMean(mean);
    }

    public double getEfficiencyMedian() {
        return efficiencyStatistics.getMedian();
    }

    public void setEfficiencyMedian(double median) {
        efficiencyStatistics.setMedian(median);
    }

    public double getEfficiencyMaximum() {
        return efficiencyStatistics.getMaximum();
    }

    public void setEfficiencyMaximum(double maximum) {
        efficiencyStatistics.setMaximum(maximum);
    }

    public double getHierarchySum() {
        return hierarchyStatistics.getSum();
    }

    public void setHierarchySum(double sum) {
        hierarchyStatistics.setSum(sum);
    }

    public double getHierarchyMean() {
        return hierarchyStatistics.getMean();
    }

    public void setHierarchyMean(double mean) {
        hierarchyStatistics.setMean(mean);
    }

    public double getHierarchyMedian() {
        return hierarchyStatistics.getMedian();
    }

    public void setHierarchyMedian(double median) {
        hierarchyStatistics.setMedian(median);
    }

    public double getHierarchyMaximum() {
        return hierarchyStatistics.getMaximum();
    }

    public void setHierarchyMaximum(double maximum) {
        hierarchyStatistics.setMaximum(maximum);
    }

    public long getSize() {
        return pairFileGlobal.getSize();
    }

    public void setSize(long size) {
        pairFileGlobal.setSize(size);
    }

    public long getTies() {
        return pairFileGlobal.getTies();
    }

    public void setTies(long ties) {
        pairFileGlobal.setTies(ties);
    }

    public double getDensity() {
        return pairFileGlobal.getDensity();
    }

    public void setDensity(double density) {
        pairFileGlobal.setDensity(density);
    }

    public double getDiameter() {
        return pairFileGlobal.getDiameter();
    }

    public void setDiameter(double diameter) {
        pairFileGlobal.setDiameter(diameter);
    }

    @Override
    public String toString() {
        return id + ";" + issue.getId() + ";" + commit.getId() + ";"
//                + this.getBetweennessSum() + ";" 
//                + this.getBetweennessMean() + ";" 
                + this.getBetweennessMedian() + ";" 
//                + this.getBetweennessMaximum() + ";"
//                + this.getClosenessSum() + ";" 
//                + this.getClosenessMean() + ";" 
                + this.getClosenessMedian() + ";" 
//                + this.getClosenessMaximum() + ";"
//                + this.getDegreeSum() + ";" 
//                + this.getDegreeMean() + ";" 
                + this.getDegreeMedian() + ";" 
//                + this.getDegreeMaximum() + ";"
//                + this.getEfficiencySum() + ";" 
//                + this.getEfficiencyMean() + ";" 
                + this.getEfficiencyMedian() + ";" 
//                + this.getEfficiencyMaximum() + ";"
//                + this.getEffectiveSizeSum() + ";" 
//                + this.getEffectiveSizeMean() + ";" 
                + this.getEffectiveSizeMedian() + ";" 
//                + this.getEffectiveSizeMaximum() + ";"
//                + this.getConstraintSum() + ";" 
//                + this.getConstraintMean() + ";" 
                + this.getConstraintMedian() + ";" 
//                + this.getConstraintMaximum() + ";"
//                + this.getHierarchySum() + ";" 
//                + this.getHierarchyMean() + ";" 
                + this.getHierarchyMedian() + ";" 
//                + this.getHierarchyMaximum() + ";"
                + this.getSize() + ";" + this.getTies() + ";"
                + this.getDensity() + ";" + this.getDiameter() + ";";
    }

}
