package org.tiwpr.szymie.daos;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

public class BaseDao {

    @PersistenceContext
    protected EntityManager entityManager;

    public long countAll() {
        return countAll(this.getClass());
    }

    protected <T> long countAll(Class<T> clazz) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(clazz)));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }
}
