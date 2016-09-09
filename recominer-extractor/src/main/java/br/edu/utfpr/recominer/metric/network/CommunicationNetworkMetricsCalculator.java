package br.edu.utfpr.recominer.metric.network;

import br.edu.utfpr.recominer.metric.network.centrality.BetweennessCalculator;
import br.edu.utfpr.recominer.metric.network.centrality.ClosenessCalculator;
import br.edu.utfpr.recominer.metric.network.centrality.DegreeCalculator;
import br.edu.utfpr.recominer.model.Commenter;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.statistic.DescriptiveStatisticsHelper;
import edu.uci.ics.jung.graph.Graph;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class CommunicationNetworkMetricsCalculator {

    private final CommunicationNetworkBuilder networkBuilder;

    public CommunicationNetworkMetricsCalculator(CommunicationNetworkRepository communicationNetworkDao) {
        networkBuilder = new CommunicationNetworkBuilder(communicationNetworkDao);
    }

    public NetworkMetrics calcule(final Issue issue, final Commit commit) {
        final Network<String, String> network = networkBuilder.build(issue, commit);

        final Graph<String, String> issueGraph = network.getNetwork();
        final Map<String, Integer> edgesWeigth = network.getEdgesWeigth();
        final Set<Commenter> devsCommentters = new HashSet<>(network.getCommenters());

        final GlobalMeasure pairFileGlobal = GlobalMeasureCalculator.calcule(issueGraph);

        final Map<String, Double> betweenness = BetweennessCalculator.calcule(issueGraph, edgesWeigth);
        final Map<String, Double> closeness = ClosenessCalculator.calcule(issueGraph, edgesWeigth);
        final Map<String, Integer> degree = DegreeCalculator.calcule(issueGraph);
        final Map<String, EgoMeasure<String>> ego = EgoMeasureCalculator.calcule(issueGraph, edgesWeigth);
        final Map<String, StructuralHolesMeasure<String>> structuralHoles = StructuralHolesCalculator.calcule(issueGraph, edgesWeigth);

        final int size = devsCommentters.isEmpty() ? 1 : devsCommentters.size();

        DescriptiveStatisticsHelper betweennessStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper closenessStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper degreeStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper efficiencyStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper effectiveSizeStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper constraintStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper hierarchyStatistics = new DescriptiveStatisticsHelper(size);

        for (Commenter user : devsCommentters) {
            String commenter = user.getName();

            betweennessStatistics.addValue(betweenness.get(commenter));
            closenessStatistics.addValue(closeness.get(commenter));
            degreeStatistics.addValue(degree.get(commenter));

            final StructuralHolesMeasure<String> structuralHolesMetric = structuralHoles.get(commenter);
            efficiencyStatistics.addValue(structuralHolesMetric.getEfficiency());
            effectiveSizeStatistics.addValue(structuralHolesMetric.getEffectiveSize());
            constraintStatistics.addValue(structuralHolesMetric.getConstraint());
            hierarchyStatistics.addValue(structuralHolesMetric.getHierarchy());
        }

        return new NetworkMetrics(issue, commit, betweennessStatistics,
                closenessStatistics, degreeStatistics, 
                efficiencyStatistics, effectiveSizeStatistics,
                constraintStatistics, hierarchyStatistics, pairFileGlobal);
    }
}
