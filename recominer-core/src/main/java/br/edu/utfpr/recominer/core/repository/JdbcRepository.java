package br.edu.utfpr.recominer.core.repository;

import br.edu.utfpr.recominer.core.repository.helper.MissingRowUnmapper;
import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;
import br.edu.utfpr.recominer.core.repository.helper.SqlGenerator;
import br.edu.utfpr.recominer.core.repository.helper.TableDescription;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.util.Assert;

/**
 * Implementation of {@link PagingAndSortingRepository} using
 * {@link JdbcTemplate}
 *
 * @param <T> Type of entity
 * @param <ID> Type of entity identifier
 */
public abstract class JdbcRepository<T extends Persistable<ID>, ID extends Serializable>
        extends ReadOnlyJdbcRepository<T, ID>
        implements PagingAndSortingRepository<T, ID>, InitializingBean, BeanFactoryAware {

    private final Logger log = LoggerFactory.getLogger(JdbcRepository.class);

    public static Object[] pk(Object... idValues) {
        return idValues;
    }

    private final RowUnmapper<T> rowUnmapper;

    public JdbcRepository(RowMapper<T> rowMapper, RowUnmapper<T> rowUnmapper, SqlGenerator sqlGenerator, TableDescription table) {
        super(rowMapper, sqlGenerator, table);
        Assert.notNull(rowUnmapper);

        this.rowUnmapper = rowUnmapper;
    }

    public JdbcRepository(RowMapper<T> rowMapper, RowUnmapper<T> rowUnmapper, TableDescription table) {
        this(rowMapper, rowUnmapper, null, table);
    }

    public JdbcRepository(RowMapper<T> rowMapper, RowUnmapper<T> rowUnmapper, String schema, String tableName, String idColumn) {
        this(rowMapper, rowUnmapper, null, new TableDescription(schema, tableName, idColumn));
    }

    public JdbcRepository(RowMapper<T> rowMapper, RowUnmapper<T> rowUnmapper, String tableName, String idColumn) {
        this(rowMapper, rowUnmapper, new TableDescription(null, tableName, idColumn));
    }

    public JdbcRepository(RowMapper<T> rowMapper, RowUnmapper<T> rowUnmapper, String tableName) {
        this(rowMapper, rowUnmapper, new TableDescription(tableName, "id"));
    }

    public JdbcRepository(RowMapper<T> rowMapper, TableDescription table) {
        this(rowMapper, new MissingRowUnmapper<>(), null, table);
    }

    public JdbcRepository(RowMapper<T> rowMapper, String tableName, String idColumn) {
        this(rowMapper, new MissingRowUnmapper<>(), null, new TableDescription(tableName, idColumn));
    }

    public JdbcRepository(RowMapper<T> rowMapper, String tableName) {
        this(rowMapper, new MissingRowUnmapper<>(), new TableDescription(tableName, "id"));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void delete(ID id) {
        jdbcOperations.update(sqlGenerator.deleteById(table), idToObjectArray(id));
    }

    @Override
    public void delete(T entity) {
        jdbcOperations.update(sqlGenerator.deleteById(table), idToObjectArray(entity.getId()));
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        for (T t : entities) {
            delete(t);
        }
    }

    @Override
    public void deleteAll() {
        jdbcOperations.update(sqlGenerator.deleteAll(table));
    }

    @Override
    public <S extends T> S save(S entity) {
        if (entity.isNew()) {
            return create(entity);
        } else {
            return update(entity);
        }
    }

    protected <S extends T> S update(S entity) {
        final Map<String, Object> columns = preUpdate(entity, columns(entity));
        final List<Object> idValues = removeIdColumns(columns);
        final String updateQuery = sqlGenerator.update(table, columns);
        log.info(updateQuery);
        for (int i = 0; i < table.getIdColumns().size(); ++i) {
            columns.put(table.getIdColumns().get(i), idValues.get(i));
        }
        final Object[] queryParams = columns.values().toArray();
        jdbcOperations.update(updateQuery, queryParams);
        return postUpdate(entity);
    }

    protected Map<String, Object> preUpdate(T entity, Map<String, Object> columns) {
        return columns;
    }

    protected <S extends T> S create(S entity) {
        final Map<String, Object> columns = preCreate(columns(entity), entity);
        if (entity.getId() == null) {
            return createWithAutoGeneratedKey(entity, columns);
        } else {
            return createWithManuallyAssignedKey(entity, columns);
        }
    }

    private <S extends T> S createWithManuallyAssignedKey(S entity, Map<String, Object> columns) {
        final String createQuery = sqlGenerator.create(table, columns);
        log.info(createQuery);
        final Object[] queryParams = columns.values().toArray();
        jdbcOperations.update(createQuery, queryParams);
        return postCreate(entity, null);
    }

    private <S extends T> S createWithAutoGeneratedKey(S entity, Map<String, Object> columns) {
        removeIdColumns(columns);
        final String createQuery = sqlGenerator.create(table, columns);
        log.info(createQuery);
        final Object[] queryParams = columns.values().toArray();
        final GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbcOperations.update((Connection con) -> {
            final String idColumnName = table.getIdColumns().get(0);
            final PreparedStatement ps = con.prepareStatement(createQuery, new String[]{idColumnName});
            for (int i = 0; i < queryParams.length; ++i) {
                ps.setObject(i + 1, queryParams[i]);
            }
            return ps;
        }, key);
        return postCreate(entity, key.getKey());
    }

    private List<Object> removeIdColumns(Map<String, Object> columns) {
        List<Object> idColumnsValues = new ArrayList<>(columns.size());
        for (String idColumn : table.getIdColumns()) {
            idColumnsValues.add(columns.remove(idColumn));
        }
        return idColumnsValues;
    }

    protected Map<String, Object> preCreate(Map<String, Object> columns, T entity) {
        return columns;
    }

    private LinkedHashMap<String, Object> columns(T entity) {
        return new LinkedHashMap<>(rowUnmapper.mapColumns(entity));
    }

    protected <S extends T> S postUpdate(S entity) {
        return entity;
    }

    /**
     * General purpose hook method that is called every time {@link #create} is
     * called with a new entity.
     * <p/>
     * OVerride this method e.g. if you want to fetch auto-generated key from
     * database
     *
     *
     * @param entity Entity that was passed to {@link #create}
     * @param generatedId ID generated during INSERT or NULL if not
     * available/not generated. todo: Type should be ID, not Number
     * @return Either the same object as an argument or completely different one
     */
    protected <S extends T> S postCreate(S entity, Number generatedId) {
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        List<S> ret = new ArrayList<>();
        for (S s : entities) {
            ret.add(save(s));
        }
        return ret;
    }

}
