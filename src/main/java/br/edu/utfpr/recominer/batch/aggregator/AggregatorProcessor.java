package br.edu.utfpr.recominer.batch.aggregator;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Named;

/**
 *
 * @author Rodrigo T. Kuroda
 */
@Named
public class AggregatorProcessor implements ItemProcessor {

    @Override
    public Object processItem(Object item) throws Exception {
        System.out.println("Processing: " + item);
//        final Statement statement = conn.createStatement();
//        final ResultSet commitMessages = statement.executeQuery("SELECT id, message FROM " + project + "_vcs.scmlog WHERE num_files <= 20");
//        final Set<Commit> commits = new HashSet<>();
//
//        log.info("Querying commits...");
//        while (commitMessages.next()) {
//            commits.add(new Commit(commitMessages.getInt("id"), commitMessages.getString("message")));
//        }
//
//        int totalPatternOccurrences = 0;
//        int totalPatternRelatedWithAnIssue = 0;
//
//        conn.setAutoCommit(false);
//
//        ResultSet queryIfIssuesIsFromJira = conn.prepareStatement("SELECT 1 "
//                + "FROM information_schema.tables "
//                + "WHERE table_schema = '" + project + "_issues' "
//                + "    AND table_name = 'issues_ext_bugzilla' "
//                + "LIMIT 1").executeQuery();
//        boolean isIssuesFromBugzilla = queryIfIssuesIsFromJira.next();
//
//        final String selectIssueId;
//        final String issueReferencePattern;
//        if (isIssuesFromBugzilla) {
//            issueReferencePattern = "(?i)(bug|issue|fixed|fix|bugzilla)+(\\s)*(id|for)?(:|-)?\\s*#?\\s*(\\d+)(,\\s*\\d+)*";
//            selectIssueId = "SELECT id FROM " + project + "_issues.issues WHERE issue = ?";
//        } else {
//            issueReferencePattern = buildPatternByName(project);
//            selectIssueId
//                    = "SELECT DISTINCT i.id, iej.fix_version FROM " + project + "_issues.issues i"
//                    + "  JOIN " + project + "_issues.changes c ON c.issue_id = i.id"
//                    + "  JOIN " + project + "_issues.issues_ext_jira iej ON iej.issue_id = i.id"
//                    + " WHERE UPPER(iej.issue_key) = ?"
//                    + "   AND i.resolution = 'Fixed'"
//                    + "   AND c.field = 'Resolution'"
//                    + "   AND c.new_value = i.resolution";
//        }
//
//        final int totalIssues;
//        try (PreparedStatement countIssuesStatement = conn.prepareStatement("SELECT COUNT(1) FROM " + project + "_issues.issues");
//                ResultSet countIssuesResult = countIssuesStatement.executeQuery();) {
//            countIssuesResult.next();
//            totalIssues = countIssuesResult.getInt(1);
//        }
//
//        final int totalCommits;
//        try (PreparedStatement countCommitsStatement = conn.prepareStatement("SELECT COUNT(1) FROM " + project + "_vcs.scmlog");
//                ResultSet countCommitsResult = countCommitsStatement.executeQuery();) {
//            countCommitsResult.next();
//            totalCommits = countCommitsResult.getInt(1);
//        }
//
//        final Pattern regex = Pattern.compile(issueReferencePattern, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
//        final Pattern regexNumber = Pattern.compile("\\d+");
//
//        final Map<Integer, List<String>> fixedIssuesIdFixVersion = new HashMap<>();
//        final Set<Integer> fixedIssuesSet = new HashSet<>();
//
//        int totalCommitsWithOccurrences = 0;
//
//        for (Commit commit : commits) {
//            // remove "git-svn-id: https://svn.apache.org/*" from message
//            // to avoid false positive matches of pattern
//            // (e.g. git-svn-id: https://svn.apache.org/camel-1.1.0)
//            final String commitMessage = replaceUrl(commit.getMessage());
//            final Matcher matcher = regex.matcher(commitMessage);
//
//            int matcherCount = 0;
//
//            // para cada ocorrência do padrão
//            while (matcher.find()) {
//
//                String issueKey = matcher.group().replace(" ", ""); // e.g.: ARIES-1234
//
//                if (isIssuesFromBugzilla) {
//                    Matcher matcherNumber = regexNumber.matcher(issueKey);
//                    if (matcherNumber.find()) {
//                        issueKey = matcherNumber.group(); // e.g.: 1234
//                    } else {
//                        log.info("Not found issue for match pattern " + issueKey);
//                    }
//                }
//
//                totalPatternOccurrences++;
//                matcherCount++;
//                PreparedStatement selectRelatedIssue = conn.prepareStatement(selectIssueId);
//                selectRelatedIssue.setString(1, issueKey.toUpperCase());
//
//                ResultSet executeQuery = selectRelatedIssue.executeQuery();
//
//                if (executeQuery.next()) {
//                    try (PreparedStatement queryToRelate = conn.prepareStatement(
//                            "INSERT INTO " + project
//                            + "_issues.issues_scmlog (issue_id, scmlog_id) VALUES (?, ?)")) {
//                        final int issueId = executeQuery.getInt(1);
//                        queryToRelate.setInt(1, issueId);
//                        queryToRelate.setInt(2, commit.getId());
//
//                        // adiciona as versões da issue corrigida
//                        fixedIssuesIdFixVersion.put(issueId, Arrays.asList(executeQuery.getString(2).split(",")));
//                        // adiciona a issue corrigida
//                        fixedIssuesSet.add(issueId);
//                        try {
//                            queryToRelate.execute();
//                            totalPatternRelatedWithAnIssue++;
//                        } catch (MySQLIntegrityConstraintViolationException e) {
//                            log.info("Issue " + issueId + " and commit " + commit.getId() + " already exists.");
//                        }
//                    }
//                }
//            }
//            if (matcherCount > 0) {
//                totalCommitsWithOccurrences++;
//            } else {
//                log.info(commitMessage);
//            }
////            log.info(matcherCount + " ocorrências para o commit " + commit.getId());
//        }
//
//        PreparedStatement issueFixVersionInsert = conn.prepareStatement(
//                "INSERT INTO " + project
//                + "_issues.issues_fix_version (issue_id, fix_version, minor_fix_version, major_fix_version) VALUES (?, ?, ?, ?)");
//
//        int countIssuesWithFixVersion = 0;
//
//        Set<String> distincMinorVersion = new HashSet<>();
//        for (Map.Entry<Integer, List<String>> entrySet : fixedIssuesIdFixVersion.entrySet()) {
//            Integer issueId = entrySet.getKey();
//            List<String> versions = entrySet.getValue();
//
//            if (versions.isEmpty() || versions.get(0).isEmpty()) {
//                log.info("Issue " + issueId + " has no fix version.");
//            } else {
////                log.info("Issue " + issueId + " is fixed in " + versions.size() + " versions.");
//
//                for (String version : versions) {
//                    try {
//                        issueFixVersionInsert.setInt(1, issueId);
//                        issueFixVersionInsert.setString(2, version);
//
//                        String minorVersion = getMinorVersion(version);
//                        issueFixVersionInsert.setString(3, minorVersion);
//
////                        String majorVersion = getMajorVersion(version);
//                        issueFixVersionInsert.setString(4, minorVersion);
//
//                        distincMinorVersion.add(minorVersion);
//
//                        issueFixVersionInsert.execute();
//                    } catch (MySQLIntegrityConstraintViolationException e) {
//                        log.info("Issue " + issueId + " and version " + version + " already exists.");
//                    }
//                }
//
//                countIssuesWithFixVersion++;
//            }
//        }
//
//        issueFixVersionInsert.close();
//
//        List<String> minorVersionsOrdered = new ArrayList<>(distincMinorVersion);
//        log.info(Arrays.toString(minorVersionsOrdered.toArray()));
//
//        Collections.sort(minorVersionsOrdered, new VersionComparator());
//
//        PreparedStatement issueFixVersionOrderInsert = conn.prepareStatement(
//                "INSERT INTO " + project
//                + "_issues.issues_fix_version_order (minor_fix_version, major_fix_version, version_order) VALUES (?, ?, ?)");
//        int order = 1;
//        for (String minorVersion : minorVersionsOrdered) {
//            try {
//                issueFixVersionOrderInsert.setString(1, minorVersion);
////                issueFixVersionOrderInsert.setString(2, getMajorVersion(minorVersion));
//                issueFixVersionOrderInsert.setString(2, minorVersion);
//                issueFixVersionOrderInsert.setInt(3, order++);
//
//                issueFixVersionOrderInsert.execute();
//            } catch (MySQLIntegrityConstraintViolationException e) {
//                log.info("Issue " + minorVersion + " order " + order + " already exists.");
//            }
//        }
//
//        conn.commit();
//        conn.setAutoCommit(true);
//
//        log.info("\n\n"
//                + commits.size() + " of " + totalCommits + " (total) commits has less than or equal to 20 files\n"
//                + totalCommitsWithOccurrences + " of " + commits.size() + " commits has at least one occurrence of pattern \"" + issueReferencePattern + "\"\n\n"
//                + totalPatternOccurrences + " occurrences of pattern \"" + issueReferencePattern + "\" in commits' message was found\n"
//                + totalPatternRelatedWithAnIssue + " of " + totalPatternOccurrences + " occurrences was related with an issue\n\n"
//                + fixedIssuesSet.size() + " of " + totalIssues + " (total) issues was fixed\n"
//                + countIssuesWithFixVersion + " of " + fixedIssuesSet.size() + " issues has 'fix version'\n\n"
//        );
        return null;
    }
}
