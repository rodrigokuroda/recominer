package br.edu.utfpr.recominer.core.repository.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.util.Assert;

/**
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 * @since 12/18/12, 10:06 PM
 */
public class TableDescription {

    private final String schema;
    private final String name;
    private final String schemaAndName;
    private final List<String> idColumns;
    private final String fromClause;

    public TableDescription(String schema, String name, String... idColumns) {
        Assert.notNull(name);
        Assert.notNull(idColumns);
        Assert.isTrue(idColumns.length > 0, "At least one primary key column must be provided");

        this.schema = schema;
        this.name = name;
        this.idColumns = Collections.unmodifiableList(Arrays.asList(idColumns));
        
        if (schema == null) {
            schemaAndName = name;
        } else {
            schemaAndName = schema + "." + name;
        }
        
        this.fromClause = schema == null ? name : schema + "." + name;
    }

    public TableDescription(String name, String idColumn) {
        this(null, name, idColumn);
    }
    
    public String getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }

    public String getSchemaAndName() {
        return schemaAndName;
    }

    public List<String> getIdColumns() {
        return idColumns;
    }

    public String getFromClause() {
        return fromClause;
    }
}
