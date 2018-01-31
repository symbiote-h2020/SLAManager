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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.dao.jpa.AgreementDAOJpa;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EPolicy;
import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.datamodel.EProvider;
import eu.atos.sla.datamodel.ETemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/sla-repository-db-JPA-test-context.xml")
public class AgreementDAOJpaTest extends
        AbstractTransactionalJUnit4SpringContextTests {
    
    @Autowired
    IProviderDAO providerDAO;
    
    @Autowired
    IAgreementDAO agreementDAO;

    @Autowired
    ITemplateDAO templateDAO;
    
    private static boolean setupDone;
    
    private EProvider provider;
    private ETemplate template;
    
    private void setUp() {
        if (setupDone) {
            return;
        }
        provider = new EProvider(null, UUID.randomUUID().toString(), "Provider2");
        providerDAO.save(provider);

        String templateUuid = UUID.randomUUID().toString();
        template = new ETemplate();
        template.setText("Template name 1");
        template.setUuid(templateUuid);
        template.setProvider(provider);
        
        templateDAO.save(template);
    }
    
    @Test
    public void notNull() {
        if (agreementDAO == null)
            fail();
    }

    
    
    @Test
    public void getById() {

        setUp();
        
        String uuid = UUID.randomUUID().toString();

        StringBuilder agreementText = new StringBuilder();

        agreementText
                .append("<Agreement xmlns=\"http://www.ggf.org/namespaces/ws-agreement\" AgreementId=\""
                        + uuid + "\">\n");
        agreementText.append("  <Name>ExampleAgreement</name>\n");
        agreementText.append("  <Context>\n");
        agreementText
                .append("   <AggreementInitiator>RandomClient</AgreementInitiator>\n");
        agreementText
                .append("   <AgreementResponder>Provider02</AgreementResponder>\n");
        agreementText
                .append("   <ServiceProvider>AgreementResponder</ServiceProvider>\n");
        agreementText
                .append("   <ExpirationTime>2014-03-07-1200</ExpirationTime>\n");
        agreementText
                .append("   <TemplateId>contract-template-2007-12-04</<TemplateId>>\n");
        agreementText.append("  </Context>\n");
        agreementText.append("</Agreement>\n");

        EAgreement agreement = new EAgreement();
        agreement.setAgreementId(uuid);
        agreement.setConsumer("consumer2");
        agreement.setProvider(provider);
        agreement.setTemplate(template);
        agreement.setText(agreementText.toString());

        @SuppressWarnings("unused")
        EAgreement agreementSaved = new EAgreement();
        agreementSaved = agreementDAO.save(agreement);

        EAgreement nullAgreement = agreementDAO.getById(new Long(30000));
        assertEquals(null, nullAgreement);
    }


    @Test
    public void save() {

        setUp();
        
        StringBuilder agreementText = new StringBuilder();

        String agreementId = UUID.randomUUID().toString();

        agreementText
                .append("<Agreement xmlns=\"http://www.ggf.org/namespaces/ws-agreement\" AgreementId=\""
                        + agreementId + "\">\n");
        agreementText.append("  <Name>ExampleAgreement</name>\n");
        agreementText.append("  <Context>\n");
        agreementText
                .append("   <AggreementInitiator>RandomClient</AgreementInitiator>\n");
        agreementText
                .append("   <AgreementResponder>Provider01</AgreementResponder>\n");
        agreementText
                .append("   <ServiceProvider>AgreementResponder</ServiceProvider>\n");
        agreementText
                .append("   <ExpirationTime>2014-03-07-1200</ExpirationTime>\n");
        agreementText
                .append("   <TemplateId>contract-template-2007-12-04</<TemplateId>>\n");
        agreementText.append("  </Context>\n");
        agreementText.append("</Agreement>\n");

        eu.atos.sla.datamodel.EAgreement.AgreementStatus status = eu.atos.sla.datamodel.EAgreement.AgreementStatus.PENDING;

        // Guarantee terms

        EGuaranteeTerm guaranteeTerm = new EGuaranteeTerm();
        guaranteeTerm.setName("guarantee term name");
        guaranteeTerm.setServiceName("service Name");

        List<EGuaranteeTerm> guaranteeTerms = new ArrayList<EGuaranteeTerm>();
        guaranteeTerms.add(guaranteeTerm);

        EAgreement agreement = new EAgreement();
        agreement.setAgreementId(agreementId);
        agreement.setConsumer("Consumer2");
        agreement.setProvider(provider);
        agreement.setStatus(status);
        agreement.setTemplate(template);
        agreement.setText(agreementText.toString());
        agreement.setGuaranteeTerms(guaranteeTerms);

        EViolation violation = new EViolation();

        violation.setContractUuid(agreementId);
        violation.setActualValue("8.0");
        violation.setExpectedValue("5.0");
        @SuppressWarnings("unused")
        EAgreement agreementSaved = new EAgreement();
        try {
            agreementSaved = agreementDAO.save(agreement);

        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
    
    @Test
    public void testSaveWindows() throws FileNotFoundException {
        setUp();
        
        String text = readFile("/samples/test_parse_agreement.xml");
        /*
         * Now build agreement entities by hand
         */
        EAgreement agreement = new EAgreement();
        
        agreement.setAgreementId(UUID.randomUUID().toString());
        agreement.setTemplate(template);
        agreement.setConsumer("consumer2");
        agreement.setProvider(provider);
        agreement.setText(text);

        // Guarantee terms

        EGuaranteeTerm guaranteeTerm = new EGuaranteeTerm();
        guaranteeTerm.setName("guarantee term name");
        guaranteeTerm.setServiceName("service Name");
        guaranteeTerm.setServiceLevel("responsetime LT 200");
        guaranteeTerm.setKpiName("fast_response_time");
        
        EPolicy[] policies = new EPolicy[] {
            new EPolicy(2, new Date(120 * 1000)),
            new EPolicy(3, new Date(3600 * 1000))
        };
        guaranteeTerm.setPolicies(Arrays.<EPolicy>asList(policies));
        

        List<EGuaranteeTerm> guaranteeTerms = new ArrayList<EGuaranteeTerm>();
        guaranteeTerms.add(guaranteeTerm);
        agreement.setGuaranteeTerms(guaranteeTerms);
        
        agreementDAO.save(agreement);
        assertNotNull(agreement.getId());

        ((AgreementDAOJpa)agreementDAO).getEntityManager().detach(agreement);
        
        agreement = (EAgreement) agreementDAO.getByAgreementId(agreement.getAgreementId());
        guaranteeTerm = agreement.getGuaranteeTerms().get(0);
        assertNotNull(guaranteeTerm.getPolicies());
        assertEquals(2, guaranteeTerm.getPolicies().size());
        for (EPolicy p : guaranteeTerm.getPolicies()) {
            System.out.println(p.getCount() + " " + p.getTimeInterval());
        }

    }
    
    private String readFile(String path) throws FileNotFoundException {
        URL url = this.getClass().getResource(path);
        if (url == null) {
            throw new NullPointerException("Resource" + path + " not found");
        }
        File f = new File(url.getFile());
        Scanner s = new Scanner(new FileReader(f));
        String result = s.next();
        s.close();
        
        return result;
    }
    
}
