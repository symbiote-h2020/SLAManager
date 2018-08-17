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
package eu.atos.sla.service.jpa;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IPenaltyDAO;
import eu.atos.sla.dao.IPenaltyDAO.SearchParameters;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.dao.IViolationDAO;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EBusinessValueList;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EPenalty;
import eu.atos.sla.datamodel.EPenaltyDefinition;
import eu.atos.sla.datamodel.EProvider;
import eu.atos.sla.datamodel.ETemplate;
import eu.atos.sla.datamodel.EViolation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/sla-repository-db-JPA-test-context.xml")
public class PenaltyDAOJpaTest extends
        AbstractTransactionalJUnit4SpringContextTests {
    private EntityManager entityManager;
    
    @Autowired
    IProviderDAO providerDao;
    
    @Autowired
    ITemplateDAO templateDao;
    
    @Autowired
    IPenaltyDAO penaltyDao;

    @Autowired
    IAgreementDAO agreementDAO;

    @Autowired
    IViolationDAO violationDao;
    
    @PersistenceContext(unitName = "slarepositoryDB")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Test
    public void getById() {

        Date dateTime = new Date();
        
        EPenaltyDefinition def = new EPenaltyDefinition(1, "euro", "100");
        savePenaltyDefinition(def);
        EViolation violation = new EViolation();
        violationDao.save(violation);
        
        EPenalty expected = new EPenalty("agreement-id", dateTime, "kpiname", def, violation);

        EPenalty saved = penaltyDao.save(expected);

        EPenalty actual = penaltyDao.getById(saved.getId());

        /*
         * All these should succeed, as expected and actual are the same object after save()
         */
        assertEquals(expected.getAgreementId(), actual.getAgreementId());
        assertEquals(expected.getDatetime().getTime(), actual.getDatetime().getTime());
        assertEquals(expected.getKpiName(), actual.getKpiName());
        assertEquals(expected.getUuid(), actual.getUuid());

        Assert.assertTrue(expected == actual);
    }

    private void savePenaltyDefinition(EPenaltyDefinition def) {
        entityManager.persist(def);
        entityManager.flush();
    }
    
    @Test
    public void testSearch() {
        
        SearchParameters params = new IPenaltyDAO.SearchParameters();
        params.setAgreementId(null);
        params.setGuaranteeTermName(null);
        params.setBegin(null);
        params.setEnd(null);
        
        penaltyDao.search(params);
    }
    
    @SuppressWarnings("unused")
    @Test
    public void testGetLastPenalty() {
        EProvider p = new EProvider(null, "p1", "Provider1");
        providerDao.save(p);
        
        ETemplate t = new ETemplate();
        t.setUuid("template1");
        t.setProvider(p);
        t.setText("<wsag:Template/>");
        templateDao.save(t);
        
        String id = "agreement1";
        EAgreement a1 = new EAgreement(id);
        a1.setProvider(p);
        a1.setTemplate(t);

        EPenaltyDefinition a1_t1_def1 = new EPenaltyDefinition(1, new Date(0), "one-shot", "1", "", "");
        EPenaltyDefinition a1_t1_def2 = new EPenaltyDefinition(2, new Date(1000), "long-term", "2", "", "");
        EPenaltyDefinition a1_t2_def1 = new EPenaltyDefinition(1, new Date(0), "single-shot", "3", "", "");
        
        EGuaranteeTerm t1 = newGuaranteeTerm("t1", Arrays.asList(a1_t1_def1, a1_t1_def2));
        EGuaranteeTerm t2 = newGuaranteeTerm("t2", Arrays.asList(a1_t2_def1));

        a1.setGuaranteeTerms(new ArrayList<EGuaranteeTerm>());
        a1.getGuaranteeTerms().add(t1);
        a1.getGuaranteeTerms().add(t2);
        
        agreementDAO.save(a1);
        
        Date _1000 = new Date(1000);
        Date _2000 = new Date(2000);
        Date _3000 = new Date(3000);
        Date _4000 = new Date(4000);
        EPenalty p1 = savePenalty(a1, t1, _1000, a1_t1_def1);
        EPenalty p2 = savePenalty(a1, t1, _2000, a1_t1_def2);
        EPenalty p3 = savePenalty(a1, t2, _3000, a1_t2_def1);
        EPenalty p4 = savePenalty(a1, t2, _4000, a1_t2_def1);
        
        assertEquals(_1000, penaltyDao.getLastPenaltyDate(id, a1_t1_def1, t1.getKpiName()));
        assertEquals(_2000, penaltyDao.getLastPenaltyDate(id, a1_t1_def2, t1.getKpiName()));
        assertEquals(_4000, penaltyDao.getLastPenaltyDate(id, a1_t2_def1, t2.getKpiName()));
    }
    
    private EPenalty savePenalty(EAgreement a, EGuaranteeTerm t, Date date, EPenaltyDefinition def) {
        
        EViolation v = new EViolation(a, t, null, t.getKpiName(), "", "", date);
        violationDao.save(v);
        
        EPenalty p = new EPenalty(a.getAgreementId(), date, "kpiname", def, v);
        penaltyDao.save(p);
        return p;
    }
    
    private EGuaranteeTerm newGuaranteeTerm(String kpiName, List<EPenaltyDefinition> defs) {
        EGuaranteeTerm t = new EGuaranteeTerm();
        t.setKpiName(kpiName);
        t.setName(kpiName);
        t.setServiceLevel("");
        t.setServiceName("");
        t.setServiceScope("");
        t.setBusinessValueList(new EBusinessValueList(0, defs));
        return t;
    }
}
