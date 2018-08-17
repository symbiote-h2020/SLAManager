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
package eu.h2020.symbiote.sla.notification;

import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EBreach;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.evaluation.guarantee.GuaranteeTermEvaluator;
import eu.atos.sla.notification.IAgreementEnforcementNotifier;
import eu.atos.sla.notification.NotificationException;
import eu.h2020.symbiote.cloud.sla.model.Violation;
import eu.h2020.symbiote.cloud.sla.model.ViolationNotification;
import eu.h2020.symbiote.sla.SLAConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class RabbitEnforcementNotifier implements IAgreementEnforcementNotifier{
  
  private static final Logger logger = LoggerFactory.getLogger(RabbitEnforcementNotifier.class);
  
  @Autowired
  private RabbitTemplate rabbitTemplate;
  
  @Override
  public void onFinishEvaluation(
      EAgreement agreement,
      Map<EGuaranteeTerm, GuaranteeTermEvaluator.GuaranteeTermEvaluationResult> guaranteeTermEvaluationMap)
      throws NotificationException {
    
    logger.debug("Evaluation finished, checking violations for SLA " + agreement.getAgreementId());
    
    ViolationNotification notification = new ViolationNotification();
    notification.setFederationId(agreement.getAgreementId());
    
    guaranteeTermEvaluationMap.forEach((term, result) -> {
      if ((result.getViolations() != null) && (!result.getViolations().isEmpty())) {
        Violation violation = new Violation();
        EViolation slaViolation = result.getViolations().get(0);
        violation.setConstraint(term.getServiceLevel());
        violation.setActualValue(slaViolation.getActualValue());
  
        List<EBreach> breaches = slaViolation.getBreaches();
        if (breaches != null && !breaches.isEmpty()) {
          EBreach breach = breaches.get(0);
          violation.setDeviceId(breach.getKpiName());
          violation.setDate(breach.getDatetime());
        }
        
        notification.getViolations().add(violation);
      }
    });
    
    if (!notification.getViolations().isEmpty()) {
      logger.debug("Found violations for SLA " + agreement.getAgreementId() + ". Notifying...");
      rabbitTemplate.convertAndSend(SLAConstants.EXCHANGE_NAME_SLAM, SLAConstants.VIOLATION_KEY, notification);
    } else {
      logger.debug("No violations found for SLA " + agreement.getAgreementId());
    }
  }
}
