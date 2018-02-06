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

import eu.atos.sla.datamodel.EAgreement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IViolationDAO;
import eu.atos.sla.datamodel.EViolation;

@Repository("ViolationRepository")
public class ViolationDAOJpa extends AbstractDAOJpa<EViolation> implements IViolationDAO {
    private static Logger logger = LoggerFactory.getLogger(ViolationDAOJpa.class);

    public ViolationDAOJpa() {
        super(EViolation.class);
    }
    
    public EViolation getViolationByUUID(String uuid) {
        try {
            Query query = entityManager
                    .createNamedQuery(EViolation.QUERY_FIND_BY_UUID);
            query.setParameter("uuid", uuid);
            EViolation violation = null;

            violation = (EViolation) query.getSingleResult();

            return violation;

        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
            return null;
        }

    }

    public List<EViolation> getByAgreement(String agreementId, String termName) {
        List<EViolation> violations = null;
        if (agreementId != null) {
            TypedQuery<EViolation> query = entityManager.createNamedQuery(
                    EViolation.QUERY_FIND_VIOLATIONS_BY_AGREEMENT,
                    EViolation.class);
            query.setParameter("agreementId", agreementId);
            query.setParameter("termName", termName);

            violations = null;

            violations = (List<EViolation>) query.getResultList();

        }

        if (violations != null) {
            logger.debug("Number of violations by AgreementId " + agreementId + " : " + violations.size());
        } else {
            logger.debug("No Result found.");
        }
        return violations;
    }

    public List<EViolation> getByAgreementIdInARangeOfDates(String agreementId,
            String termName, Date begin, Date end) {
        List<EViolation> violations = null;
        if (agreementId != null) {
                TypedQuery<EViolation> query = entityManager
                        .createNamedQuery(
                                EViolation.QUERY_FIND_VIOLATIONS_BY_AGREEMENT_IN_A_RANGE_OF_DATES,
                                EViolation.class);
                query.setParameter("agreementId", agreementId);
                query.setParameter("termName", termName);
                query.setParameter("begin", begin);
                query.setParameter("end", end);

                violations = null;

                violations = (List<EViolation>) query.getResultList();
        }

        if (violations != null) {
            logger.debug("Number of violations between " + begin + " and "
                    + end + ":" + violations.size());
        } else {
            logger.debug("No Result found.");
        }
        return violations;
    }

    @Override
    public List<EViolation> getByProvider(String providerUuid) {

        List<EViolation> violations = null;

        if (providerUuid != null) {
            TypedQuery<EViolation> query = entityManager.createNamedQuery(
                    EViolation.QUERY_FIND_VIOLATIONS_BY_PROVIDER,
                    EViolation.class);
            query.setParameter("providerUuid", providerUuid);

            violations = null;

            violations = (List<EViolation>) query.getResultList();

        }
        if (violations != null) {
            logger.debug("Number of violations of the providerUuuid "
                    + providerUuid + " :" + violations.size());
        } else {
            logger.debug("No Result found.");
        }

        return violations;
    }

    @Override
    public List<EViolation> getByProviderInaRangeOfDates(String providerUuid,
            Date begin, Date end) {

        List<EViolation> violations = null;
        if (providerUuid != null) {
                TypedQuery<EViolation> query = entityManager
                        .createNamedQuery(
                                EViolation.QUERY_FIND_VIOLATIONS_BY_PROVIDER_IN_A_RANGE_OF_DATES,
                                EViolation.class);
                query.setParameter("providerUuid", providerUuid);
                query.setParameter("begin", begin);
                query.setParameter("end", end);

                violations = null;

                violations = (List<EViolation>) query.getResultList();

        }

        if (violations != null) {
            logger.debug("Number of violations between " + begin + " and "
                    + end + ":" + violations.size());
        } else {
            logger.debug("No Result found.");
        }

        return violations;
    }

    @Override
    public List<EViolation> search(SearchParameters params) {

        TypedQuery<EViolation> query = entityManager.createNamedQuery(
                        EViolation.QUERY_SEARCH,
                        EViolation.class);
        
        query.setParameter("providerUuid", params.getProviderUuid());
        query.setParameter("agreementId", params.getAgreementId());
        query.setParameter("termName", params.getGuaranteeTermName());
        query.setParameter("begin", params.getBegin());
        query.setParameter("end", params.getEnd());

        List<EViolation> violations = query.getResultList();
        return violations;
    }

    @Override
    public Date getLastViolationDate(EAgreement agreement, String kpiName) {
        Date violationDate = null;

        if (agreement == null) {
            throw new NullPointerException("agreement cannot be null");
        }
        TypedQuery<Date> query = entityManager.createNamedQuery(
                EViolation.QUERY_FIND_LAST_VIOLATION_DATE,
                Date.class);
        query.setParameter("agreementId", agreement.getAgreementId());
        query.setParameter("kpiName", kpiName);

        violationDate = query.getSingleResult();
        return violationDate;
    }

}
