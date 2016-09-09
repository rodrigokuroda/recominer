package br.edu.utfpr.recominer.metric.network;

import br.edu.utfpr.recominer.statistic.DescriptiveStatisticsHelper;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
public class MetricStatistic {
    
    private double sum;
    private double mean;
    private double median;
    private double maximum;

    public MetricStatistic() {
    }
    
    public MetricStatistic(DescriptiveStatisticsHelper statistic) {
        sum = statistic.getSum();
        mean = statistic.getMean();
        median = statistic.getMedian();
        maximum = statistic.getMax();
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }
}
