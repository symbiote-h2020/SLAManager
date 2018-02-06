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
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IServicePropertiesDAO;
import eu.atos.sla.datamodel.EServiceProperties;

@Repository("ServicePropertiesRepository")
public class ServicePropertiesDAOJpa extends AbstractDAOJpa<EServiceProperties> implements IServicePropertiesDAO {
    private static Logger logger = LoggerFactory.getLogger(ServicePropertiesDAOJpa.class);

    public ServicePropertiesDAOJpa() {
        super(EServiceProperties.class);
    }
    
    @Override
    public EServiceProperties getByName(String name) {
        try {
            TypedQuery<EServiceProperties> query = entityManager.createNamedQuery(
                    EServiceProperties.QUERY_FIND_BY_NAME, EServiceProperties.class);
            query.setParameter("name", name);
            EServiceProperties serviceProperties = query.getSingleResult();
            

            return serviceProperties;

        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
            return null;
        }
    }

    @Override
    public boolean update(EServiceProperties serviceProperties) {
        entityManager.merge(serviceProperties);
        entityManager.flush();
        return true;
    }
}
