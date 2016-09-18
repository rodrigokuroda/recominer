package br.edu.utfpr.recominer.core.repository;

import br.edu.utfpr.recominer.core.model.IssueTracker;
import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.model.VersionControl;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Repository
public class ProjectRepository extends JdbcRepository<Project, Integer> {
    
    private static final String SCHEMA_NAME = "recominer";
    private static final String TABLE_NAME = "project";
    private static final String ID_COLUMN = "id";
    
    public ProjectRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, SCHEMA_NAME, TABLE_NAME, ID_COLUMN);
    }

    @Override
    public void setProject(Project project) {
        throw new IllegalArgumentException("The schema for this repository is already set statically.");
    }

    public static final RowMapper<Project> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                Project project = new Project();
                project.setId(rs.getInt("id"));
                project.setIssueTracker(new IssueTracker(rs.getInt("issue_tracker")));
                project.setIssueTrackerUrl(rs.getString("issue_tracker_url"));
                project.setProjectName(rs.getString("project_name"));
                project.setSchemaPrefix(rs.getString("schema_prefix"));
                project.setRepositoryPath(rs.getString("repository_path"));
                project.setVersionControl(new VersionControl(rs.getInt("version_control")));
                project.setVersionControlUrl(rs.getString("version_control_url"));
                return project;
            };

    private static final RowUnmapper<Project> ROW_UNMAPPER
            = (Project p) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", p.getId());
                mapping.put("issue_tracker_id", p.getIssueTracker().getId());
                mapping.put("issue_tracker_url", p.getIssueTrackerUrl());
                mapping.put("project_name", p.getProjectName());
                mapping.put("schema_prefix", p.getSchemaPrefix());
                mapping.put("repository_path", p.getRepositoryPath());
                mapping.put("version_control", p.getVersionControl().getId());
                mapping.put("version_constrol", p.getVersionControlUrl());

                return mapping;
            };

}
