package br.edu.utfpr.recominer;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
@Configuration
public class DataSourceConfiguration {

//    @Value("classpath:schema-mysql.sql")
//    private Resource schemaScript;
    @Value("classpath:test_avro.sql")
    private Resource testScript;

    @Value("classpath:test_avro_issues.sql")
    private Resource testIssuesScript;

    @Value("classpath:test_avro_vcs.sql")
    private Resource testVcsScript;

    @Value("classpath:test_recominer.sql")
    private Resource testRecominerScript;

    @Value("classpath:test_schemas.sql")
    private Resource testSchemas;

    @Value("${spring.datasource.initialize:false}")
    private boolean databaseInitializationEnabled = false;

    @PostConstruct
    protected void initDatabaseForTesting() {
        if (databaseInitializationEnabled) {
            System.out.println("Initializing database...");
            DatabasePopulatorUtils.execute(databasePopulator(), dataSource());
        }
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        final DataSource datasource = DataSourceBuilder.create().build();
        return datasource;
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.batch.datasource")
    public DataSource batchDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") final DataSource dataSource) {
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
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
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
        populator.addScript(testSchemas);
        populator.addScript(testRecominerScript);
        populator.addScript(testIssuesScript);
        populator.addScript(testScript);
        populator.addScript(testVcsScript);
        return populator;
    }

}
