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
package eu.atos.sla.service.rest.helpers;

import static eu.atos.sla.datamodel.EGuaranteeTerm.GuaranteeTermStatusEnum.FULFILLED;
import static eu.atos.sla.datamodel.EGuaranteeTerm.GuaranteeTermStatusEnum.NON_DETERMINED;
import static eu.atos.sla.datamodel.EGuaranteeTerm.GuaranteeTermStatusEnum.VIOLATED;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EGuaranteeTerm.GuaranteeTermStatusEnum;


public class AgreementHelperETest {

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetAgreementStatus() {
        testStatus(FULFILLED,       FULFILLED);
        testStatus(NON_DETERMINED);
        testStatus(NON_DETERMINED,  FULFILLED, NON_DETERMINED);
        testStatus(NON_DETERMINED,  NON_DETERMINED, FULFILLED);
        testStatus(VIOLATED,        FULFILLED, VIOLATED);
        testStatus(VIOLATED,        NON_DETERMINED, VIOLATED);
        testStatus(VIOLATED,        VIOLATED, NON_DETERMINED);
        testStatus(VIOLATED,        VIOLATED, NON_DETERMINED, FULFILLED);
        
    }
    
    private void testStatus(GuaranteeTermStatusEnum expected, GuaranteeTermStatusEnum... termsStatus) {
        
        List<EGuaranteeTerm> terms = buildTerms(termsStatus);
                
        GuaranteeTermStatusEnum current = AgreementHelperE.AgreementStatusCalculator.getStatus(terms);
        
        assertEquals(expected, current);
    }
    
    private List<EGuaranteeTerm> buildTerms(GuaranteeTermStatusEnum... termsStatus) {
        
        List<EGuaranteeTerm> result = new ArrayList<EGuaranteeTerm>();
        for (GuaranteeTermStatusEnum status: termsStatus) {
            
            EGuaranteeTerm term = new EGuaranteeTerm();
            term.setStatus(status);
            result.add(term);
            
        }
        return result;
    }

}
