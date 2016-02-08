package br.edu.utfpr.recominer.batch.aggregator;

import br.edu.utfpr.recominer.comparator.VersionComparator;
import br.edu.utfpr.recominer.dao.GenericDao;
import br.edu.utfpr.recominer.model.Version;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
    private final String issueFixVersionInsert;
    private final String selectFixedVersion;
    private final String selectIssuesFixVersion;
    private final String issueFixVersionOrderInsert;
    private final String selectIssuesIdAndFixVersions;

    private final GenericDao dao;
    private final Project project;

    public ExtractFixVersion(final GenericDao dao, final Project project) {
        this.dao = dao;
        this.project = project;
        final String projectName = project.getProjectName();

        this.issueFixVersionInsert
                = "INSERT INTO " + projectName
                + "_issues.issues_fix_version (issue_id, fix_version, minor_fix_version, major_fix_version) VALUES (?, ?, ?, ?)";

        this.selectFixedVersion
                = "SELECT minor_fix_version FROM " + projectName
                + "_issues.issues_fix_version_order";

        this.selectIssuesFixVersion
                = "SELECT issue_id, fix_version FROM "
                + projectName + "_issues.issues_fix_version";

        this.issueFixVersionOrderInsert
                = "INSERT INTO " + projectName
                + "_issues.issues_fix_version_order (minor_fix_version, major_fix_version, version_order) VALUES (?, ?, ?)";

        this.selectIssuesIdAndFixVersions
                = "SELECT DISTINCT i.id, iej.fix_version FROM " + projectName + "_issues.issues i"
                + "  JOIN " + projectName + "_issues.changes c ON c.issue_id = i.id"
                + "  JOIN " + projectName + "_issues.issues_ext_jira iej ON iej.issue_id = i.id"
                + " WHERE UPPER(iej.issue_key) = ?"
                + "   AND i.resolution = 'Fixed'"
                + "   AND c.field = 'Resolution'"
                + "   AND c.new_value = i.resolution";;
    }

    public void extract() {
        List<Object[]> issuesFixVersion = dao.selectNativeWithParams(selectIssuesFixVersion, new Object[0]);
        final Set<Version> distincMinorVersion = new HashSet<>();
        final Map<Integer, Set<Version>> versionsInDb = new HashMap<>();
        issuesFixVersion.forEach(o -> {
            final Integer issueIdFromDb = (Integer) o[0];
            final Version issueFixVersionFromDb = new Version((String) o[1]);
            if (versionsInDb.containsKey(issueIdFromDb)) {
                versionsInDb.get(issueIdFromDb).add(issueFixVersionFromDb);
            } else {
                Set<Version> set = new HashSet<>();
                set.add(issueFixVersionFromDb);
                versionsInDb.put(issueIdFromDb, set);
            }
        });
        final List<Object[]> issuesIdAndFixVersions = (List<Object[]>) dao.selectNativeOneWithParams(selectIssuesIdAndFixVersions, new Object[0]);
        for (Object[] raw : issuesIdAndFixVersions) {
            Integer issueId = (Integer) raw[0];
            List<String> versions = Arrays.asList(((String) raw[1]).split(","));

            if (versions.isEmpty() || versions.get(0).isEmpty()) {
                log.info("Issue " + issueId + " has no fix version.");
            } else {
                //                log.info("Issue " + issueId + " is fixed in " + versions.size() + " versions.");

                if (versionsInDb.containsKey(issueId)) {
                    Set<Version> versionsFromDbForIssue = versionsInDb.get(issueId);
                    versions.removeAll(versionsFromDbForIssue);
                    continue;
                } else {
                    versionsInDb.put(issueId, new HashSet<>());
                }
                for (String version : versions) {
                    String minorVersion = getMinorVersion(version);
//                        String majorVersion = getMajorVersion(version);

                    versionsInDb.get(issueId).add(new Version(minorVersion));

                    dao.executeNativeQuery(issueFixVersionInsert, new Object[]{issueId, version, minorVersion, minorVersion});
                }
            }
        }

        final List<String> minorVersionsExisting = (List<String>) dao.selectNativeWithParams(
                selectFixedVersion, new Object[0]);
        final Set<String> minorVersionsExistingSet = new HashSet<>(minorVersionsExisting);

        if (minorVersionsExisting.containsAll(distincMinorVersion.stream().map(v -> v.getVersion()).collect(Collectors.toList()))) {
            List<Version> minorVersionsOrdered = new ArrayList<>(distincMinorVersion);

            Collections.sort(minorVersionsOrdered, new VersionComparator());
            final List<Version> versionsToInsert = minorVersionsOrdered.stream().filter(v -> !minorVersionsExistingSet.contains(v.getVersion())).collect(Collectors.toList());
            int order = 1;
            for (Version minorVersion : versionsToInsert) {
                dao.executeNativeQuery(issueFixVersionOrderInsert, new Object[]{minorVersion.getVersion(), minorVersion.getVersion(), order++});
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
