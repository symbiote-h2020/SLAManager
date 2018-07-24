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
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EEnforcementJob;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EProvider;
import eu.atos.sla.datamodel.ETemplate;
import eu.atos.sla.enforcement.IEnforcementService;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/personalization-test-context.xml")
@Transactional
public class FederationTest {
  
  public static final String FED_ID = "fed1";
  
  @Autowired
  private RabbitFederationListener listener;
  
  @Value("${platform.id}")
  private String platformId;
  
  @Autowired
  private IProviderDAO providerDAO;
  
  @Autowired
  private IAgreementDAO agreementDAO;
  
  @Autowired
  private ITemplateDAO templateDAO;
  
  @Autowired
  private IEnforcementService enforcementService;
  
  private Federation federation;
  
  
  
  @Before
  public void setUp() throws Exception {
    
    federation = SymbioteTestUtils.createTestFederation(FED_ID, platformId);
    
  }
  
  @Test
  @Transactional
  public void TestFederations() {
    listener.federationCreation(SymbioteTestUtils.toMessage(federation));
  
    verifyFederation();
    
    federation.getSlaConstraints().remove(0);
    
    listener.federationUpdated(SymbioteTestUtils.toMessage(federation));
    
    verifyFederation();
    
    listener.federationRemoval(SymbioteTestUtils.toMessage(federation));
    
    assert providerDAO.getByName(platformId) != null;
    
    assert templateDAO.getAll().isEmpty();
    
    assert  agreementDAO.getByAgreementId(federation.getId()) == null;
    
    assert  enforcementService.getEnforcementJobByAgreementId(federation.getId()) == null;
  }
  
  private void verifyFederation() {
    EProvider provider = providerDAO.getByName(platformId);
    assert  provider != null;
  
    EAgreement agreement = agreementDAO.getByAgreementId(federation.getId());
    assert agreement != null;
    assert agreement.getProvider().getName().equals(provider.getName());
    assert agreement.getProvider().getUuid().equals(provider.getUuid());
    checkRules(agreement);
  
    ETemplate template = agreement.getTemplate();
    assert template != null;
    assert template.getProvider() != null;
    assert template.getProvider().getName().equals(provider.getName());
  
    EEnforcementJob enforcenemt = enforcementService.getEnforcementJobByAgreementId(federation.getId());
    assert enforcenemt != null;
  }
  
  private void checkRules(EAgreement agreement) {
  
    List<EGuaranteeTerm> guaranteeTerms = agreement.getGuaranteeTerms();
    Map<String, EGuaranteeTerm> expectedTerms = federation.getSlaConstraints().stream()
                                                    .collect(
                                                        Collectors.toMap(
                                                            c -> listener.getKpiName(c),
                                                            c -> listener.geteGuaranteeTerm(c)));
    
    assert guaranteeTerms.size() == federation.getSlaConstraints().size();
    
    for (EGuaranteeTerm term : guaranteeTerms) {
      String kpiName = term.getKpiName();
      
      EGuaranteeTerm expected = expectedTerms.get(kpiName);
      assert term.getKpiName().equals(expected.getKpiName());
      assert term.getServiceLevel().equals(expected.getServiceLevel());
    }
    
  }
  
}
