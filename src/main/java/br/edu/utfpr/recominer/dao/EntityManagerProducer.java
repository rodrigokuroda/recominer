package br.edu.utfpr.recominer.dao;

import java.util.Properties;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
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
public class EntityManagerProducer {

    @PersistenceUnit(unitName = "mysql")
    private EntityManagerFactory factory;

    @RequestScoped
    @Produces
    public EntityManager createEntityManager() {
        return factory.createEntityManager();
    }

    public EntityManager createEntityManager(String schema) {
        Properties properties = new Properties();
        properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, schema);
        return Persistence.createEntityManagerFactory("mysql")
                .createEntityManager(properties);
    }

    public void closeEntityManager(@Disposes EntityManager manager) {
        if (manager.isOpen()) {
            manager.close();
        }
    }

}
