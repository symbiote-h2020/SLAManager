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
package eu.atos.sla.modelconversion.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EPolicy;
import eu.atos.sla.parser.DurationUtils;
import eu.atos.sla.parser.data.wsag.CustomServiceLevel;
import eu.atos.sla.parser.data.wsag.ServiceLevelObjective;
import eu.atos.sla.parser.data.wsag.custom.SimpleCustomServiceLevel;
import eu.atos.sla.parser.data.wsag.custom.SimpleCustomServiceLevel.ViolationWindow;

public class SimpleServiceLevelConverter implements ServiceLevelConverter {
    private static Logger logger = LoggerFactory.getLogger(SimpleServiceLevelConverter.class);
    
    public SimpleServiceLevelConverter() {
    }

    @Override
    public void toDataModel(ServiceLevelObjective slo, EGuaranteeTerm guaranteeTerm) {
        if (slo.getKpitarget() != null) {
            if (slo.getKpitarget().getKpiName() != null) {
                guaranteeTerm.setKpiName(slo.getKpitarget().getKpiName());
                CustomServiceLevel cslObject = slo.getKpitarget().getCustomServiceLevel();
                SimpleCustomServiceLevel csl;
                try {
                    csl = cslObject.getAnyAsObject(SimpleCustomServiceLevel.class);
                    
                    if (fromJson(cslObject)) {
                        replaceIn(cslObject, csl);
                    }
                }
                catch (ClassCastException e) {
                    throw new UnsupportedOperationException(
                            "Only SimpleCustomServiceLevel is supported: " + e.getMessage(), e);
                }
                logger.debug("guaranteeTerm with kpiname:{} --  getCustomServiceLevel: {}", 
                        slo.getKpitarget().getKpiName(), csl);
                if (csl != null) {
                    logger.debug("CustomServiceLevel not null"); 

                    guaranteeTerm.setServiceLevel(csl.getConstraint());
                    guaranteeTerm.setPolicies(toPolicies(csl.getViolationWindows()));
                }
            }
        }
        
    }

    private void replaceIn(CustomServiceLevel cslObject, SimpleCustomServiceLevel csl) {
        
        cslObject.getAny().clear();
        cslObject.getAny().add(csl);
    }

    private boolean fromJson(CustomServiceLevel cslObject) {
        
        return (cslObject.getAny().size() > 0) && (cslObject.getAny().get(0) instanceof Map);
    }

    private static List<EPolicy> toPolicies(List<ViolationWindow> windows) {
        List<EPolicy> result = new ArrayList<EPolicy>();
        for (ViolationWindow w : windows) {
            EPolicy p = new EPolicy(w.getCount(), DurationUtils.durationToDate(w.getInterval()));
            result.add(p);
        }
        return result;
    }

}
