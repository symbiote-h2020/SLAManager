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
import eu.h2020.symbiote.model.mim.Comparator;
import eu.h2020.symbiote.model.mim.Federation;
import eu.h2020.symbiote.model.mim.FederationMember;
import eu.h2020.symbiote.model.mim.QoSConstraint;
import eu.h2020.symbiote.model.mim.QoSMetric;
import eu.h2020.symbiote.sla.federation.RabbitFederationListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/personalization-test-context.xml")
@Transactional
public class FederationTest {
  
  private static final String FED_ID = "fed1";
  
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
  
  private Federation federation = new Federation();
  private QoSConstraint availability;
  private QoSConstraint load;
  
  @Before
  public void setUp() throws Exception {
    
    federation.setId(FED_ID);
  
    List<FederationMember> members = new ArrayList<>();
    
    FederationMember myself = new FederationMember();
    myself.setPlatformId(platformId);
    
    members.add(myself);
    
    federation.setMembers(members);
  
    List<QoSConstraint> constraints = new ArrayList<>();
    
    availability = new QoSConstraint();
    availability.setMetric(QoSMetric.availability);
    availability.setResourceType("actuator");
    availability.setDuration(30);
    availability.setComparator(Comparator.greaterThanOrEqual);
    availability.setThreshold(0.9);
    
    constraints.add(availability);
    
    load = new QoSConstraint();
    load.setMetric(QoSMetric.load);
    load.setComparator(Comparator.lessThan);
    load.setThreshold(75);
    
    constraints.add(load);
    
    
    federation.setSlaConstraints(constraints);
    
  }
  
  @Test
  @Transactional
  public void TestFederations() {
    listener.federationUpdate(federation);
  
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
    
    federation.getMembers().clear();
    
    listener.federationUpdate(federation);
    
    provider = providerDAO.getByName(platformId);
    assert provider != null;
    
    template = templateDAO.getById(template.getId());
    assert template == null;
    
    agreement = agreementDAO.getByAgreementId(federation.getId());
    assert agreement == null;
    
    enforcenemt = enforcementService.getEnforcementJobByAgreementId(federation.getId());
    assert enforcenemt == null;
  }
  
  private void checkRules(EAgreement agreement) {
  
    List<EGuaranteeTerm> guaranteeTerms = agreement.getGuaranteeTerms();
    assert guaranteeTerms.size() == 2;
    
    for (EGuaranteeTerm term : guaranteeTerms) {
      String kpiName = term.getKpiName();
      
      EGuaranteeTerm expected = null;
      if (kpiName.startsWith(QoSMetric.availability.toString())) {
        expected = listener.geteGuaranteeTerm(availability);
      } else if (kpiName.startsWith(QoSMetric.load.toString())) {
        expected = listener.geteGuaranteeTerm(load);
      }
      
      assert term.getKpiName().equals(expected.getKpiName());
      assert term.getServiceLevel().equals(expected.getServiceLevel());
    }
    
  }
  
}
