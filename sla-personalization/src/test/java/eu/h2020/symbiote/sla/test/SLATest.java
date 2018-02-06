/**
 * Copyright 2017 Atos
 * Contact: Atos <jose.sanchezm@atos.net>
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
package eu.h2020.symbiote.sla.test;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.enforcement.IEnforcementService;
import eu.atos.sla.monitoring.IMetricsRetriever;
import eu.atos.sla.monitoring.IMonitoringMetric;
import eu.h2020.symbiote.model.mim.Federation;
import eu.h2020.symbiote.sla.federation.RabbitFederationListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/personalization-test-context.xml")
@Transactional
public class SLATest {
  
  @Value("${platform.id}")
  private String platformId;
  
  @Autowired
  private RabbitFederationListener listener;
  
  @Autowired
  private IAgreementDAO agreementDAO;
  
  @Autowired
  private IEnforcementService enforcementService;
  
  private Federation federation;
  
  private IMetricsRetriever retriever = new MockMetricRetriever();
  
  @Before
  public void setUp() {
    
    federation = SymbioteTestUtils.createTestFederation(FederationTest.FED_ID, platformId);
    listener.federationCreation(SymbioteTestUtils.toMessage(federation));
    
  }
  
  @Test
  @Transactional
  public void testSlaEnforcement() {
  
    EAgreement agreement = agreementDAO.getByAgreementId(federation.getId());
  
    Map<EGuaranteeTerm, List<IMonitoringMetric>> termsResult = new HashMap<>();
    for (EGuaranteeTerm term : agreement.getGuaranteeTerms()) {
      
      termsResult.put(term, retriever.getMetrics(
          agreement.getAgreementId(), agreement.getServiceId(), term.getKpiName(),
          new Date(), new Date(), 1));
    }
    
    enforcementService.doEnforcement(agreement, termsResult);
    
  }
}
