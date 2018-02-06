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

import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.parser.data.wsag.ServiceLevelObjective;

/**
 * Parses a ServiceLevelObjective, building/completing an existing EGuaranteeTerm.
 */
public interface ServiceLevelConverter {

    /**
     * Converts a ServiceLevelObjective in WSAG model to Entity model.
     * 
     * If the SLO has been parsed from JSON and contains maps and arrays instead of the expected object
     * (as happens when parsing XML), the maps and arrays need to be replaced by the converted object.
     * 
     * @param in SLO in WSAG model (may be modified)
     * @param out GT in entity model (is modified)
     */
    void toDataModel(ServiceLevelObjective in, EGuaranteeTerm out);
}
