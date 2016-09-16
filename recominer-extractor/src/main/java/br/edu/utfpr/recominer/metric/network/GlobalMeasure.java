package br.edu.utfpr.recominer.metric.network;

/**
 * Stores the following local measure of the vertex <code>V</code>: - in degree:
 * incoming edges - out degree: outcoming edges - in and out degree: in degree +
 * out degree
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class GlobalMeasure {

    private long size;
    private long ties;
    private long pairs;
    private double density;
    private double diameter;

    public GlobalMeasure(long size, long ties) {
        this.size = size;
        this.ties = ties;
        this.pairs = size * (size - 1);
        this.density = pairs == 0 ? 1 : (double) ties / (double) pairs;
        this.diameter = Double.NaN;
    }
    
    public GlobalMeasure(long size, long ties, double diameter) {
        this.size = size;
        this.ties = ties;
        this.pairs = size * (size - 1);
        this.density = pairs == 0 ? 1 : (double) ties / (double) pairs;
        this.diameter = diameter;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTies() {
        return ties;
    }

    public void setTies(long ties) {
        this.ties = ties;
    }

    public long getPairs() {
        return pairs;
    }

    public void setPairs(long pairs) {
        this.pairs = pairs;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    @Override
    public String toString() {
        return size + ";" + ties + ";"
                + density + ";" + diameter + ";";
    }

}
