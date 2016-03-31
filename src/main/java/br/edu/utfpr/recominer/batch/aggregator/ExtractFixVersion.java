package br.edu.utfpr.recominer.batch.aggregator;

import br.edu.utfpr.recominer.comparator.VersionComparator;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.dao.QueryUtils;
import br.edu.utfpr.recominer.model.Version;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class ExtractFixVersion {

    private final Logger log = LogManager.getLogger();
    private final String insertIssueFixVersion;
    private final String deleteFixedVersionOrder;
    private final String selectFixedVersionOrder;
    private final String selectExistingIssuesRelatedToFixVersion;
    private final String insertFixedVersionOrder;
    private final String selectIssuesIdAndFixVersions;

    private final GenericDao dao;
    private final Project project;

    public ExtractFixVersion(final GenericDao dao, final Project project) {
        this.dao = dao;
        this.project = project;
        final String projectName = project.getProjectName();

        this.insertIssueFixVersion
                = QueryUtils.getQueryForDatabase(
                        "INSERT INTO "
                        + "{0}.issues_fix_version (issue_id, fix_version, minor_fix_version, major_fix_version) "
                        + "VALUES (?, ?, ?, ?)", projectName);

        this.selectFixedVersionOrder
                = QueryUtils.getQueryForDatabase(
                        "SELECT minor_fix_version "
                        + "  FROM {0}.issues_fix_version_order", projectName);

        this.selectExistingIssuesRelatedToFixVersion
                = QueryUtils.getQueryForDatabase(
                        "SELECT issue_id, fix_version "
                        + "  FROM {0}.issues_fix_version", projectName);

        this.insertFixedVersionOrder
                = QueryUtils.getQueryForDatabase(
                        "INSERT INTO "
                        + "{0}.issues_fix_version_order (minor_fix_version, major_fix_version, version_order) "
                        + "VALUES (?, ?, ?)", projectName);

        this.selectIssuesIdAndFixVersions
                = QueryUtils.getQueryForDatabase(
                        "SELECT i.id, iej.fix_version "
                        + "  FROM {0}.issues i"
                        + "  JOIN {0}_issues.issues_ext_jira iej ON iej.issue_id = i.id"
                        + " WHERE i.fixed_on IS NOT NULL"
                        + "   AND i.updated_on > ?", projectName);
        
        this.deleteFixedVersionOrder 
                = QueryUtils.getQueryForDatabase(
                    "DELETE FROM {0}.issues_fix_version_order", projectName);
    }

    public void extract() {
        final List<Object[]> existingIssuesRelatedToVersionRaw
                = dao.selectNativeWithParams(selectExistingIssuesRelatedToFixVersion);

        // group versions by issues
        final Map<Integer, Set<String>> existingIssuesRelatedToVersion
                = existingIssuesRelatedToVersionRaw.stream()
                .collect(
                        Collectors.groupingBy(
                                o -> (Integer) o[0],
                                Collectors.mapping(o -> (String) o[1], Collectors.toSet())));

//        existingIssuesRelatedToVersion.forEach(o -> {
//            final Integer issueIdFromDb = (Integer) o[0];
//            final Version issueFixVersionFromDb = new Version((String) o[1]);
//            if (existingIssuesRelatedToVersion.containsKey(issueIdFromDb)) {
//                existingIssuesRelatedToVersion.get(issueIdFromDb).add(issueFixVersionFromDb);
//            } else {
//                Set<Version> set = new HashSet<>();
//                set.add(issueFixVersionFromDb);
//                existingIssuesRelatedToVersion.put(issueIdFromDb, set);
//            }
//        });
        final List<Object[]> issuesIdAndFixVersions
                = dao.selectNativeOneWithParams(selectIssuesIdAndFixVersions, 
                        project.getLastIssueUpdateAnalyzedForVersion());

        final Set<String> distincMinorVersion = new HashSet<>();
        
        for (Object[] raw : issuesIdAndFixVersions) {
            final Integer issueId = (Integer) raw[0];
            final List<String> versions = Arrays.asList(((String) raw[1]).split(","));

            if (versions.isEmpty() || versions.get(0).isEmpty()) {
                log.info("Issue " + issueId + " has no fix version.");
            } else {
                //                log.info("Issue " + issueId + " is fixed in " + versions.size() + " versions.");

                if (existingIssuesRelatedToVersion.containsKey(issueId)) {
                    final Set<String> versionsFromDbForIssue = existingIssuesRelatedToVersion.get(issueId);
                    versions.removeAll(versionsFromDbForIssue);
                } else {
                    existingIssuesRelatedToVersion.put(issueId, new HashSet<>(versions));
                }
                for (String version : versions) {
                    final String minorVersion = getMinorVersion(version);
//                        String majorVersion = getMajorVersion(version);

                    existingIssuesRelatedToVersion.get(issueId).add(minorVersion);
                    distincMinorVersion.add(minorVersion);
                    dao.executeNativeQuery(insertIssueFixVersion, issueId, version, minorVersion, minorVersion);
                }
            }
        }

        final List<String> existingMinorVersionOrder
                = (List<String>) dao.selectNativeWithParams(
                        selectFixedVersionOrder);
//        final Set<String> existingMinorVersionsSet
//                = new HashSet<>(existingMinorVersionOrder);

        if (!existingMinorVersionOrder.containsAll(distincMinorVersion)) {
            existingMinorVersionOrder.addAll(distincMinorVersion);
            final List<Version> minorVersionsOrdered
                    = existingMinorVersionOrder.stream()
                            .map(Version::new)
                            .sorted(new VersionComparator())
                            //.filter(v -> !existingMinorVersionsSet.contains(v.getVersion()))
                            .collect(Collectors.toList());
            
            int order = 1;
            for (Version minorVersion : minorVersionsOrdered) {
                dao.executeNativeQuery(deleteFixedVersionOrder);
                dao.executeNativeQuery(insertFixedVersionOrder,
                        minorVersion.getVersion(),
                        minorVersion.getVersion(),
                        order++);
            }
        }

    }

    // 1.1.1 > 1
    private String getMajorVersion(String version) {
        String majorVersion;
        String[] versionsSplited = version.split("[.]");
        if (versionsSplited.length > 1) {
            majorVersion = versionsSplited[0];
        } else {
            majorVersion = version;
        }
        return majorVersion;
    }

    // 1.1.1 > 1.1
    private String getMinorVersion(String version) {
        String minorVersion;
        String[] versionsSplited = version.split("[.]");
        if (versionsSplited.length > 2) {
            minorVersion = versionsSplited[0] + "." + versionsSplited[1];
        } else {
            minorVersion = version;
        }
        return minorVersion;
    }
}
