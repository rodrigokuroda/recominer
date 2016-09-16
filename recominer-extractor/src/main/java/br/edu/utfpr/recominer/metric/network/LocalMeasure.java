package br.edu.utfpr.recominer.metric.network;

/**
 * Stores the following local measure of the vertex <code>V</code>: - in degree:
 * incoming edges - out degree: outcoming edges - in and out degree: in degree +
 * out degree
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 * @param <V> Class of vertex
 */
public class LocalMeasure<V> extends Measure<V> {

    private final long inDegree;
    private final long outDegree;
    private final long inOutDegree;
    private final double clusteringCoefficient;

    public LocalMeasure(V vertex, long inDegree, long outDegree) {
        super(vertex);
        this.inDegree = inDegree;
        this.outDegree = outDegree;
        this.inOutDegree = inDegree + outDegree;
        this.clusteringCoefficient = Double.NaN;
    }

    public LocalMeasure(V vertex, long inDegree, long outDegree, 
            double clusteringCoefficient) {
        super(vertex);
        this.inDegree = inDegree;
        this.outDegree = outDegree;
        this.inOutDegree = inDegree + outDegree;
        this.clusteringCoefficient = clusteringCoefficient;
    }

    public long getInDegree() {
        return inDegree;
    }

    public long getOutDegree() {
        return outDegree;
    }

    public long getInOutDegree() {
        return inOutDegree;
    }

    public double getClusteringCoefficient() {
        return clusteringCoefficient;
    }

    @Override
    public String toString() {
        return super.toString()
                + ", in degree: " + inDegree
                + ", out degree: " + outDegree
                + ", in and out degree: " + inOutDegree
                + ", clusteringCoefficient: " + clusteringCoefficient;
    }

}
