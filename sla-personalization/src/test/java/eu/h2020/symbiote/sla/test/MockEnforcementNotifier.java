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
