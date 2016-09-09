package br.edu.utfpr.recominer.repository;

import br.edu.utfpr.recominer.core.repository.JdbcRepository;
import br.edu.utfpr.recominer.model.Configuration;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@Repository
public class ConfigurationRepository extends JdbcRepository<Configuration, Integer> {
    
    public ConfigurationRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, SCHEMA_NAME, TABLE_NAME, ID_COLUMN);
    }
    
    private static final String SCHEMA_NAME = "recominer";
    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "configuration";

    public static final RowMapper<Configuration> ROW_MAPPER
            = (ResultSet rs, int rowNum) -> {
                Configuration config = new Configuration();
                config.setId(rs.getInt("id"));
                config.setKey(rs.getString("key"));
                config.setValue(rs.getString("value"));
                
                return config;
            };

    private static final RowUnmapper<Configuration> ROW_UNMAPPER
            = (Configuration config) -> {
                Map<String, Object> mapping = new LinkedHashMap<>();
                mapping.put("id", config.getId());
                mapping.put("key", config.getKey());
                mapping.put("value", config.getValue());

                return mapping;
            };
}
