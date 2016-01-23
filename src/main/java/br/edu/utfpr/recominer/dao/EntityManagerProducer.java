package br.edu.utfpr.recominer.dao;

import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Produces a EntityManager to able inject into CDI Beans.
 *
 * @author Rodrigo T. Kuroda
 */
@Named
@ApplicationScoped
public class EntityManagerProducer {

    @PersistenceUnit(unitName = "mysql")
    @Produces
    @Mysql
    private EntityManagerFactory mysqlFactory;
    
    @PersistenceUnit(unitName = "postgresql")
    @Produces
    @Default
    @Postgresql
    private EntityManagerFactory postgresqlFactory;

    @Produces
    @Mysql
    public EntityManager createMysqlEntityManager() {
        return mysqlFactory.createEntityManager();
    }
    
    @Produces
    @Postgresql
    @Default
    public EntityManager createPostgresqlEntityManager() {
        return postgresqlFactory.createEntityManager();
    }

//    @Produces
//    @Mysql
//    public EntityManagerFactory createMysqlEntityManagerFactory(final InjectionPoint injectionPoint) {
//        final Mysql annotation = injectionPoint.getAnnotated().getAnnotation(Mysql.class);
//        final String schema = annotation.schema();
//        
//        final Properties properties = new Properties();
//        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, schema);
//        return Persistence.createEntityManagerFactory("mysql", properties);
//    }
    
    public EntityManagerFactory createMysqlEntityManagerFactory(final String schema) {
        final Properties properties = new Properties();
        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, schema);
        return Persistence.createEntityManagerFactory("mysql", properties);
    }
    
    public EntityManager createMysqlEntityManager(final String schema) {
        final Properties properties = new Properties();
        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, schema);
        return Persistence.createEntityManagerFactory("mysql")
                .createEntityManager(properties);
    }
//
//    public void closePostgresqlEntityManager(@Disposes @Postgresql final EntityManager manager) {
//        if (manager.isOpen()) {
//            manager.flush();
//            manager.close();
//        }
//    }
//
//    public void closeMysqlEntityManager(@Disposes @Mysql final EntityManager manager) {
//        if (manager.isOpen()) {
//            manager.flush();
//            manager.close();
//        }
//    }

}
