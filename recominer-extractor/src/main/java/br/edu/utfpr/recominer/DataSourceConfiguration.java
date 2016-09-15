package br.edu.utfpr.recominer;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at gmail.com>
 */
@Configuration
public class DataSourceConfiguration {

//    @Value("classpath:schema-mysql.sql")
//    private Resource schemaScript;
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource mysqlDataSource() throws SQLException {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.batch.datasource")
    public DataSource batchDataSource() throws SQLException {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public JdbcTemplate mysqlJdbcTemplate(@Qualifier("mysqlDataSource") final DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

//    @Bean
//    @Primary
//    public DataSource hsqldbDataSource() throws SQLException {
//        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//        dataSource.setDriver(new org.hsqldb.jdbcDriver());
//        dataSource.setUrl("jdbc:hsqldb:mem:batch");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("");
//        return dataSource;
//    }
//    @Bean
//    public JdbcTemplate hsqldbJdbcTemplate(@Qualifier("hsqldbDataSource") DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }
    @Bean
    public JdbcTemplate batchJdbcTemplate(@Qualifier("batchDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Primary
    public PlatformTransactionManager mysqlTransactionManager(@Qualifier("mysqlDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

//    @Bean
//    public PlatformTransactionManager hsqldbTransactionManager(@Qualifier("hsqldbDataSource") DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
    @Bean
    public PlatformTransactionManager batchTransactionManager(@Qualifier("batchDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
//        populator.addScript(schemaScript);
        return populator;
    }

    @Bean
    @Profile("test")
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:test-data.sql")
                .build();
    }
}
