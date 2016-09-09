package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.ReadOnlyJdbcRepository;
import br.edu.utfpr.recominer.core.model.Committer;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@Repository
public class CommitterRepository extends ReadOnlyJdbcRepository<Committer, Integer> {

    public CommitterRepository() {
        super(ROW_MAPPER, TABLE_NAME, ID_COLUMN);
    }

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "people";

    public static final RowMapper<Committer> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                Committer committer = new Committer(rs.getInt("id"), 
                        rs.getString("name"),
                        rs.getString("email"));
                return committer;
            };

}
