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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IEnforcementJobDAO;
import eu.atos.sla.dao.IGuaranteeTermDAO;
import eu.atos.sla.dao.IPenaltyDAO;
import eu.atos.sla.dao.IViolationDAO;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.ECompensation;
import eu.atos.sla.datamodel.ECompensation.IReward;
import eu.atos.sla.datamodel.EEnforcementJob;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EGuaranteeTerm.GuaranteeTermStatusEnum;
import eu.atos.sla.datamodel.EPenalty;
import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.evaluation.guarantee.GuaranteeTermEvaluator.GuaranteeTermEvaluationResult;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * 
 * @author rsosa
 *
 */
@Service("EnforcementManager")
public class EnforcementService implements IEnforcementService {
    private static Logger logger = LoggerFactory.getLogger(EnforcementService.class);
    
    @Autowired
    IEnforcementJobDAO enforcementJobDAO;

    @Autowired
    IAgreementDAO agreementDAO;
    
    @Autowired
    IGuaranteeTermDAO guaranteeTermDAO;
    
    @Autowired
    IViolationDAO violationDAO;

    @Autowired
    IPenaltyDAO penaltyDAO;
    
    @Autowired
    AgreementEnforcement agreementEnforcement;
    
    
    @Override
    public EEnforcementJob getEnforcementJob(Long id) {

        return enforcementJobDAO.getById(id);
    }

    @Override
    public EEnforcementJob getEnforcementJobByAgreementId(String agreementId) {

        return enforcementJobDAO.getByAgreementId(agreementId);
    }

    @Override
    public List<EEnforcementJob> getEnforcementJobs() {

        return enforcementJobDAO.getAll();
    }

    @Override
    public EEnforcementJob createEnforcementJob(EEnforcementJob job) {

        String agreementId = job.getAgreement().getAgreementId();
        if (enforcementJobDAO.getByAgreementId(agreementId) != null) {
            throw new IllegalStateException("Agreement " + agreementId
                    + " already has EnforcementJob");
        }
        EAgreement agreement = agreementDAO.getByAgreementId(agreementId);
        if (agreement==null)
            throw new IllegalStateException("Agreement " + agreementId
                    + " doesn't exist in the database, cannot be associated to the enforcement");

        job.setAgreement(agreement);

        EEnforcementJob saved = enforcementJobDAO.save(job);

        return saved;

    }
    
    @Override
    public EEnforcementJob createEnforcementJob(String agreementId) {

        EEnforcementJob job = new EEnforcementJob();
        EAgreement agreement = new EAgreement();
        agreement.setAgreementId(agreementId);
        job.setAgreement(agreement);
        job.setEnabled(false);
        
        EEnforcementJob saved = createEnforcementJob(job);
        return saved;
    }

    @Override
    public boolean deleteEnforcementJobByAgreementId(String agreementId) {
        EEnforcementJob job = enforcementJobDAO.getByAgreementId(agreementId);

        if (job == null) {
            return false;
        }

        boolean result = enforcementJobDAO.delete((EEnforcementJob)job);

        return result;
    }

    @Override
    public boolean startEnforcement(String agreementId) {
        EEnforcementJob job = enforcementJobDAO.getByAgreementId(agreementId);

        if (job == null) {
            return false;
        }
        job.setEnabled(true);
        enforcementJobDAO.save(job);
        
        return true;
    }

    @Override
    public boolean stopEnforcement(String agreementId) {
        EEnforcementJob job = enforcementJobDAO.getByAgreementId(agreementId);
        if (job == null) {
            return false;
        }
        job.setEnabled(false);
        enforcementJobDAO.save(job);
            EAgreement agreement = agreementDAO.getByAgreementId(agreementId);
            if (agreement.getHasGTermToBeEvaluatedAtEndOfEnformcement()!= null){
                if (agreement.getHasGTermToBeEvaluatedAtEndOfEnformcement()){
                    try{
                        agreementEnforcement.enforce(agreement, job.getLastExecuted(), true);
                    }catch(Throwable t){
                        logger.error("Fatal error in stopEnforcement doing enforcement", t);
                    }

                }
            }
        return true;
    }

    @Override
    public void saveEnforcementResult(EAgreement agreement,
            Map<EGuaranteeTerm, GuaranteeTermEvaluationResult> enforcementResult) {
        
        for (EGuaranteeTerm gterm : enforcementResult.keySet()) {
            EGuaranteeTerm dbTerm = guaranteeTermDAO.getById(gterm.getId());
            GuaranteeTermEvaluationResult gttermResult = enforcementResult.get(gterm);
            
            for (EViolation violation : gttermResult.getViolations()) {
                dbTerm.getViolations().add(violation);
                violationDAO.save(violation);
            }
            
            for (ECompensation compensation : gttermResult.getCompensations()) {
                
                if (compensation instanceof EPenalty) {
                    EPenalty penalty = (EPenalty)compensation;
                    penaltyDAO.save(penalty);
                    dbTerm.getPenalties().add(penalty);
                }
                else if (compensation instanceof IReward) {
                    logger.warn("Saving a Reward is not implemented");
                }
                else {
                    throw new AssertionError("Unexpected compensation type: " + compensation.getClass().getName());
                }
            }
            
            dbTerm.setStatus( dbTerm.getViolations().size() > 0? 
                    GuaranteeTermStatusEnum.VIOLATED : GuaranteeTermStatusEnum.FULFILLED);
            guaranteeTermDAO.update(dbTerm);
        }
        
        EEnforcementJob job = getEnforcementJobByAgreementId(agreement.getAgreementId());
        job.setLastExecuted(new Date());
        if (job.getFirstExecuted() == null) job.setFirstExecuted(job.getLastExecuted());
        enforcementJobDAO.save(job);
        logger.info("saved enforcement result(agreement=" + agreement.getAgreementId()+")");
    }
    
    public void enforceReceivedMetrics(
            EAgreement agreement, String guaranteeTermName, List<IMonitoringMetric> metrics) {
        
        logger.debug(
                "enforceReceivedMetrics(agreement=" + agreement.getAgreementId() + ", gt=" + guaranteeTermName + ")");
        Map<EGuaranteeTerm, List<IMonitoringMetric>> metricsMap = 
                new HashMap<EGuaranteeTerm, List<IMonitoringMetric>>();

        for (EGuaranteeTerm gt : agreement.getGuaranteeTerms()) {
            if (guaranteeTermName.equals(gt.getName())) {
                metricsMap.put(gt, metrics);
            }
            else {
                metricsMap.put(gt, Collections.<IMonitoringMetric>emptyList());
            }
        }
        agreementEnforcement.enforce(agreement, metricsMap);
    }
    
    @Override
    public void doEnforcement(EAgreement agreement,
            Map<EGuaranteeTerm, List<IMonitoringMetric>> metrics) {

        logger.debug("enforceReceivedMetrics(" + agreement.getAgreementId() + ")");
        agreementEnforcement.enforce(agreement, metrics);
    }

    

    @Override
    public void saveCheckedGuaranteeTerm(EGuaranteeTerm term) {
        term.setLastSampledDate(new Date());
        guaranteeTermDAO.update(term);
    }
}
