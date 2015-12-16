package br.edu.utfpr.recominer.dao;

import br.edu.utfpr.recominer.model.InterfaceEntity;
import br.edu.utfpr.recominer.util.Util;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Named
public class GenericBichoDAO {

    @Inject
    private EntityManager em;

    public GenericBichoDAO() {
    }

    public GenericBichoDAO(final EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public void insert(InterfaceEntity entity) {
        getEntityManager().persist(entity);
        getEntityManager().flush();
    }

    public void edit(InterfaceEntity entity) {
        getEntityManager().merge(entity);
        getEntityManager().flush();
    }

    public void remove(InterfaceEntity entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public <T> T findByID(Long id, Class<T> classe) {
        return getEntityManager().find(classe, id);
    }

    public <T> T findByID(String strId, Class<T> classe) {
        try {
            Long lId = Util.tratarStringParaLong(strId);
            T obj = findByID(lId, classe);
            return obj;
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

    public <T> List<T> selectBy(int[] intervalo, Class<T> clazz) {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(clazz);
        cq.select(cq.from(clazz));
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        q.setMaxResults(intervalo[1] - intervalo[0]);
        q.setFirstResult(intervalo[0]);
        return q.getResultList();
    }

    public <T> int count(Class<T> clazz) {
        CriteriaQuery<Long> cq = getEntityManager().getCriteriaBuilder().createQuery(Long.class);
        Root<T> rt = cq.from(clazz);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult().intValue();
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

    public <T> List<T> executeNamedQuery(String namedQuery, Class<T> clazz) {
        List<T> list = getEntityManager().createNamedQuery(namedQuery, clazz).getResultList();
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
