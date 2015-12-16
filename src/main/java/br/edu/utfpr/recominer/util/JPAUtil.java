package br.edu.utfpr.recominer.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class JPAUtil {

    @Produces
    @ApplicationScoped
    @Database(Database.DatabaseType.POSTGRESQL)
    public EntityManagerFactory getEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("pu");
    }

    @Produces
    @RequestScoped
    @Database(Database.DatabaseType.POSTGRESQL)
    public EntityManager getEntityManager(@Database(Database.DatabaseType.POSTGRESQL) EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    @Produces
    @ApplicationScoped
    @Database(Database.DatabaseType.MYSQL_BICHO)
    public EntityManagerFactory getBichoEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("mysql");
    }

    @Produces
    @RequestScoped
    @Database(Database.DatabaseType.MYSQL_BICHO)
    public EntityManager getBichoEntityManager(@Database(Database.DatabaseType.MYSQL_BICHO) EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    public void closeMysqlBichoEntityManager(@Disposes @Any EntityManager em) {
        em.close();
    }
}
