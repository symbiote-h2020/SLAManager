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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.dao.IGuaranteeTermDAO;
import eu.atos.sla.dao.jpa.GuaranteeTermDAOJpa;
import eu.atos.sla.datamodel.EBreach;
import eu.atos.sla.datamodel.EBusinessValueList;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EPenaltyDefinition;
import eu.atos.sla.datamodel.EViolation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/sla-repository-db-JPA-test-context.xml")
public class GuaranteeTermDAOJpaTest extends
        AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    IGuaranteeTermDAO guaranteeTermDAO;

    @Test
    public void notNull() {
        if (guaranteeTermDAO == null)
            fail();
    }

    @Test
    public void save() {

        String contractUUID = UUID.randomUUID().toString();

        EViolation violation = new EViolation();
        violation.setActualValue("value 1");
        violation.setContractUuid(contractUUID);
        violation.setDatetime(new Date(2323));
        violation.setExpectedValue("expected value 2");

        EBreach breach = new EBreach();
        breach.setKpiName("metric name");
        breach.setDatetime(new Date(12345));
        breach.setValue("6.0");
        breach.setAgreementUuid(contractUUID);

        String contractUUID2 = UUID.randomUUID().toString();

        EBreach breach2 = new EBreach();
        breach2.setKpiName("metric name 2");
        breach2.setDatetime(new Date(12325));
        breach2.setValue("7.0");
        breach2.setAgreementUuid(contractUUID2);

        List<EBreach> breaches = new ArrayList<EBreach>();
        breaches.add(breach);
        breaches.add(breach2);

        EGuaranteeTerm guaranteeTerm = new EGuaranteeTerm();
        guaranteeTerm.setName("name guarantee term");
//      guaranteeTerm.setBreaches(breaches);
        
        
        EPenaltyDefinition penalty = newPenalty(1, "%", "10");
        List<EPenaltyDefinition> compensations = Collections.singletonList(penalty);
        EBusinessValueList bvl = new EBusinessValueList(1, compensations);
        guaranteeTerm.setBusinessValueList(bvl);

        EGuaranteeTerm saved;
        saved = guaranteeTermDAO.save(guaranteeTerm);

        ((GuaranteeTermDAOJpa)guaranteeTermDAO).getEntityManager().detach(guaranteeTerm);
        
        EGuaranteeTerm loaded = guaranteeTermDAO.getById(saved.getId());
        
        assertEquals(guaranteeTerm.getKpiName(), loaded.getKpiName());
        assertEquals(guaranteeTerm.getName(), loaded.getName());
        assertEquals(guaranteeTerm.getServiceLevel(), loaded.getServiceLevel());
        assertEquals(guaranteeTerm.getServiceName(), loaded.getServiceScope());
        assertEquals(guaranteeTerm.getServiceScope(), loaded.getServiceScope());
        
        assertEquals(guaranteeTerm.getBusinessValueList(), loaded.getBusinessValueList());

    }

    @Test
    public void getById() {

        int size = guaranteeTermDAO.getAll().size();

        EGuaranteeTerm guaranteeTerm = new EGuaranteeTerm();

        guaranteeTerm.setName("guarantee term name");
        guaranteeTerm.setServiceName("service Name");

        @SuppressWarnings("unused")
        EGuaranteeTerm saved;
        try {
            saved = guaranteeTermDAO.save(guaranteeTerm);
        } catch (Exception e) {
            fail();
        }

        EGuaranteeTerm guaranteeFromDatabase = guaranteeTermDAO.getAll().get(
                size);
        Long id = guaranteeFromDatabase.getId();
        guaranteeFromDatabase = guaranteeTermDAO.getById(id);

        assertEquals("guarantee term name", guaranteeFromDatabase.getName());
        assertEquals("service Name", guaranteeFromDatabase.getServiceName());

        EGuaranteeTerm nullBreach = guaranteeTermDAO.getById(new Long(30000));
        assertEquals(null, nullBreach);

    }

    @Test
    public void detete() {

        @SuppressWarnings("unused")
        int size = guaranteeTermDAO.getAll().size();

        String contractUUID = UUID.randomUUID().toString();
        EViolation violation = new EViolation();
        violation.setActualValue("value 1");
        violation.setContractUuid(contractUUID);
        violation.setDatetime(new Date(2323));
        violation.setExpectedValue("expected value 2");

        EBreach breach = new EBreach();
        breach.setKpiName("metric name");
        breach.setDatetime(new Date(12345));
        breach.setValue("6.0");
        breach.setAgreementUuid(contractUUID);

        List<EBreach> breaches = new ArrayList<EBreach>();
        breaches.add(breach);

        EGuaranteeTerm guaranteeTerm = new EGuaranteeTerm();
        guaranteeTerm.setName("name guarantee term");
//      guaranteeTerm.setBreaches(breaches);
        guaranteeTerm.setServiceName("uptime sensors");

        EGuaranteeTerm saved = null;
        try {
            saved = guaranteeTermDAO.save(guaranteeTerm);
        } catch (Exception e) {
            fail();
        }

        boolean deleted = guaranteeTermDAO.delete(saved);
        assertTrue(deleted);

        deleted = guaranteeTermDAO.delete(saved);
        assertTrue(!deleted);

        saved = guaranteeTermDAO.getById(new Long(223232));
        assertEquals(null, saved);

    }

    @Test
    public void update() {

        int size = guaranteeTermDAO.getAll().size();

        @SuppressWarnings("unused")
        String uuid = UUID.randomUUID().toString();

        String contractUUID = UUID.randomUUID().toString();

        EViolation violation = new EViolation();
        violation.setActualValue("value 1");
        violation.setContractUuid(contractUUID);
        violation.setDatetime(new Date(2323));
        violation.setExpectedValue("expected value 2");

        EBreach breach = new EBreach();
        breach.setKpiName("metric name");
        breach.setDatetime(new Date(12345));
        breach.setValue("6.0");
        breach.setAgreementUuid(contractUUID);

        List<EBreach> breaches = new ArrayList<EBreach>();
        breaches.add(breach);

        EGuaranteeTerm guaranteeTerm = new EGuaranteeTerm();
        guaranteeTerm.setName("name guarantee term");
//      guaranteeTerm.setBreaches(breaches);

        @SuppressWarnings("unused")
        EGuaranteeTerm saved;
        try {
            saved = guaranteeTermDAO.save(guaranteeTerm);
        } catch (Exception e) {
            fail();
        }

        EGuaranteeTerm guaranteeTermFromDatabase = guaranteeTermDAO.getAll()
                .get(size);
        Long id = guaranteeTermFromDatabase.getId();
        assertEquals("name guarantee term", guaranteeTermFromDatabase.getName());
        guaranteeTermFromDatabase.setName("name updated");
        boolean updated = guaranteeTermDAO.update(guaranteeTermFromDatabase);
        assertTrue(updated);

        guaranteeTermFromDatabase = guaranteeTermDAO.getById(id);
        assertEquals("name updated", guaranteeTermFromDatabase.getName());

    }
    
    private EPenaltyDefinition newPenalty(
            int count, String valueUnit, String valueExpression) {
        
        EPenaltyDefinition result;
        result = new EPenaltyDefinition(count, valueUnit, valueExpression);
        return result;
    }

}
