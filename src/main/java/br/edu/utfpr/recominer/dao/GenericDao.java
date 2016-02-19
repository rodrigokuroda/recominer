package br.edu.utfpr.recominer.dao;

import br.edu.utfpr.recominer.util.Util;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
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
//        getEntityManager().flush();
    }

    public void remove(Object entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public <T> T findByID(Long id, Class clazz) {
        return (T) getEntityManager().find(clazz, id);
    }

    public <T> T findByID(String strId, Class clazz) {
        Long lId = Util.tratarStringParaLong(strId);
        Object obj = findByID(lId, clazz);
        return (T) obj;
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

    public int count(Class clazz) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root rt = cq.from(clazz);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public <T> List<T> executeNamedQuery(String namedQuery, Class<T> clazz) {
        return executeNamedQueryWithParams(namedQuery, new String[0], new Object[0], clazz);
    }
    
    public <T> List<T> executeNamedQueryWithParams(String namedQuery, String[] parametros, Object[] objetos, Class<T> clazz) {
        return executeNamedQueryWithParams(namedQuery, parametros, objetos, clazz, false);
    }

    public <T> List<T> executeNamedQueryWithParams(String namedQuery, String[] parametros, Object[] objetos, Class<T> clazz, boolean singleResult) {
        TypedQuery<T> query = getEntityManager().createNamedQuery(namedQuery, clazz);
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
        List<T> list = query.getResultList();
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

    public List selectNativeWithParams(String select, Map<String, Object> params) {
        Query query = em.createNativeQuery(select);

        if (params != null) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                String name = param.getKey();
                Object value = param.getValue();
                query.setParameter(name, value);
            }
        }

        return query.getResultList();
    }

    public <T> T selectOneWithParams(String select, String[] params, Object[] objects) {
        List<T> result = selectWithParams(select, params, objects);
        if (result == null || result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public <T> T selectNativeOneWithParams(String select, Object... objects) {
        List<T> result = selectNativeWithParams(select, objects);
        if (result == null || result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public <T> T selectNativeOneWithParams(String select, Map<String, Object> params) {
        List<T> result = selectNativeWithParams(select, params);
        if (result == null || result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public int executeNativeQuery(String sql, Object[] params) {
        final Query nativeQuery = em.createNativeQuery(sql);
        for (int position = 1; position <= params.length; position++) {
            nativeQuery.setParameter(position, params[position - 1]);
        }
        return nativeQuery.executeUpdate();
    }
    
}
