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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.datamodel.EProvider;

@Repository("ProviderRepository")
public class ProviderDAOJpa extends AbstractDAOJpa<EProvider> implements IProviderDAO {
    private static Logger logger = LoggerFactory.getLogger(ProviderDAOJpa.class);

    public ProviderDAOJpa() {
        super(EProvider.class);
    }
    
    @Override
    public EProvider getByUUID(String uuid) {
        try {
            Query query = entityManager
                    .createNamedQuery(EProvider.QUERY_FIND_BY_UUID);
            query.setParameter("uuid", uuid);
            EProvider provider = null;

            provider = (EProvider) query.getSingleResult();
            System.out.println("Provider name:" + provider.getName());

            return provider;

        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
            return null;
        }
    }

    @Override
    public EProvider getByName(String name) {
        try {
            Query query = entityManager
                    .createNamedQuery(EProvider.QUERY_FIND_BY_NAME);
            query.setParameter("name", name);
            EProvider provider = null;

            provider = (EProvider) query.getSingleResult();
            System.out.println("Provider uuid:" + provider.getUuid());

            return provider;

        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
            return null;
        }
    }

    @Override
    public EProvider getLastProvider() {
        Query query = entityManager
                .createQuery("SELECT p FROM Provider p order by id DESC");
        query.setMaxResults(1);
        return (EProvider) query.getSingleResult();
    }

    @Override
    public boolean update(EProvider provider) {
        entityManager.merge(provider);
        entityManager.flush();
        return true;
    }
}
