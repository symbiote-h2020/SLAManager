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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.UUID;

import eu.atos.sla.datamodel.EAgreement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IViolationDAO;
import eu.atos.sla.dao.IViolationDAO.SearchParameters;
import eu.atos.sla.datamodel.EViolation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/sla-repository-db-JPA-test-context.xml")
public class ViolationDAOJpaTest extends
        AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    IViolationDAO violationDAO;

    @Autowired
    IAgreementDAO agreementDAO;

    private boolean setupDone = false;

    @Test
    public void notNull() {
        if (violationDAO == null)
            fail();
    }

    @Test
    public void getById() {

        String violationUuid = UUID.randomUUID().toString();
        Date dateTime = new Date(2323L);
        EViolation violation = new EViolation();
        EViolation violationSaved = new EViolation();

        violation.setUuid(violationUuid);
        violation.setContractUuid(UUID.randomUUID().toString());
        violation.setServiceName("Service name");
        violation.setServiceScope("Service scope");
        violation.setKpiName("response time");
        violation.setDatetime(dateTime);
        violation.setExpectedValue("3.0");
        violation.setActualValue("5.0");

        try {
            violationSaved = violationDAO.save(violation);
        } catch (Exception e) {
            fail();
        }

        Long id = violationSaved.getId();
        violationSaved = violationDAO.getById(id);

        assertEquals("response time", violationSaved.getKpiName());
        assertEquals("3.0", violationSaved.getExpectedValue());

        EViolation nullBreach = violationDAO.getById(new Long(30000));
        assertEquals(null, nullBreach);

    }
    
    @Test
    public void testSearch() {
        
        SearchParameters params = new IViolationDAO.SearchParameters();
        params.setAgreementId(null);
        params.setGuaranteeTermName(null);
        params.setProviderUuid(null);
        params.setBegin(null);
        params.setEnd(null);
        
        violationDAO.search(params);
    }

    @Test
    public void testGetLastViolationDate() {
        setup();

        EAgreement agreement = new EAgreement();
        agreement.setAgreementId("id");
        Date d = violationDAO.getLastViolationDate(agreement, "responsetime");

        assertEquals(d.getTime(), 3L);
    }

    @Test
    public void testGetLastViolationDateWhenNoResult() {

        EAgreement agreement = new EAgreement();
        agreement.setAgreementId("idnoexiste");
        Date d = violationDAO.getLastViolationDate(agreement, "responsetime");

        assertNull(d);
    }

    private void setup() {
        if (setupDone) {
            return;
        }
        setupDone = true;

        /*
         * get last violation setup
         */
        String agreementId = "id";
        EViolation v;
        v = newViolation(agreementId, new Date(3L));
        violationDAO.save(v);

        v = newViolation(agreementId, new Date(2L));
        violationDAO.save(v);

        v = newViolation(agreementId, new Date(1L));
        violationDAO.save(v);
    }

    private EViolation newViolation(String agreementId, Date date) {

        String violationUuid = UUID.randomUUID().toString();

        EViolation violation = new EViolation();
        violation.setUuid(violationUuid);
        violation.setContractUuid(agreementId);
        violation.setServiceName("");
        violation.setServiceScope("");
        violation.setKpiName("responsetime");
        violation.setDatetime(date);
        violation.setExpectedValue("1");
        violation.setActualValue("2");

        return violation;
    }
}
