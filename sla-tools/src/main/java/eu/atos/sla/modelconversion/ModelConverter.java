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
package eu.atos.sla.modelconversion;

import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EEnforcementJob;
import eu.atos.sla.datamodel.EPenalty;
import eu.atos.sla.datamodel.EProvider;
import eu.atos.sla.datamodel.ETemplate;
import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.parser.data.EnforcementJob;
import eu.atos.sla.parser.data.Penalty;
import eu.atos.sla.parser.data.Provider;
import eu.atos.sla.parser.data.Violation;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.Context;
import eu.atos.sla.parser.data.wsag.Template;

/**
 * A ModelConverter translates objects between data model and service model and viceversa.
 * 
 * Regarding templates and agreements, it is intended to translate between WSAG and data model, but nothing prevents
 * to use a ModelConveter that translates from a different service model (e.g. another SLA standard).
 * 
 * @see SimpleModelConverter
 */
public interface ModelConverter {

    public EAgreement getAgreementFromAgreementXML(Agreement agreementXML, String payload) throws ModelConversionException;

    public ETemplate getTemplateFromTemplateXML(Template templateXML, String payload) throws ModelConversionException;

    public EEnforcementJob getEnforcementJobFromEnforcementJobXML(EnforcementJob enforcementJobXML) throws ModelConversionException;

    public Context getContextFromAgreement(EAgreement agreement) throws ModelConversionException;
    
    public EProvider getProviderFromProviderXML(Provider providerXML);

    public EnforcementJob getEnforcementJobXML(EEnforcementJob enforcementJob);

    public Provider getProviderXML(EProvider provider);
    
    public Violation getViolationXML(EViolation violation);
    
    public Penalty getPenaltyXML(EPenalty penalty);

}