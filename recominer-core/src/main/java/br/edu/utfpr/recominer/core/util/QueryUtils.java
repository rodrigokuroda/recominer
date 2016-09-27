package br.edu.utfpr.recominer.core.util;

import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.model.Project;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class QueryUtils {

    public static String getQueryForDatabase(String query, Project project) {
        return MessageFormat.format(query, project.getSchemaPrefix());
    }

    public static String getQueryForDatabase(String query, Project project, String... params) {
        if (project != null) {
            List<String> paramsList = new ArrayList<>(Arrays.asList(params));
            paramsList.add(0, project.getSchemaPrefix());
            return MessageFormat.format(query, paramsList.toArray());
        } else {
            return MessageFormat.format(query, (Object[]) params);
        }
    }

    public static void filterByIssues(Collection<Integer> issues, StringBuilder sql) {
        if (issues != null && !issues.isEmpty()) {
            sql.append(" AND i.id IN (");
            boolean appendComma = false;
            for (Integer issue : issues) {
                if (appendComma) {
                    sql.append(",");
                }
                sql.append(issue);
                appendComma = true;
            }
            sql.append(")");
        }
    }

    public static void filterByIssues(Set<Issue> issues, StringBuilder sql) {
        if (issues != null && !issues.isEmpty()) {
            sql.append(" AND i.id IN (");
            boolean appendComma = false;
            for (Issue issue : issues) {
                if (appendComma) {
                    sql.append(",");
                }
                sql.append(issue.getId());
                appendComma = true;
            }
            sql.append(")");
        }
    }

    public static void filterByIssues(Integer issues, StringBuilder sql) {
        if (issues != null) {
            sql.append(" AND i.id = ")
                    .append(issues)
                    .append(" ");
        }
    }

    /**
     * Get statement from file, replacing all comments SQL command (starts with
     * "--" and ends with "\n" or ";") in order to check if is a empty SQL
     * command.
     *
     * @param sql SQL statement without comments
     * @return
     */
    public static String removeComments(String sql) {
        return sql.replaceAll("(\\s)*-{2,}.*(\r?\n|;)", "");
    }
}
