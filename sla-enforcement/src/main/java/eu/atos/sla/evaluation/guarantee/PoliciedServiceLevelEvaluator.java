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
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.common.collections.CompositeList;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EBreach;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EPolicy;
import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.evaluation.constraint.IConstraintEvaluator;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * Implements a ServiceLevelEvaluator that takes into account Policies. 
 * 
 * <p>In a policy, a non-fulfilled service level by a metric is considered a breach. A policy specifies how many
 * breaches in a interval of time must occur to raise a violation.</p>
 * 
 * <p>If no policies are defined for the guarantee term, each breach is a violation. Otherwise, only a violation
 * will be raised (if applicable) in each execution. Therefore, to avoid having breaches not considered as 
 * violations, the policy interval should be greater than the evaluation interval.</p>
 * 
 * <p>The breaches management (load, store) is totally performed in this class, and therefore, can be considered 
 * as a side effect. The advantage is that this way, the interface for upper levels is cleaner 
 * (GuaranteeTermEvaluator and AgreementEvaluator do not know about breaches).</p>
 * 
 * Usage:
 * <pre>
 * PoliciedServiceLevelEvaluator pe = new PoliciedServiceLevelEvaluator();
 * pe.setConstraintEvaluator(...);
 * pe.setBreachRepository(...);
 * 
 * pe.evaluate(...):
 * </pre>
 * 
 * @see IBreachRepository
 * @see IConstraintEvaluator
 * @author rsosa
 *
 */
public class PoliciedServiceLevelEvaluator implements IServiceLevelEvaluator {

    private static Logger logger = LoggerFactory.getLogger(PoliciedServiceLevelEvaluator.class);

    private static EPolicy defaultPolicy = new EPolicy(1, new Date(0));
    private IConstraintEvaluator constraintEval;
    private IBreachRepository breachRepository;
    private IViolationRepository violationRepository;
    private PoliciedServiceLevelEvaluator.BreachesFromMetricsBuilder breachesFromMetricsBuilder = new BreachesFromMetricsBuilder();
    private ViolationsFromBreachesBuilder violationsFromBreachesBuilder = new ViolationsFromBreachesBuilder();

    @Override
    public List<EViolation> evaluate(
            EAgreement agreement, EGuaranteeTerm term, List<IMonitoringMetric> metrics, Date now) {
        logger.debug("evaluate(agreement={}, term={}, servicelevel={})", 
                agreement.getAgreementId(), term.getKpiName(), term.getServiceLevel());

        /*
         * throws NullPointerException if not property initialized 
         */
        checkInitialized();
        
        List<EViolation> newViolations = new ArrayList<EViolation>();
        String kpiName = term.getKpiName();
        String constraint = term.getServiceLevel();

        /*
         * Calculate with new metrics are breaches
         */
        List<IMonitoringMetric> newBreachMetrics = constraintEval.evaluate(kpiName, constraint, metrics);
        logger.debug("Found {} breaches in new metrics", newBreachMetrics.size());
        
        List<EPolicy> policies = getPoliciesOrDefault(term);
        boolean withPolicies = !isDefaultPolicy(policies);
        
        List<EBreach> newBreaches = null;           /* only to use with policies */
        if (withPolicies) {
            newBreaches = breachesFromMetricsBuilder.build(newBreachMetrics, agreement, kpiName);
        }
        
        /*
         * Evaluate each policy
         */
        for (EPolicy policy : policies) {
            Date breachesBegin = calcBreachesBegin(agreement, now, kpiName, policy, metrics);

            logger.debug("Evaluating policy({},{}s) in interval({}, {})", 
                    policy.getCount(), policy.getTimeInterval().getTime() / 1000, breachesBegin, now);
            
            List<EBreach> oldBreaches;
            
            if (withPolicies) {
                oldBreaches = breachRepository.getBreachesByTimeRange(agreement, kpiName, breachesBegin, now);
                logger.debug("Found {} breaches", oldBreaches.size());
                
                List<EBreach> breaches = new CompositeList<EBreach>(
                        oldBreaches, newBreaches);
                if (evaluatePolicy(policy, oldBreaches, newBreaches)) {
                    List<EViolation> policyViolations = violationsFromBreachesBuilder.build(
                            agreement, term, kpiName, breaches, policy);

                    newViolations.addAll(policyViolations);
                    logger.debug("Violation raised");
                }
            } 
            else {
                oldBreaches = Collections.emptyList();
                for (IMonitoringMetric breach : newBreachMetrics) {
                    EViolation violation = newViolation(agreement, term, kpiName, breach);
                    newViolations.add(violation);
                    logger.debug("Violation raised");
                }
            }
        }
        if (withPolicies) {
            saveBreaches(newBreaches);
        }
        return newViolations;
    }

    private Date calcBreachesBegin(
            EAgreement agreement, Date now, String kpiName, EPolicy policy, List<IMonitoringMetric> metrics) {
        
        Date breachesBegin = (metrics.size() > 0)?
                substract(metrics.get(0).getDate(), policy.getTimeInterval())
                : substract(now, policy.getTimeInterval());
        Date lastViolation = violationRepository.getLastViolationDate(agreement, kpiName);
        if (lastViolation != null && breachesBegin.compareTo(lastViolation) < 0) {
            breachesBegin = lastViolation;
        }
        return breachesBegin;
    }

    private static Date substract(Date d1, Date d2) {
        
        return new Date(d1.getTime() - d2.getTime());
    }
    
    private void checkInitialized() {
        
        if (breachRepository == null) {
            throw new NullPointerException("breachRepository is not set");
        }
        if (constraintEval == null) {
            throw new NullPointerException("constraintEval is not set");
        }
    }
    
    private void saveBreaches(List<EBreach> breaches) {
        
        breachRepository.saveBreaches(breaches);
    }

    /**
     * Builds a Violation from metric breach (for the case when the term does not have policies)
     */
    private EViolation newViolation(final EAgreement agreement, final EGuaranteeTerm term,
            final String kpiName, IMonitoringMetric breach) {
        
        String actualValue = breach.getMetricValue();
        String expectedValue = null;
        
        /*
         * The policy is null, as the term has the default policy.
         */
        EViolation v = newViolation(
                agreement, term, null, kpiName, actualValue, expectedValue, breach.getDate());
        return v;
    }
    
    private EViolation newViolation(final EAgreement contract, final EGuaranteeTerm term, 
            final EPolicy policy, final String kpiName, final String actualValue, 
            final String expectedValue, final Date timestamp) {

        EViolation result = new EViolation();
        result.setUuid(UUID.randomUUID().toString());
        result.setContractUuid(contract.getAgreementId());
        result.setKpiName(kpiName);
        result.setDatetime(timestamp);
        result.setExpectedValue(expectedValue);
        result.setActualValue(actualValue);
        result.setServiceName(term.getServiceName());
        result.setServiceScope(term.getServiceScope());
        result.setContractUuid(contract.getAgreementId());
        result.setPolicy(policy);
        
        return result;
    }
    
    private boolean evaluatePolicy(
            EPolicy policy, 
            List<EBreach> oldBreaches, 
            List<EBreach> newBreaches) {
        
        return oldBreaches.size() + newBreaches.size() >= policy.getCount();
    }
    
    /**
     * Return policies of the term if any, or the default policy.
     */
    private List<EPolicy> getPoliciesOrDefault(final EGuaranteeTerm term) {
        
        if (term.getPolicies() != null && term.getPolicies().size() > 0) {
            return term.getPolicies();
        }
        
        return Collections.singletonList(defaultPolicy);
    }
    
    /**
     * Checks if a list of breaches is only the default policy.
     */
    private boolean isDefaultPolicy(List<EPolicy> policies) {
        
        return policies.size() == 1 && isDefaultPolicy(policies.get(0));
    }
    
    private boolean isDefaultPolicy(EPolicy policy) {
        return policy.getCount() == 1;
    }
    
    public IConstraintEvaluator getConstraintEvaluator() {
        return constraintEval;
    }

    public void setConstraintEvaluator(IConstraintEvaluator constraintEval) {
        this.constraintEval = constraintEval;
    }

    public IBreachRepository getBreachRepository() {
        return breachRepository;
    }

    public void setBreachRepository(IBreachRepository breachRepository) {
        this.breachRepository = breachRepository;
    }

    public IViolationRepository getViolationRepository() {
        return violationRepository;
    }

    public void setViolationRepository(IViolationRepository violationRepository) {
        this.violationRepository = violationRepository;
    }

    /**
     * Constructs a list of Breach entities from a list of metrics that are considered breaches.
     */
    public static class BreachesFromMetricsBuilder {
        
        public List<EBreach> build(
                final List<IMonitoringMetric> metrics, 
                final EAgreement agreement, 
                final String kpiName) {
            
            List<EBreach> result = new ArrayList<EBreach>();
            for (IMonitoringMetric metric : metrics) {
                
                result.add(newBreach(agreement, metric, kpiName));
            }
            return result;
        }
        
        private EBreach newBreach(
                final EAgreement contract, 
                final IMonitoringMetric metric, 
                final String kpiName) {
            
            EBreach breach = new EBreach();
            breach.setDatetime(metric.getDate());
            breach.setKpiName(kpiName);
            breach.setValue(metric.getMetricValue());
            breach.setAgreementUuid(contract.getAgreementId());
            
            return breach;
        }
    }
    
    /**
     * Calculates the actual value of a breach. This value is not expected to be read by a computer, but by a human,
     * as it can be truncated.
     * 
     * This builder builds a comma separated list of the first three breach values.
     */
    public static class ActualValueBuilder {
        private static final int MAX_ACTUAL_VALUES = 3;
        private PoliciedServiceLevelEvaluator.BreachesFromMetricsBuilder breachesFromMetricsBuilder = new BreachesFromMetricsBuilder();
        
        public String fromBreaches(final List<EBreach> breaches) {
            
            StringBuilder str = new StringBuilder();
            String sep = "";
            for (int i = 0; i < breaches.size() && i < MAX_ACTUAL_VALUES; i++) {
                EBreach breach = breaches.get(i);
                str.append(sep);
                str.append(breach.getValue());
                
                sep = ",";
            }
            if (breaches.size() > MAX_ACTUAL_VALUES) {
                str.append("...");
            }
            return str.toString();
        }
        
        public String fromMetrics(
                final List<IMonitoringMetric> breachMetrics, EAgreement agreement, String kpiName) {

            List<EBreach> breaches = breachesFromMetricsBuilder.build(breachMetrics, agreement, kpiName);
            
            String result = fromBreaches(breaches);
            
            return result;
        }
    }
    
    /**
     * Builds violations from a list of breaches (for the case when the term has policies)
     * 
     * The build method constructs a violation every policy.getCount() breaches if the gap between breaches
     * are less or equal than the window interval.
     * 
     * Each violation have date and actualValue of the last breach in the series.
     */
    public static class ViolationsFromBreachesBuilder {
        public List<EViolation> build(EAgreement agreement, EGuaranteeTerm term, String kpiName, 
                List<EBreach> allBreaches, EPolicy policy) {
            
            List<EViolation> result = new ArrayList<EViolation>();
           
            int index = 0;
            int count = policy.getCount();
            while (index < allBreaches.size()) {
                if (isViolation(allBreaches, policy, index)) {
                    
                    List<EBreach> vBreaches = new ArrayList<EBreach>(allBreaches.subList(index, index + count));
                    EBreach lastBreach = allBreaches.get(index + count - 1);
                    
                    EViolation v = new EViolation(
                            agreement, term, policy, kpiName, lastBreach.getValue(), null, lastBreach.getDatetime());
                    v.setBreaches(vBreaches);
                    
                    result.add(v);
                    index += index + count;
                }
                else {
                    index++;
                }
            }
            return result;
        }
        
        /**
         * Check that the n breaches starting from <code>firstBreachIndex</code> are inside the interval time
         * of the window.
         */
        private boolean isViolation(List<EBreach> breaches, EPolicy policy, int firstBreachIndex) {
            
            int lastBreachIndex = firstBreachIndex + policy.getCount() - 1;
            if (lastBreachIndex >= breaches.size()) {
                
                return false;
            }
            Date firstBreachDate = breaches.get(firstBreachIndex).getDatetime();
            Date lastBreachDate = breaches.get(lastBreachIndex).getDatetime();
            
            return (substract(lastBreachDate, firstBreachDate).compareTo(policy.getTimeInterval()) <= 0);
        }
    }
    
}
