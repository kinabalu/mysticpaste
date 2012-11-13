package com.mysticcoders.mysticpaste.persistence.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * AbstractDaoHibernate
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class AbstractDaoHibernate<T> {

    private Class entityClass;

    @PersistenceContext
    EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    protected AbstractDaoHibernate(Class dataClass) {
        super();
        this.entityClass = dataClass;
    }

    @SuppressWarnings("unchecked")
    public T load(Long id) {
        return (T) em.getReference(entityClass, id);
    }

    @SuppressWarnings("unchecked")
    public T load(String id) {
        return (T) em.getReference(entityClass, id);
    }

    @SuppressWarnings("unchecked")
    public T loadChecked(Long id)
            throws EntityNotFoundException {
        T persistedObject = load(id);

        if (persistedObject == null) {
            throw new EntityNotFoundException(entityClass, id);
        }

        return persistedObject;
    }

    public void merge(T detachedObject) {
        em.merge(detachedObject);
    }

    public void refresh(T detachedObject) {
        em.refresh(detachedObject);
    }

    public void save(T persistedObject) {
        em.persist(persistedObject);
    }

    public void delete(T persistedObject) {
        em.remove(persistedObject);
    }

    public void delete(Long id) {
        delete(loadChecked(id));
    }

}