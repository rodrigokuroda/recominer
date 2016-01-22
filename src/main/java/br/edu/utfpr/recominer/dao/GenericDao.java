package br.edu.utfpr.recominer.dao;

import br.edu.utfpr.recominer.util.Util;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Named
@Dependent
public class GenericDao implements Serializable {

    @Inject
    private EntityManager em;

    public GenericDao() {
    }

    public GenericDao(EntityManager em) {
        this.em = em;
    }
    
    public EntityManager getEntityManager() {
        return em;
    }

    public void insert(Object entity) {
        getEntityManager().persist(entity);
        getEntityManager().flush();
    }

    public void edit(Object entity) {
        getEntityManager().merge(entity);
        getEntityManager().flush();
    }

    public void remove(Object entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public <T> T findByID(Long id, Class classe) {
        return (T) getEntityManager().find(classe, id);
    }

    public <T> T findByID(String strId, Class classe) {
        try {
            Long lId = Util.tratarStringParaLong(strId);
            Object obj = findByID(lId, classe);
            return (T) obj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public <T> T findByID(String strId, String strClasse) {
        try {
            Class cClass = Class.forName(strClasse);
            Object obj = findByID(strId, cClass);
            return (T) obj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public <T> List<T> selectAll(Class<T> clazz) {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(clazz);
        cq.select(cq.from(clazz));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List selectBy(int[] intervalo, Class classe) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(classe));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(intervalo[1] - intervalo[0]);
        q.setFirstResult(intervalo[0]);
        return q.getResultList();
    }

    public int count(Class classe) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root rt = cq.from(classe);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public List executeNamedQueryWithParams(String namedQuery, String[] parametros, Object[] objetos) {
        return executeNamedQueryWithParams(namedQuery, parametros, objetos, false);
    }

    public List executeNamedQueryWithParams(String namedQuery, String[] parametros, Object[] objetos, boolean singleResult) {
        Query query = getEntityManager().createNamedQuery(namedQuery);
        if (singleResult) {
            query.setFirstResult(0);
        }
        if (parametros.length != objetos.length) {
            throw new IndexOutOfBoundsException("The lenght of params array is not equals lenght of objects array.");
        }
        for (int i = 0; i < parametros.length; i++) {
            String atributo = parametros[i];
            Object parametro = objetos[i];
            query.setParameter(atributo, parametro);
        }
        List list = query.getResultList();
        return list;
    }

    public List executeNamedQuery(String namedQuery) {
        List list = getEntityManager().createNamedQuery(namedQuery).getResultList();
        return list;
    }

    public List selectWithParams(String select, String[] params, Object[] objects) {
        return selectWithParams(select, params, objects, 0, 0);
    }

    public List selectWithParams(String select, String[] params, Object[] objects, int offset, int limit) {
        Query query = em.createQuery(select);
        if (params.length != objects.length) {
            throw new IndexOutOfBoundsException("The lenght of params array is not equals lenght of objects array.");
        }
        for (int i = 0; i < params.length; i++) {
            if (params[i].equals("#none#")) {
                continue;
            }
            String atributo = params[i];
            Object parametro = objects[i];
            query.setParameter(atributo, parametro);
        }
        if (offset > 0) {
            query.setFirstResult(offset);
        }
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    public <T> List<T> selectWithParams(String select, String[] params, Object[] objects, Class<T> clazz) {
        return selectWithParams(select, params, objects, 0, 0, clazz);
    }

    public <T> List<T> selectWithParams(String select, String[] params, Object[] objects, int offset, int limit, Class<T> clazz) {
        TypedQuery<T> query = em.createQuery(select, clazz);
        if (params.length != objects.length) {
            throw new IndexOutOfBoundsException("The lenght of params array is not equals lenght of objects array.");
        }
        for (int i = 0; i < params.length; i++) {
            if (params[i].equals("#none#")) {
                continue;
            }
            String atributo = params[i];
            Object parametro = objects[i];
            query.setParameter(atributo, parametro);
        }
        if (offset > 0) {
            query.setFirstResult(offset);
        }
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    public List selectNativeWithParams(String select, Object[] objects) {
        Query query = em.createNativeQuery(select);

        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                query.setParameter(i + 1, objects[i]);
            }
        }

        return query.getResultList();
    }

    public void clearCache(boolean evictAll) {
//        try {
//            if (evictAll) {
//                em.getEntityManagerFactory().getCache().evictAll();
//            }
//            em.clear();
//            System.gc();
//            System.out.println("######### CLEARED THE CACHE #########");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    public <T> T selectOneWithParams(String select, String[] params, Object[] objects) {
        List<T> result = selectWithParams(select, params, objects);
        if (result == null || result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public <T> T selectNativeOneWithParams(String select, Object[] objects) {
        List<T> result = selectNativeWithParams(select, objects);
        if (result == null || result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }
}
