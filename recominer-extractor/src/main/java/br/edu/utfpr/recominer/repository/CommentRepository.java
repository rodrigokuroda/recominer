package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.ReadOnlyJdbcRepository;
import br.edu.utfpr.recominer.model.issue.Comment;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@Repository
public class CommentRepository extends ReadOnlyJdbcRepository<Comment, Integer> {

    public CommentRepository() {
        super(ROW_MAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "comment";

    public static final RowMapper<Comment> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                Comment comment = new Comment();
                comment.setId(rs.getInt("id"));
                comment.setIssueId(rs.getInt("issue_id"));
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setText(rs.getString("text"));
                comment.setSubmittedBy(rs.getInt("submitted_by"));
                comment.setSubmittedOn(rs.getDate("submitted_on"));
                return comment;
            };

}
