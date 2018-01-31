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
package eu.h2020.symbiote.sla.federation;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EProvider;
import eu.atos.sla.enforcement.IEnforcementService;
import eu.atos.sla.evaluation.constraint.simple.Operator;
import eu.h2020.symbiote.model.mim.Federation;
import eu.h2020.symbiote.model.mim.QoSConstraint;
import eu.h2020.symbiote.sla.SLAConstants;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RabbitFederationListener {
  
  @Value("${platform.id}")
  private String platformId;
  
  @Autowired
  FederationRepository federationRepository;
  
  @Autowired
  IProviderDAO providerDAO;
  
  @Autowired
  IAgreementDAO agreementDAO;
  
  @Autowired
  private IEnforcementService enforcementService;
  
  @RabbitListener(bindings = @QueueBinding(
      value = @Queue(value = SLAConstants.SLA_REGISTRATION_QUEUE_NAME, durable = "true",
          exclusive = "true", autoDelete = "true"),
      exchange = @Exchange(value = SLAConstants.EXCHANGE_NAME_FM, durable = "true"),
      key = SLAConstants.FEDERATION_REGISTRATION_KEY)
  )
  public void resourceRegistration(@Payload Federation federation) {
    
    Optional<Federation> existing = federationRepository.findById(federation.getId());
    if (existing.isPresent()) {
      
      EProvider provider = providerDAO.getByName(platformId);
      if (provider == null) {
        provider = new EProvider();
        provider.setName(platformId);
        provider.setUuid(UUID.randomUUID().toString());
        provider = providerDAO.save(provider);
      }
  
      EAgreement agreement = new EAgreement();
      agreement.setProvider(provider);
      agreement.setAgreementId(federation.getId());
      agreement.setConsumer(federation.getId());
      agreement.setCreationDate(new Date());
      agreement.setName(federation.getId());
      agreement.setServiceId(federation.getId());
      
      agreement.setGuaranteeTerms(getGuaranteeTerms(federation.getSlaConstraints()));
      
      agreement = agreementDAO.save(agreement);
      
      enforcementService.createEnforcementJob(agreement.getAgreementId());
      enforcementService.startEnforcement(agreement.getAgreementId());
      
      federationRepository.save(federation);
    }
    
  }
  
  private List<EGuaranteeTerm> getGuaranteeTerms(List<QoSConstraint> slaConstraints) {
    List<EGuaranteeTerm> guaranteeTerms = new ArrayList<>();
    for (QoSConstraint constraint : slaConstraints) {
      EGuaranteeTerm term = new EGuaranteeTerm();
      String kpiName = constraint.getMetric().toString();
      if (constraint.getResourceType() != null) {
        kpiName = kpiName + "." + constraint.getResourceType();
      }
      
      String duration = "all";
      if (constraint.getDuration() != null) {
        duration = constraint.getDuration().toString();
      }
      
      kpiName = kpiName + "." + duration;
      
      term.setKpiName(kpiName);
      
      String serviceLevel = kpiName;
      Operator comparator = null;
      switch (constraint.getComparator()) {
        case equal:
          comparator = Operator.EQ;
          break;
        case lessThanOrEqual:
          comparator = Operator.LE;
          break;
        case lessThan:
          comparator = Operator.LT;
          break;
        case greaterThan:
          comparator = Operator.GT;
          break;
        case greaterThanOrEqual:
          comparator = Operator.GE;
          break;
      }
      
      String slo = kpiName + " " + comparator.toString() + constraint.getThreshold();
      
      term.setServiceLevel(slo);
      
      guaranteeTerms.add(term);
    }
    return guaranteeTerms;
  }
  
}
