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

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IServiceDAO;
import eu.atos.sla.datamodel.EService;

@Repository("ServiceRepository")
public class ServiceDAOJpa extends AbstractDAOJpa<EService> implements IServiceDAO {
    private static Logger logger = LoggerFactory.getLogger(ServiceDAOJpa.class);

    public ServiceDAOJpa() {
        super(EService.class);
    }
    
    @Override
    public EService getByUUID(String uuid) {
        try {
            Query query = entityManager
                    .createNamedQuery(EService.QUERY_FIND_BY_UUID);
            query.setParameter("uuid", uuid);
            EService service = (EService) query.getSingleResult();
            System.out.println("Service name:" + service.getName());

            return service;

        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
            return null;
        }
    }

    @Override
    public EService getByName(String name) {
        try {
            TypedQuery<EService> query = entityManager.createNamedQuery(
                    EService.QUERY_FIND_BY_NAME, EService.class);
            query.setParameter("name", name);
            EService service = new EService();

            service = (EService) query.getSingleResult();
            System.out.println("Service uuid:" + service.getUuid());

            return service;

        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
            return null;
        }
    }

    @Override
    public boolean update(EService service) {
        entityManager.merge(service);
        entityManager.flush();
        return true;
    }
}
