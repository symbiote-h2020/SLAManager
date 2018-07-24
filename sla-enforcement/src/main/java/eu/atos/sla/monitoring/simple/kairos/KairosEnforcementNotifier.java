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
package eu.atos.sla.monitoring.simple.kairos;

import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.evaluation.guarantee.GuaranteeTermEvaluator.GuaranteeTermEvaluationResult;
import eu.atos.sla.notification.IAgreementEnforcementNotifier;
import eu.atos.sla.notification.NotificationException;

public class KairosEnforcementNotifier implements IAgreementEnforcementNotifier, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(KairosEnforcementNotifier.class);

    @Value("${KAIROSDB_URL}")
    private String kairosDbUrlStr;
    
    private URL kairosDbUrl;

    @Override
    public void onFinishEvaluation(EAgreement agreement,
            Map<EGuaranteeTerm, GuaranteeTermEvaluationResult> evaluation)
            throws NotificationException {
        
        try (KairosPusher pusher = new KairosPusher(kairosDbUrl)) {
            
            for (Map.Entry<EGuaranteeTerm, GuaranteeTermEvaluationResult> item : evaluation.entrySet()) {
                
                EGuaranteeTerm gt = item.getKey();
                GuaranteeTermEvaluationResult eval = item.getValue();
                
                if (eval.getViolations().isEmpty()) {
                    continue;
                }
                pusher.pushViolations(agreement.getAgreementId(), gt.getName(), eval.getViolations());
                logger.info("Violations {} saved to KairosDb", gt.getName());
            }
        } catch (Exception e) {
            logger.warn("Exception ignored: {}", e.getMessage());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        kairosDbUrl = new URL(kairosDbUrlStr);
        logger.debug("KAIROSDB_URL={}", kairosDbUrl.toString());
    }

}
