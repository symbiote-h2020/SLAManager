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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EBusinessValueList;
import eu.atos.sla.datamodel.EPenaltyDefinition;
import eu.atos.sla.datamodel.EPolicy;
import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EProvider;

public class TestAgreementFactory {
    
    public static EAgreement newAgreement(List<EGuaranteeTerm> terms) {
        
        return newAgreement(
                UUID.randomUUID().toString(),
                new EProvider(),
                "",
                terms
                );
    }
    
    public static EAgreement newAgreement(
            String agreementId, 
            EProvider provider, 
            String consumer, 
            List<EGuaranteeTerm> terms) {
        
        EAgreement result = new EAgreement();
        result.setAgreementId(agreementId);
        result.setProvider(provider);
        result.setConsumer(consumer);
        result.setGuaranteeTerms(terms);
        
        return result;
    }
    
    public static EGuaranteeTerm newGuaranteeTerm(String kpiName, String constraint) {

        return newGuaranteeTerm(kpiName, constraint, Collections.<EPenaltyDefinition>emptyList());
    }

    public static EGuaranteeTerm newGuaranteeTerm(
            String kpiName, String constraint, List<EPenaltyDefinition> penalties) {
        
        EGuaranteeTerm t = new EGuaranteeTerm();
        t.setName(kpiName);
        t.setKpiName(kpiName);
        t.setServiceLevel(constraint);
        t.setPolicies(
            new ArrayList<EPolicy>(Arrays.<EPolicy>asList(new EPolicy(1, new Date(0))))
        );
        t.setViolations(new ArrayList<EViolation>());
        
        EBusinessValueList businessValueList = new EBusinessValueList(0, penalties);
        t.setBusinessValueList(businessValueList);
        return t;
    }

    public static EViolation newViolation(EAgreement agreement, EGuaranteeTerm term, EPolicy policy) {
        
        return newViolation(agreement, term, policy, new Date());
    }
    
    public static EViolation newViolation(EAgreement agreement, EGuaranteeTerm term, EPolicy policy, Date datetime) {
        
        EViolation result = new EViolation(agreement, term, policy, "", "", "", datetime);
        return result;
    }
}
