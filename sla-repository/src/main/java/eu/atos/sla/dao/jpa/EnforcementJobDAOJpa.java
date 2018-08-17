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

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IEnforcementJobDAO;
import eu.atos.sla.datamodel.EEnforcementJob;


@Repository("EnforcementJobService")
public class EnforcementJobDAOJpa extends AbstractDAOJpa<EEnforcementJob> implements IEnforcementJobDAO {
    private static Logger logger = LoggerFactory.getLogger(EnforcementJobDAOJpa.class);

    public EnforcementJobDAOJpa() {
        super(EEnforcementJob.class);
    }
    
    @Override
    public List<EEnforcementJob> getNotExecuted(Date since) {

        TypedQuery<EEnforcementJob> query = entityManager.createNamedQuery(EEnforcementJob.QUERY_FIND_NOT_EXECUTED, EEnforcementJob.class);
        query.setParameter("since", since);
        List<EEnforcementJob> list = query.getResultList();

        if (list != null) {
            logger.debug("Number of enforcements:" + list.size());
        } else {
            logger.debug("No Result found.");
        }

        return list;

    }

    @Override
    public EEnforcementJob getByAgreementId(String agreementId) {

        TypedQuery<EEnforcementJob> query = entityManager.createNamedQuery(EEnforcementJob.QUERY_FIND_BY_AGREEMENT_ID, EEnforcementJob.class);
        query.setParameter("agreementId", agreementId);

        EEnforcementJob result;
        try {
            result = query.getSingleResult();

        } catch (NoResultException e) {
            logger.debug("Null will returned due to no Result found: " + e);
            return null;
        }

        return result;
    }

}
