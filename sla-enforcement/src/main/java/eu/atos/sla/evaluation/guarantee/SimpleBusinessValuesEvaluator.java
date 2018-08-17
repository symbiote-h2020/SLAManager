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
package eu.atos.sla.evaluation.guarantee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EBusinessValueList;
import eu.atos.sla.datamodel.ECompensation;
import eu.atos.sla.datamodel.ECompensationDefinition.CompensationKind;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EPenalty;
import eu.atos.sla.datamodel.EPenaltyDefinition;
import eu.atos.sla.datamodel.EViolation;

/**
 * BusinessValuesEvaluator that raises a penalty if the existent number of violations match the 
 * count in the penalty definition and they occur in interval time defined in the penalty definition.
 * 
 * @author rsosa
 */
public class SimpleBusinessValuesEvaluator implements IBusinessValuesEvaluator {
    private static final Date DATE0 = new Date(0);

    private static Logger logger = LoggerFactory.getLogger(SimpleBusinessValuesEvaluator.class);
    
    private IViolationRepository violationRepository;
    private ICompensationRepository compensationRepository;
    
    @Override
    public List<? extends ECompensation> evaluate(
            EAgreement agreement, EGuaranteeTerm term, List<EViolation> newViolations, Date now) {
        Objects.requireNonNull(violationRepository);
        Objects.requireNonNull(compensationRepository);
        
        logger.debug("Evaluating business for {} new violations", newViolations.size());
        List<ECompensation> result = new ArrayList<ECompensation>();
        EBusinessValueList businessValues = term.getBusinessValueList();
        if (businessValues == null) {
            /*
             * sanity check
             */
            return Collections.emptyList();
        }
        for (EPenaltyDefinition penaltyDef : businessValues.getPenalties()) {
            if (penaltyDef.getKind() != CompensationKind.CUSTOM_PENALTY) {
                continue;
            }
            
            Date lastPenalty = compensationRepository.getLastPenaltyDate(
                    agreement.getAgreementId(), penaltyDef, term.getKpiName());
            
            Date violationsBegin = new Date(now.getTime() - penaltyDef.getTimeInterval().getTime());
            
            /**
             * If found a penalty in interval [violationsBegin, now], 
             * we have to count violations in (lastPenalty, now] 
             */
            if (lastPenalty != null && lastPenalty.compareTo(violationsBegin) >= 0) {
                violationsBegin = new Date(lastPenalty.getTime() + 1000);
            }

            List<EViolation> oldViolations = 
                    violationRepository.getViolationsByTimeRange(agreement, term.getName(), violationsBegin, now);
            
            /*
             * TODO: only one penalty per def is raised on an evaluation. 
             * Also, if found 6 violations and def.count = 5, the date should be taken from 5th, not from last.
             */
            
            if (thereIsPenalty(penaltyDef, newViolations, oldViolations)) {
                
                EPenalty penalty = new EPenalty(
                        agreement.getAgreementId(),
                        getLastDate(newViolations),
                        term.getKpiName(),
                        penaltyDef, 
                        getLastViolation(newViolations, oldViolations));
                result.add(penalty);
                logger.debug("Raised {}", penalty);
            }
        }
        return result;
    }

    private Date getLastDate(List<EViolation> newViolations) {
        Date result = DATE0;
        
        for (EViolation v : newViolations) {
            
            if (v.getDatetime().compareTo(result) > 0) {
                result = v.getDatetime();
            }
        }
        return result;
    }

    private boolean thereIsPenalty(EPenaltyDefinition penaltyDef,
            List<EViolation> newViolations, List<EViolation> oldViolations) {
        return oldViolations.size() + newViolations.size() >= penaltyDef.getCount();
    }
    
    private EViolation getLastViolation(List<EViolation> violations1, List<EViolation> violations2) {
        
        if (violations1.size() > 0) {
            return violations1.get(violations1.size() - 1);
        }
        else if (violations2.size() > 0) {
            return violations2.get(violations2.size() - 1);
        }
        else {
            throw new IllegalStateException("Raising penalty with no violations");
        }
    }

    public IViolationRepository getViolationRepository() {
        return violationRepository;
    }

    public void setViolationRepository(IViolationRepository violationRepository) {
        this.violationRepository = violationRepository;
    }

    public ICompensationRepository getCompensationRepository() {
        return compensationRepository;
    }
    
    public void setCompensationRepository(ICompensationRepository compensationRepository) {
        this.compensationRepository = compensationRepository;
    }
}
