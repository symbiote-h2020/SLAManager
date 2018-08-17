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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.ECompensation;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EPenalty;
import eu.atos.sla.datamodel.EPenaltyDefinition;
import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.enforcement.TestAgreementFactory;

public class SimpleBusinessValuesEvaluatorTest {

    private static final Date _0 = new Date(0);
    private static final Date _2 = new Date(2000);
    private static final Date _3 = new Date(3000);
    private static final Date _5 = new Date(5000);
    
//  private final static Date SECOND = new Date(1000);
//  private final static Date MINUTE = new Date(60 * SECOND.getTime());
//  private final static Date HOUR = new Date(60 * MINUTE.getTime());

    private int[] times = new int[] {
            0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0
    };
    private EViolation[] violations;
    EAgreement contract;
    private DummyViolationRepository repository;
    
    public SimpleBusinessValuesEvaluatorTest() {
    }

    @Before
    public void setUp() throws Exception {
        
        String kpiName = "LATENCY";
        String constraint = kpiName + " LT 100";

        contract = TestAgreementFactory.newAgreement(
            Arrays.asList(
                TestAgreementFactory.newGuaranteeTerm(
                    kpiName, 
                    constraint,
                    Arrays.<EPenaltyDefinition>asList(
                        new EPenaltyDefinition(1, _0, "discount", "euro", "10", "P1D"),
                        new EPenaltyDefinition(2, _2, "terminate", "", "", ""),
                        new EPenaltyDefinition(2, _3, "discount", "euro", "10", ""),
                        new EPenaltyDefinition(3, _3, "discount", "euro", "10", ""),
                        new EPenaltyDefinition(3, _5, "discount", "euro", "10", "")
                    )
                )
            )
        );

        violations = new EViolation[times.length];
        for (int i = 0; i < violations.length; i++) {
            if (times[i] != 0) {
                EViolation violation = newViolation(i);
                violations[i] = violation;
            }
        }
    }

    private EViolation newViolation(int time) {
        Date datetime = new Date(1000 * time);
        EViolation violation = new EViolation(contract, contract.getGuaranteeTerms().get(0), null, "", "", "", datetime);
        return violation;
    }
    
    @Test
    public void testEvaluate() {
        
        SimpleBusinessValuesEvaluator bvEval = new SimpleBusinessValuesEvaluator();
        repository = new DummyViolationRepository(Arrays.asList(violations));
        bvEval.setViolationRepository(repository);
        bvEval.setCompensationRepository(repository);
        EGuaranteeTerm term = contract.getGuaranteeTerms().get(0);
        
        List<? extends ECompensation> compensations;
        
        for (int i = 0; i < times.length; i++) {
            Date now = new Date(i * 1000);
            
            StringBuilder out = new StringBuilder(String.format("i = %d", i));
            
            List<EViolation> newViolations = (times[i] != 0)?
                Collections.singletonList(TestAgreementFactory.newViolation(contract, term, null, now)) :
                Collections.<EViolation>emptyList();

            compensations = bvEval.evaluate(
                contract, 
                term, 
                newViolations,
                now
            );
            for (EPenaltyDefinition def : contract.getGuaranteeTerms().get(0).getBusinessValueList().getPenalties()) {
                int count = def.getCount();
                int step = (int)def.getTimeInterval().getTime() / 1000;

                boolean actualPenalty = foundPenaltyDefinition(def, compensations);
                if (actualPenalty) {
                    out.append(String.format("\tP(%d,%d)", count, step));
                }
            }
            System.out.println(out);

            for (EPenaltyDefinition def : contract.getGuaranteeTerms().get(0).getBusinessValueList().getPenalties()) {
                int step = (int)def.getTimeInterval().getTime() / 1000;
                
                boolean expectedPenalty = isExpectedPenalty(i, def);
                boolean actualPenalty = foundPenaltyDefinition(def, compensations);

                if (actualPenalty != expectedPenalty) {
                    System.out.println(String.format("i = %d, %s found P(%d, %d)", 
                            i, expectedPenalty? "not" : "", def.getCount(), step));
                }
                assertEquals(expectedPenalty, actualPenalty);
            }
            repository.savePenalties((List<ECompensation>)compensations);
        }
    }
    
    private boolean isExpectedPenalty(int i, EPenaltyDefinition def) {
        int count = def.getCount();
        int step = (int)def.getTimeInterval().getTime() / 1000;
        
        Date lastPenalty = repository.getLastPenaltyDate(def);
        int t_lastPenalty = (int) lastPenalty.getTime() / 1000;
        /*
         * begin is from the last penalty (not counting last penalty date; that's why t_lastPenalty+1),
         * or i - step
         */
        int begin = (t_lastPenalty >= i - step? t_lastPenalty + 1 : i - step);
        return sum(times, begin, i) >= count;
    }
    
    private int sum(int[] arr, int begin, int end) {
        int result = 0;
        if (begin < 0) {
            begin = 0;
        }
        for (int i = begin; i <= end; i++) {
            result += arr[i];
        }
        return result;
    }
    
    private boolean foundPenaltyDefinition(EPenaltyDefinition def, List<? extends ECompensation> compensations) {
        for (ECompensation c : compensations) {
            if (c instanceof EPenalty) {
                EPenalty p = (EPenalty) c;
                if (p.getDefinition().equals(def)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static class DummyViolationRepository implements IViolationRepository, ICompensationRepository {
        
        List<EViolation> violations = new ArrayList<EViolation>();
        List<ECompensation> penalties = new ArrayList<>();

        public DummyViolationRepository() {
        }

        public DummyViolationRepository(List<EViolation> violations) {
            this.violations.addAll(violations);
        }

        @Override
        public List<EViolation> getViolationsByTimeRange(EAgreement agreement,
                String guaranteeTermName, Date begin, Date end) {

            List<EViolation> result = new ArrayList<EViolation>();
            for (EViolation violation: violations) {
                if (violation == null) {
                    continue;
                }
                Date date = violation.getDatetime();
                if (begin.compareTo(date) <= 0 && end.compareTo(date) > 0) {
                    result.add(violation);
                }
            }
            return result;
        }

        @Override
        public Date getLastViolationDate(EAgreement agreement, String kpiName) {
            Date maxDate = new Date(0);
            for (EViolation violation: violations) {
                if (violation == null) {
                    continue;
                }
                Date violationDate = violation.getDatetime();
                if (maxDate.compareTo(violationDate) < 0) {
                    maxDate = violationDate;
                }
            }
            return maxDate.getTime() == 0? null : maxDate;
        }
        
        public Date getLastPenaltyDate(EPenaltyDefinition def) {
            Date result = _0;
            for (ECompensation c : penalties) {
                EPenalty p = EPenalty.class.cast(c);
                if (_0.compareTo(p.getDatetime()) < 0 && sameDefinition(def, p.getDefinition())) {
                    result = c.getDatetime();
                }
            }
            return result;
        }

        @Override
        public Date getLastPenaltyDate(String agreementId, EPenaltyDefinition def, String kpiName) {
            return getLastPenaltyDate(def);
        }
        
        private boolean sameDefinition(EPenaltyDefinition def1, EPenaltyDefinition def2) {
            return def1.getCount() == def2.getCount() && def1.getTimeInterval() == def2.getTimeInterval();
        }

        public void savePenalties(List<ECompensation> p) {
            penalties.addAll(p);
        }

    }
}
