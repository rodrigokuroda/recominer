package br.edu.utfpr.recominer.batch.extractor;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.model.Version;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class ExtractFixVersion {

    private final Logger log = LoggerFactory.getLogger(ExtractFixVersion.class);

    private final String insertIssueFixVersion;
    private final String deleteFixedVersionOrder;
    private final String selectFixedVersionOrder;
    private final String selectExistingIssuesRelatedToFixVersion;
    private final String insertFixedVersionOrder;
    private final String selectIssuesIdAndFixVersions;
    private final Project project;

    private JdbcTemplate template;

    public ExtractFixVersion(final JdbcTemplate template, final Project project) {
        this.template = template;
        this.project = project;

        // TODO mover para um Repository
        this.insertIssueFixVersion
                = QueryUtils.getQueryForDatabase(
                        "INSERT INTO "
                        + "{0}.issues_fix_version (issue_id, fix_version, minor_fix_version, major_fix_version) "
                        + "VALUES (?, ?, ?, ?)", project);

        this.selectFixedVersionOrder
                = QueryUtils.getQueryForDatabase(
                        "SELECT minor_fix_version "
                        + "  FROM {0}.issues_fix_version_order", project);

        this.selectExistingIssuesRelatedToFixVersion
                = QueryUtils.getQueryForDatabase(
                        "SELECT issue_id, fix_version "
                        + "  FROM {0}.issues_fix_version", project);

        this.insertFixedVersionOrder
                = QueryUtils.getQueryForDatabase(
                        "INSERT INTO "
                        + "{0}.issues_fix_version_order (minor_fix_version, major_fix_version, version_order) "
                        + "VALUES (?, ?, ?)", project);

        this.selectIssuesIdAndFixVersions
                = QueryUtils.getQueryForDatabase(
                        "SELECT i.id, iej.fix_version "
                        + "  FROM {0}.issues i"
                        + "  JOIN {0}_issues.issues_ext_jira iej ON iej.issue_id = i.id"
                        + " WHERE i.fixed_on IS NOT NULL"
                        + "   AND i.updated_on > ?", project);

        this.deleteFixedVersionOrder
                = QueryUtils.getQueryForDatabase(
                        "DELETE FROM {0}.issues_fix_version_order", project);
    }

    public void extract() {

        // group versions by issues
        final Map<Integer, Set<String>> existingIssuesRelatedToVersion
                = template.query(selectIssuesIdAndFixVersions,
                        (ResultSet rs) -> {
                            Map<Integer, Set<String>> map = new HashMap<>();
                            while (rs.next()) {
                                map.put(rs.getInt(1), new HashSet<>(Arrays.asList(rs.getString(2))));
                            }
                            return map;
                        }
                );

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
        final List<IssueAndFixVersions> issuesIdAndFixVersions
                = template.query(selectIssuesIdAndFixVersions,
                        (ResultSet rs, int rowNum) -> new IssueAndFixVersions(rs.getInt(1), Arrays.asList(rs.getString(2))),
                        project.getLastIssueUpdateAnalyzedForVersion());

        final Set<String> distincMinorVersion = new HashSet<>();

        for (IssueAndFixVersions raw : issuesIdAndFixVersions) {
            final Integer issueId = raw.getIssue();
            final List<String> versions = raw.getFixVersions();

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
                    template.update(insertIssueFixVersion, issueId, version, minorVersion, minorVersion);
                }
            }
        }

        final List<String> existingMinorVersionOrder
                = (List<String>) template.query(
                        selectFixedVersionOrder, (ResultSet rs, int rowNum) -> rs.getString(1));
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
                template.execute(deleteFixedVersionOrder);
                template.update(insertFixedVersionOrder,
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

    private static class IssueAndFixVersions {

        private Integer issue;
        private List<String> fixVersions;

        private IssueAndFixVersions(int issue, List<String> fixVersions) {
            this.issue = issue;
            this.fixVersions = fixVersions;
        }

        public Integer getIssue() {
            return issue;
        }

        public void setIssue(Integer issue) {
            this.issue = issue;
        }

        public List<String> getFixVersions() {
            return fixVersions;
        }

        public void setFixVersions(List<String> fixVersions) {
            this.fixVersions = fixVersions;
        }

    }
}
