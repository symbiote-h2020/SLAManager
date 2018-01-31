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
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IPenaltyDAO;
import eu.atos.sla.datamodel.EPenalty;
import eu.atos.sla.datamodel.EPenaltyDefinition;

@Repository("PenaltyRepository")
public class PenaltyDAOJpa extends AbstractDAOJpa<EPenalty> implements IPenaltyDAO {

    private static Logger logger = LoggerFactory.getLogger(PenaltyDAOJpa.class);

    public PenaltyDAOJpa() {
        super(EPenalty.class);
    }
    
    @Override
    public EPenalty getByUuid(String uuid) {
        try {
            Query query = entityManager.createNamedQuery(EPenalty.Query.FIND_BY_UUID);
            query.setParameter("uuid", uuid);
            EPenalty penalty = null;

            penalty = (EPenalty) query.getSingleResult();

            return penalty;

        } catch (NoResultException e) {
            logger.debug("No Result searching " + uuid + ":" + e);
            return null;
        }
    }

    @Override
    public List<EPenalty> getByAgreement(String agreementId) {
        SearchParameters params = new SearchParameters();
        params.setAgreementId(agreementId);
        
        return search(params);
    }

    @Override
    public List<EPenalty> search(SearchParameters params) {
        TypedQuery<EPenalty> query = entityManager.createNamedQuery(
                EPenalty.Query.SEARCH,
                EPenalty.class);

        query.setParameter("agreementId", params.getAgreementId());
        query.setParameter("termName", params.getGuaranteeTermName());
        query.setParameter("begin", params.getBegin());
        query.setParameter("end", params.getEnd());
        
        List<EPenalty> penalties = query.getResultList();
        return penalties;
    }

    @Override
    public Date getLastPenaltyDate(String agreementId, EPenaltyDefinition def, String kpiName) {
        try {
            TypedQuery<Date> query = entityManager.createNamedQuery(EPenalty.Query.FIND_LAST_PENALTY, Date.class);

            query.setParameter("agreementId", agreementId);
            query.setParameter("count", def.getCount());
            query.setParameter("interval", def.getTimeInterval());
            query.setParameter("kpiName", kpiName);
            
            Date penalty = query.getSingleResult();

            return penalty;

        } catch (NoResultException e) {
            return null;
        }
    }
}
