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
package eu.atos.sla.enforcement;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EEnforcementJob;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EProvider;
import eu.atos.sla.datamodel.ETemplate;
import eu.atos.sla.datamodel.EViolation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/enforcement-test-context.xml")
@Transactional
public class AgreementEnforcementTest {

    @Autowired
    AgreementEnforcement agreementEnforcement;
    
    @Autowired
    IAgreementDAO agreementDao;
    
    @Autowired
    ITemplateDAO templateDao;
    
    @Autowired
    IEnforcementService enforcementService;
    
    @Autowired
    IProviderDAO providerDao;
    
    EAgreement agreement;
    
    @Before
    @Transactional
    public void setUp() throws Exception {
        String kpiName = "LATENCY";
        String constraint = kpiName + " LT 0.5";

        EProvider provider = newProvider();
        EProvider psaved = providerDao.save(provider);
        
        ETemplate template = newTemplate(psaved);
        
        templateDao.save(template);
        
        agreement = new EAgreement();
        agreement.setAgreementId("test-agreement");
        agreement.setProvider(provider);
        agreement.setTemplate(template);
        
        agreement.setGuaranteeTerms(Collections.singletonList(newGuaranteeTerm(kpiName, constraint)));
        agreement.getGuaranteeTerms().get(0).setViolations(new ArrayList<EViolation>());
        
        agreementDao.save(agreement);
        
        EEnforcementJob job = new EEnforcementJob();
        job.setAgreement(agreement);
        
        enforcementService.createEnforcementJob(job);
    }

    @Test
    public void testEnforce() {
        Date now = new Date();
        Date since = new Date(now.getTime() - 10000);
        
        agreementEnforcement.enforce(agreement, since, false);
        
        EAgreement a = agreementDao.getByAgreementId(agreement.getAgreementId());
        assertEquals(agreement.getGuaranteeTerms().get(0).getStatus(), a.getGuaranteeTerms().get(0).getStatus());
    }

//  @Test
    public void testEnforceWithMetrics() {
        fail("Not yet implemented");
    }
    
    private EProvider newProvider() {
        EProvider provider = new EProvider();
        provider.setName("provider-name");
        provider.setUuid("provider-uuid");
        
        return provider;
    }

    private ETemplate newTemplate(EProvider provider) {
        ETemplate template = new ETemplate();
        template.setUuid("templateId");
        template.setText("");
        template.setProvider(provider);
        return template;
    }
    private EGuaranteeTerm newGuaranteeTerm(String kpiName, String constraint) {
        
        EGuaranteeTerm t = new EGuaranteeTerm();
        t.setKpiName(kpiName);
        t.setServiceLevel(constraint);
        
        return t;
    }

    
}
