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

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.datamodel.EAgreement;

@Repository("AgreementRepository")
public class AgreementDAOJpa extends AbstractDAOJpa<EAgreement> implements IAgreementDAO {
    private static Logger logger = LoggerFactory.getLogger(AgreementDAOJpa.class);

    public AgreementDAOJpa() {
        super(EAgreement.class);
    }
    
    public EAgreement getByAgreementId(String agreementId) {
        try {
            Query query = entityManager.createNamedQuery(EAgreement.QUERY_FIND_BY_AGREEMENT_ID);
            query.setParameter("agreementId", agreementId);
            EAgreement agreement = null;

            agreement = (EAgreement) query.getSingleResult();

            return agreement;

        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
            return null;
        }
    }

    
    public List<EAgreement> getByConsumer(String consumerId) {
        TypedQuery<EAgreement> query = entityManager.createNamedQuery(
                EAgreement.QUERY_FIND_BY_CONSUMER, EAgreement.class);
        query.setParameter("consumerId", consumerId);
        List<EAgreement> agreements = query.getResultList();

        if (agreements != null) {
            logger.debug("Number of agreements:" + agreements.size());
        } else {
            logger.debug("No Result found.");
        }

        return agreements;
    }
    
    public List<EAgreement> getByActiveAgreements(long date) {

        TypedQuery<EAgreement> query = entityManager.createNamedQuery(
                EAgreement.QUERY_ACTIVE_AGREEMENTS, EAgreement.class);
        Date actualDate = new Date(date);
        query.setParameter("actualDate", actualDate);
        List<EAgreement> agreements = query.getResultList();

        if (agreements != null) {
            logger.debug("Number of active agreements:" + agreements.size());
        } else {
            logger.debug("No Result found.");
        }

        return agreements;
    }

    public List<EAgreement> getByTemplate(String templateUUID) {

        TypedQuery<EAgreement> query = entityManager.createNamedQuery(
                EAgreement.QUERY_FIND_BY_TEMPLATEUUID, EAgreement.class);
        query.setParameter("templateUUID", templateUUID);
        List<EAgreement> agreements = query.getResultList();

        if (agreements != null) {
            logger.debug("Number of agreement per template "+templateUUID+" :" + agreements.size());
        } else {
            logger.debug("No Result found.");
        }

        return agreements;
    }

    public List<EAgreement> search(String consumerId, String providerId, String templateId, Boolean active) {

        TypedQuery<EAgreement> query = entityManager.createNamedQuery(EAgreement.QUERY_SEARCH, EAgreement.class);
        query.setParameter("consumerId", consumerId);
        query.setParameter("providerId", providerId);
        query.setParameter("templateId", templateId);
        query.setParameter("active", active);
        List<EAgreement> agreements = query.getResultList();
        
        return agreements;
    }

    public List<EAgreement> getByProvider(String providerUuid) {

        try {
            TypedQuery<EAgreement> query = entityManager.createNamedQuery(EAgreement.QUERY_FIND_BY_PROVIDER, EAgreement.class);
            query.setParameter("providerUuid", providerUuid);
            List<EAgreement> agreements = query.getResultList();
            return agreements;
        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
            return null;
        }
    }

    @Override
    public List<EAgreement> searchPerTemplateAndConsumer(String consumerId, String templateUUID) {
        try {
            TypedQuery<EAgreement> query = entityManager.createNamedQuery(EAgreement.QUERY_FIND_BY_TEMPLATEUUID_AND_CONSUMER, EAgreement.class);
            query.setParameter("consumerId", consumerId);
            query.setParameter("templateUUID", templateUUID);
            List<EAgreement> agreements = query.getResultList();
            return agreements;
        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
            return null;
        }
    }

}
