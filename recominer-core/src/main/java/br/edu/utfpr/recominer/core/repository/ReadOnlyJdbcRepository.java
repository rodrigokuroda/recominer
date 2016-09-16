package br.edu.utfpr.recominer.core.repository;

import br.edu.utfpr.recominer.core.model.Project;
import br.edu.utfpr.recominer.core.repository.helper.SqlGenerator;
import br.edu.utfpr.recominer.core.repository.helper.TableDescription;
import br.edu.utfpr.recominer.core.util.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

/**
 * Implementation of {@link PagingAndSortingRepository} using
 * {@link JdbcTemplate}
 *
 * @param <T> Type of entity
 * @param <ID> Type of entity identifier
 */
public abstract class ReadOnlyJdbcRepository<T extends Persistable<ID>, ID extends Serializable>
        implements InitializingBean, BeanFactoryAware, ReadOnlyRepository<T, ID> {

    private final Logger log = LoggerFactory.getLogger(ReadOnlyJdbcRepository.class);

    public static Object[] pk(Object... idValues) {
        return idValues;
    }

    protected Project project;
    protected TableDescription table;

    protected final RowMapper<T> rowMapper;

    protected SqlGenerator sqlGenerator = new SqlGenerator();
    protected BeanFactory beanFactory;
    protected JdbcOperations jdbcOperations;

    public ReadOnlyJdbcRepository(RowMapper<T> rowMapper, SqlGenerator sqlGenerator, TableDescription table) {
        Assert.notNull(rowMapper);
        Assert.notNull(table);

        this.rowMapper = rowMapper;
        this.sqlGenerator = sqlGenerator;
        this.table = table;
    }

    public ReadOnlyJdbcRepository(RowMapper<T> rowMapper, TableDescription table) {
        this(rowMapper, null, table);
    }

    public ReadOnlyJdbcRepository(RowMapper<T> rowMapper, String schema, String tableName, String idColumn) {
        this(rowMapper, null, new TableDescription(schema, tableName, idColumn));
    }

    public ReadOnlyJdbcRepository(RowMapper<T> rowMapper, String schema, String tableName) {
        this(rowMapper, new TableDescription(schema, tableName, "id"));
    }

    public ReadOnlyJdbcRepository(RowMapper<T> rowMapper, String tableName) {
        this(rowMapper, new TableDescription(tableName, "id"));
    }
    
    public void setProject(Project project) {
        this.project = project;
        setTable(new TableDescription(project.getSchemaPrefix(), table.getName(), table.getIdColumns().toArray(new String[1])));
    }
    
    protected String getQueryForSchema(String query) {
        return QueryUtils.getQueryForDatabase(query, project, table.getName());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        obtainJdbcTemplate();
        if (sqlGenerator == null) {
            obtainSqlGenerator();
        }
    }

    public void setSqlGenerator(SqlGenerator sqlGenerator) {
        this.sqlGenerator = sqlGenerator;
    }

    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcOperations = new JdbcTemplate(dataSource);
    }

    protected TableDescription getTable() {
        return table;
    }

    protected void setTable(TableDescription table) {
        this.table = table;
    }

    private void obtainSqlGenerator() {
        try {
            sqlGenerator = beanFactory.getBean(SqlGenerator.class);
        } catch (NoSuchBeanDefinitionException e) {
            sqlGenerator = new SqlGenerator();
        }
    }

    private void obtainJdbcTemplate() {
        try {
            jdbcOperations = beanFactory.getBean(JdbcOperations.class);
        } catch (NoSuchBeanDefinitionException e) {
            final DataSource dataSource = beanFactory.getBean(DataSource.class);
            jdbcOperations = new JdbcTemplate(dataSource);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public long count() {
        return jdbcOperations.queryForObject(sqlGenerator.count(table), Long.class);
    }

    @Override
    public boolean exists(ID id) {
        return jdbcOperations.queryForObject(sqlGenerator.countById(table), Integer.class, idToObjectArray(id)) > 0;
    }

    @Override
    public List<T> findAll() {
        return jdbcOperations.query(sqlGenerator.selectAll(table), rowMapper);
    }

    @Override
    public T findOne(ID id) {
        final Object[] idColumns = idToObjectArray(id);
        final List<T> entityOrEmpty = jdbcOperations.query(sqlGenerator.selectById(table), idColumns, rowMapper);
        return entityOrEmpty.isEmpty() ? null : entityOrEmpty.get(0);
    }

    protected static <ID> Object[] idToObjectArray(ID id) {
        if (id instanceof Object[]) {
            return (Object[]) id;
        } else {
            return new Object[]{id};
        }
    }

    private static <ID> List<Object> idToObjectList(ID id) {
        if (id instanceof Object[]) {
            return Arrays.asList((Object[]) id);
        } else {
            return Collections.<Object>singletonList(id);
        }
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids) {
        final List<ID> idsList = toList(ids);
        if (idsList.isEmpty()) {
            return Collections.emptyList();
        }
        final Object[] idColumnValues = flatten(idsList);
        return jdbcOperations.query(sqlGenerator.selectByIds(table, idsList.size()), rowMapper, idColumnValues);
    }

    private static <T> List<T> toList(Iterable<T> iterable) {
        final List<T> result = new ArrayList<>();
        for (T item : iterable) {
            result.add(item);
        }
        return result;
    }

    private static <ID> Object[] flatten(List<ID> ids) {
        final List<Object> result = new ArrayList<>();
        for (ID id : ids) {
            result.addAll(idToObjectList(id));
        }
        return result.toArray();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return jdbcOperations.query(sqlGenerator.selectAll(table, sort), rowMapper);
    }

    @Override
    public Page<T> findAll(Pageable page) {
        String query = sqlGenerator.selectAll(table, page);
        return new PageImpl<>(jdbcOperations.query(query, rowMapper), page, count());
    }

}
