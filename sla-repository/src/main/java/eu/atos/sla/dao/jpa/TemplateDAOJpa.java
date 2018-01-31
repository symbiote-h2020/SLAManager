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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.ETemplate;

@Repository("TemplateRepository")
public class TemplateDAOJpa extends AbstractDAOJpa<ETemplate> implements ITemplateDAO {
    private static Logger logger = LoggerFactory.getLogger(TemplateDAOJpa.class);

    public TemplateDAOJpa() {
        super(ETemplate.class);
    }
    
    public ETemplate getByUuid(String uuid) {
        try {
            Query query = entityManager
                    .createNamedQuery(ETemplate.QUERY_FIND_BY_UUID);
            query.setParameter("uuid", uuid);
            ETemplate template = null;
            template = (ETemplate) query.getSingleResult();
            return template;
        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
            return null;
        }
    }


    public List<ETemplate> search(String providerId, String []serviceIds) {
        TypedQuery<ETemplate> query = entityManager.createNamedQuery(
                ETemplate.QUERY_SEARCH, ETemplate.class);
        query.setParameter("providerId", providerId);
        query.setParameter("serviceIds", (serviceIds!=null)?Arrays.asList(serviceIds):null);
        query.setParameter("flagServiceIds", (serviceIds!=null)?"flag":null);
        logger.debug("providerId:{} - serviceIds:{}" , providerId, (serviceIds!=null)?Arrays.asList(serviceIds):null);
        List<ETemplate> templates = new ArrayList<ETemplate>();
        templates = (List<ETemplate>) query.getResultList();

        if (templates != null) {
            logger.debug("Number of templates:" + templates.size());
        } else {
            logger.debug("No Result found.");
        }

        return templates;
    }
    
    public List<ETemplate> getByAgreement(String agreement) {

        TypedQuery<ETemplate> query = entityManager.createNamedQuery(
                ETemplate.QUERY_FIND_BY_AGREEMENT, ETemplate.class);
        query.setParameter("agreement", agreement);
        List<ETemplate> templates = new ArrayList<ETemplate>();
        templates = (List<ETemplate>) query.getResultList();

        if (templates != null) {
            logger.debug("Number of templates:" + templates.size());
        } else {
            logger.debug("No Result found.");
        }

        return templates;
    }
    
    public boolean update(String uuid, ETemplate template) {
        ETemplate templateDB = null;
        try {
            Query query = entityManager.createNamedQuery(ETemplate.QUERY_FIND_BY_UUID);
            query.setParameter("uuid", uuid);
            templateDB = (ETemplate)query.getSingleResult();
        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
        }
        
        if (templateDB!=null){
            template.setId(templateDB.getId());
            logger.info("template to update with id"+template.getId());
            entityManager.merge(template);
            entityManager.flush();
        }else
            return false;
        return true;
    }
}
