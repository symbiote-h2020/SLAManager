/**
 * Copyright 2017 Atos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.atos.sla.dao.jpa;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import eu.atos.sla.datamodel.AbstractEntity;

public class AbstractDAOJpa<T extends AbstractEntity<T>> {
    private Class<T> class_;
    private String name;
    protected EntityManager entityManager;

    public AbstractDAOJpa(Class<T> class_) {
        this.class_ = class_;
        this.name = class_.getAnnotation(Entity.class).name();
    }

    @PersistenceContext(unitName = "slarepositoryDB")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public T getById(Long id) {
        return entityManager.find(class_, id);
    }

    public T save(T t) {

        entityManager.persist(t);
        entityManager.flush();

        return t;
    }

    public boolean delete(T t) {
        try {
            t = entityManager.getReference(class_, t.getId());
            entityManager.remove(t);
            entityManager.flush();
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    public List<T> getAll() {
        String queryString = "select o from " + name + " o";
        
        TypedQuery<T> query = this.entityManager.createQuery(queryString, class_);
        return query.getResultList();
    }
}
