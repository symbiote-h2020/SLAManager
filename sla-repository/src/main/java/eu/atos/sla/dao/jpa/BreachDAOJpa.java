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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IBreachDAO;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EBreach;

@Repository("BreachRepository")
public class BreachDAOJpa extends AbstractDAOJpa<EBreach> implements IBreachDAO {

    private static Logger logger = LoggerFactory.getLogger(BreachDAOJpa.class);

    public BreachDAOJpa() {
        super(EBreach.class);
    }

    public EBreach getBreachByUUID(UUID uuid) {
        try {
            Query query = entityManager
                    .createNamedQuery(EBreach.QUERY_FIND_BY_UUID);
            query.setParameter("uuid", uuid);
            EBreach breach = null;

            breach = (EBreach) query.getSingleResult();

            return breach;

        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
            return null;
        }
    }

    public boolean update(EBreach breach) {
        entityManager.merge(breach);
        entityManager.flush();
        return true;
    }

    public List<EBreach> getByTimeRange(EAgreement contract, String variable,
            Date begin, Date end) {
        TypedQuery<EBreach> query = entityManager.createNamedQuery(
                EBreach.QUERY_FIND_BY_TIME_RANGE, EBreach.class);
        query.setParameter("uuid", contract.getAgreementId());
        query.setParameter("variable", variable);
        query.setParameter("begin", begin);
        query.setParameter("end", end);
        
    
        List<EBreach> breaches = null;
        breaches = query.getResultList();

        if (breaches != null) {
            logger.debug("Number of breaches given a contract and range of dates:" + breaches.size());
        } else {
            logger.debug("No Result found.");
        }

        return breaches;

    }

}
