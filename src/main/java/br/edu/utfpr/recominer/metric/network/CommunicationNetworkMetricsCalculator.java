package br.edu.utfpr.recominer.metric.network;

import br.edu.utfpr.recominer.metric.network.centrality.BetweennessCalculator;
import br.edu.utfpr.recominer.metric.network.centrality.ClosenessCalculator;
import br.edu.utfpr.recominer.metric.network.centrality.DegreeCalculator;
import br.edu.utfpr.recominer.model.Commenter;
import br.edu.utfpr.recominer.model.Issue;
import br.edu.utfpr.recominer.util.DescriptiveStatisticsHelper;
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

    public CommunicationNetworkMetricsCalculator(CommunicationNetworkDao communicationNetworkDao) {
        networkBuilder = new CommunicationNetworkBuilder(communicationNetworkDao);
    }

    public NetworkMetrics calcule(final Issue issue) {
        final Network<String, String> network = networkBuilder.build(issue);

        final Graph<String, String> issueGraph = network.getNetwork();
        final Map<String, Integer> edgesWeigth = network.getEdgesWeigth();
        final Set<Commenter> devsCommentters = new HashSet<>(network.getCommenters());

        final GlobalMeasure pairFileGlobal = GlobalMeasureCalculator.calcule(issueGraph);

        // Map<String, Double> barycenter = BarycenterCalculator.calcule(pairFileGraph, edgesWeigth);
        final Map<String, Double> betweenness = BetweennessCalculator.calcule(issueGraph, edgesWeigth);
        final Map<String, Double> closeness = ClosenessCalculator.calcule(issueGraph, edgesWeigth);
        final Map<String, Integer> degree = DegreeCalculator.calcule(issueGraph);
        // Map<String, Double> eigenvector = EigenvectorCalculator.calcule(pairFileGraph, edgesWeigth);
        final Map<String, EgoMeasure<String>> ego = EgoMeasureCalculator.calcule(issueGraph, edgesWeigth);
        final Map<String, StructuralHolesMeasure<String>> structuralHoles = StructuralHolesCalculator.calcule(issueGraph, edgesWeigth);

        final int size = devsCommentters.isEmpty() ? 1 : devsCommentters.size();

//      DescriptiveStatisticsHelper barycenterStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper betweennessStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper closenessStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper degreeStatistics = new DescriptiveStatisticsHelper(size);
//      DescriptiveStatisticsHelper eigenvectorStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper egoBetweennessStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper egoSizeStatistics = new DescriptiveStatisticsHelper(size);
//      DescriptiveStatisticsHelper egoPairsStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper egoTiesStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper egoDensityStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper efficiencyStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper effectiveSizeStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper constraintStatistics = new DescriptiveStatisticsHelper(size);
        DescriptiveStatisticsHelper hierarchyStatistics = new DescriptiveStatisticsHelper(size);

        for (Commenter user : devsCommentters) {
            String commenter = user.getName();

//          barycenterStatistics.addValue(barycenter.get(commenter));
            betweennessStatistics.addValue(betweenness.get(commenter));
            closenessStatistics.addValue(closeness.get(commenter));
            degreeStatistics.addValue(degree.get(commenter));
//          eigenvectorStatistics.addValue(eigenvector.get(commenter));

            final EgoMeasure<String> egoMetrics = ego.get(commenter);
            egoBetweennessStatistics.addValue(egoMetrics.getBetweennessCentrality());
            egoSizeStatistics.addValue(egoMetrics.getSize());
//          egoPairsStatistics.addValue(ego.get(commenter).getPairs());
            egoTiesStatistics.addValue(egoMetrics.getTies());
            egoDensityStatistics.addValue(egoMetrics.getDensity());

            final StructuralHolesMeasure<String> structuralHolesMetric = structuralHoles.get(commenter);
            efficiencyStatistics.addValue(structuralHolesMetric.getEfficiency());
            effectiveSizeStatistics.addValue(structuralHolesMetric.getEffectiveSize());
            constraintStatistics.addValue(structuralHolesMetric.getConstraint());
            hierarchyStatistics.addValue(structuralHolesMetric.getHierarchy());
        }

        return new NetworkMetrics(betweennessStatistics,
                closenessStatistics, degreeStatistics, egoBetweennessStatistics,
                egoSizeStatistics, egoTiesStatistics, egoDensityStatistics,
                efficiencyStatistics, effectiveSizeStatistics,
                constraintStatistics, hierarchyStatistics, pairFileGlobal);
    }

    public void saveNetworkMetrics(final NetworkMetrics networkMetrics) {
        final String insertNetworkMetrics
                = "INSERT INTO avro.communication_network_metric "
                + "(issue_id, "
                + "comment_updated_on, "
                + "btwSum, "
                + "btwAvg, "
                + "btwMdn, "
                + "btwMax, "
                + "clsSum, "
                + "clsAvg, "
                + "clsMdn, "
                + "clsMax, "
                + "dgrSum, "
                + "dgrAvg, "
                + "dgrMdn, "
                + "dgrMax, "
                + "egoBtwSum, "
                + "egoBtwAvg, "
                + "egoBtwMdn, "
                + "egoBtwMax, "
                + "egoSizeSum, "
                + "egoSizeAvg, "
                + "egoSizeMdn, "
                + "egoSizeMax, "
                + "egoTiesSum, "
                + "egoTiesAvg, "
                + "egoTiesMdn, "
                + "egoTiesMax, "
                + "egoDensitySum, "
                + "egoDensityAvg, "
                + "egoDensityMdn, "
                + "egoDensityMax, "
                + "efficiencySum, "
                + "efficiencyAvg, "
                + "efficiencyMdn, "
                + "efficiencyMax, "
                + "efvSizeSum, "
                + "efvSizeAvg, "
                + "efvSizeMdn, "
                + "efvSizeMax, "
                + "constraintSum, "
                + "constraintAvg, "
                + "constraintMdn, "
                + "constraintMax, "
                + "hierarchySum, "
                + "hierarchyAvg, "
                + "hierarchyMdn, "
                + "hierarchyMax, "
                + "size, "
                + "ties, "
                + "density, "
                + "diameter) "
                + "VALUES "
                + "(?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?) ";
        
        
        
    }
}
