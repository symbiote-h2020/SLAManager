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
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.evaluation.guarantee.GuaranteeTermEvaluator;
import eu.atos.sla.notification.IAgreementEnforcementNotifier;
import eu.atos.sla.notification.NotificationException;
import eu.h2020.symbiote.sla.SLAConstants;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class RabbitEnforcementNotifier implements IAgreementEnforcementNotifier{
  
  @Autowired
  private RabbitTemplate rabbitTemplate;
  
  @Override
  public void onFinishEvaluation(
      EAgreement agreement,
      Map<EGuaranteeTerm, GuaranteeTermEvaluator.GuaranteeTermEvaluationResult> guaranteeTermEvaluationMap)
      throws NotificationException {
    
    ViolationNotification notification = new ViolationNotification();
    notification.setFederationId(agreement.getAgreementId());
    
    guaranteeTermEvaluationMap.forEach((term, result) -> {
      if ((result.getViolations() != null) && (!result.getViolations().isEmpty())) {
        Violation violation = new Violation();
        violation.setConstraint(term.getServiceLevel());
        violation.setActualValue(result.getViolations().get(0).getActualValue());
      }
    });
    
    if (!notification.getViolations().isEmpty()) {
      rabbitTemplate.convertAndSend(SLAConstants.EXCHANGE_NAME_SLAM, SLAConstants.VIOLATION_KEY, notification);
    }
  }
}
