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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.ECompensation;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EViolation;

/**
 * Business evaluator that does nothing.
 * 
 * @author rsosa
 */
public class DummyBusinessValuesEvaluator implements IBusinessValuesEvaluator {

    @Override
    public List<ECompensation> evaluate(EAgreement agreement,
            EGuaranteeTerm term, List<EViolation> violations, Date now) {

        return Collections.emptyList();
    }
}