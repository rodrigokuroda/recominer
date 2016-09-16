package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.core.model.Cochange;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import br.edu.utfpr.recominer.core.model.FilePair;
import br.edu.utfpr.recominer.model.FilePairIssueCommit;
import br.edu.utfpr.recominer.core.model.Issue;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Repository
public class FilePairIssueCommitRepository extends JdbcRepository<FilePairIssueCommit, Integer> {

    public FilePairIssueCommitRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "file_pair_issue_commit";

    public static final RowMapper<FilePairIssueCommit> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                FilePairIssueCommit filePairIssueCommit = new FilePairIssueCommit();
                filePairIssueCommit.setFilePair(
                        new FilePair(
                                new File(rs.getInt("file1_id")),
                                new File(rs.getInt("file2_id")))
                );
                filePairIssueCommit.setIssue(new Issue(rs.getInt("issue_id")));
                filePairIssueCommit.setCommit(new Commit(rs.getInt("commit_id")));
                return filePairIssueCommit;
            };

    public static final RowUnmapper<FilePairIssueCommit> ROW_UNMAPPER
            = (FilePairIssueCommit filePairIssueCommit) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", filePairIssueCommit.getId());
                mapping.put("file1_id", filePairIssueCommit.getFilePair().getFile1().getId());
                mapping.put("file2_id", filePairIssueCommit.getFilePair().getFile2().getId());
                mapping.put("issue_id", filePairIssueCommit.getIssue().getId());
                mapping.put("commit_id", filePairIssueCommit.getCommit().getId());
                return mapping;
            };

    public void save(File changedFile, List<Cochange> cochangedFilesInCommit,
            Issue issue, Commit commit) {
        for (Cochange cochange : cochangedFilesInCommit) {
            FilePairIssueCommit filePairIssueCommit = new FilePairIssueCommit(
                    new FilePair(changedFile, cochange.getFile()), issue, commit);

            Integer id = selectIdOf(filePairIssueCommit);

            if (id == null) {
                save(filePairIssueCommit);
            } else {
                filePairIssueCommit.setId(id);
            }
        }
    }

    public List<FilePairIssueCommit> selectCochangesOf(File changedFile, Commit untilCommit) {
        return jdbcOperations.query(
                getQueryForSchema(
                        "SELECT fpic.id, "
                        + "     fpic.issue_id, "
                        + "     fpic.commit_id, "
                        + "     fpic.file1_id, "
                        + "     fpic.file2_id"
                        + "  FROM {0}.file_pair_issue_commit fpic "
                        + "  JOIN {0}_vcs.scmlog s ON s.id = fpic.commit_id"
                        + " WHERE fpic.file1_id = ?"
                        + "   AND s.date <= "
                        + "    (SELECT s2.date "
                        + "       FROM {0}_vcs.scmlog s2 "
                        + "      WHERE s2.id = ?)"),
                (ResultSet rs, int rowNum) -> {
                    FilePairIssueCommit filePairIssueCommit = new FilePairIssueCommit(rs.getInt("id"));
                    filePairIssueCommit.setFilePair(
                            new FilePair(new File(rs.getInt("file1_id")),
                                    new File(rs.getInt("file2_id")))
                    );
                    filePairIssueCommit.setIssue(new Issue(rs.getInt("issue_id")));
                    filePairIssueCommit.setCommit(new Commit(rs.getInt("commit_id")));
                    return filePairIssueCommit;
                },
                changedFile.getId(),
                untilCommit.getId());
    }

    public Integer selectIdOf(FilePairIssueCommit filePairIssueCommit) {
        try {
            return jdbcOperations.queryForObject(
                    getQueryForSchema(
                            "SELECT fpic.id "
                            + "  FROM {0}.file_pair_issue_commit fpic "
                            + "  JOIN {0}_vcs.scmlog s ON s.id = fpic.commit_id"
                            + " WHERE fpic.file1_id = ?"
                            + "   AND fpic.file2_id = ?"
                            + "   AND fpic.issue_id = ?"
                            + "   AND fpic.commit_id = ?"),
                    (ResultSet rs, int rowNum) -> {
                        return rs.getInt("id");
                    },
                    filePairIssueCommit.getFilePair().getFile1().getId(),
                    filePairIssueCommit.getFilePair().getFile2().getId(),
                    filePairIssueCommit.getIssue().getId(),
                    filePairIssueCommit.getCommit().getId());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

}
