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

import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.evaluation.guarantee.GuaranteeTermEvaluator;
import eu.atos.sla.notification.IAgreementEnforcementNotifier;
import eu.atos.sla.notification.NotificationException;
import eu.h2020.symbiote.model.mim.QoSMetric;

import java.util.Map;

public class MockEnforcementNotifier implements IAgreementEnforcementNotifier {
  
  @Override
  public void onFinishEvaluation(
      EAgreement agreement,
      Map<EGuaranteeTerm, GuaranteeTermEvaluator.GuaranteeTermEvaluationResult> guaranteeTermEvaluationMap)
      throws NotificationException {
    
    guaranteeTermEvaluationMap.forEach((term, value) -> {
      if (term.getKpiName().startsWith(QoSMetric.availability.toString())) {
        assert !value.getViolations().isEmpty();
      }
      
      if (term.getKpiName().startsWith(QoSMetric.load.toString())) {
        assert value.getViolations().isEmpty();
      }
    });
    
  }
}
