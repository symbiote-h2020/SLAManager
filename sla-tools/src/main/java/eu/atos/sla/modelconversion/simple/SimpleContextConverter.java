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
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EProvider;
import eu.atos.sla.datamodel.ETemplate;
import eu.atos.sla.datamodel.EAgreement.Context.ServiceProvider;
import eu.atos.sla.modelconversion.ModelConversionException;
import eu.atos.sla.parser.data.wsag.Context;
import eu.atos.sla.parser.data.wsag.custom.CustomContext;

public class SimpleContextConverter implements ContextConverter {
    private static Logger logger = LoggerFactory.getLogger(SimpleContextConverter.class);
    private CustomContextConverter customContextConverter = new CustomContextConverter();
    
    @Override
    public void toDataModel(Context context, EAgreement agreement) {
        
        parseSpecExpression(context, agreement);
        parseDomainExpression(context, agreement);
    }

    /**
     * Parses the domain expression (xs:any) of the Context.
     * 
     * Each element of xs:any must be one of the <code>allowedClasses</code>. Not recognized elements will be
     * removed from the xs:any list.
     */
    private void parseDomainExpression(Context context, EAgreement agreement) {
        
        /*
         * TODO: Allow personalization of allowedClasses
         */
        List<Class<?>> allowedClasses = Collections.<Class<?>>singletonList(CustomContext.class);
        List<Object> replacement = new ArrayList<>();
        for (Class<?> cls : allowedClasses) {
            
            try {
            
                Object o = context.getAnyAsObject(cls);
                replacement.add(o);

                if (o instanceof CustomContext) {
                    customContextConverter.toDataModel(CustomContext.class.cast(o), agreement);
                }
            }
            catch (ClassCastException e) {
                /* 
                 * try with next class
                 */
            }
        }
        context.getAny().clear();
        context.getAny().addAll(replacement);
    }

    private void parseSpecExpression(Context context, EAgreement agreement) {
        try {
            ServiceProvider ctxProvider = ServiceProvider.fromString(context.getServiceProvider());

            switch (ctxProvider) {
            case AGREEMENT_RESPONDER:
                setProviderAndConsumer(agreement,
                        context.getAgreementResponder(),
                        context.getAgreementInitiator());
                break;
            case AGREEMENT_INITIATOR:
                setProviderAndConsumer(agreement,
                        context.getAgreementInitiator(),
                        context.getAgreementResponder());
                break;
            }
        } catch (IllegalArgumentException e) {
            throw new ModelConversionException("The Context/ServiceProvider field must match with the word "+ServiceProvider.AGREEMENT_RESPONDER+ " or "+ServiceProvider.AGREEMENT_INITIATOR);
        }

        if (context.getTemplateId() != null) {
            ETemplate template = new ETemplate();
            template.setUuid(context.getTemplateId());
            agreement.setTemplate(template);
        }else{
            throw new ModelConversionException("Template field is mandatory");
            
        }
        
        if (context.getService() != null) {
            
            agreement.setServiceId(context.getService());
        }else{
            throw new ModelConversionException("Service is null, field must be informed");
        }

        if (context.getExpirationTime() != null) {

            agreement.setExpirationDate(context.getExpirationTime());
        }
    }

    private void setProviderAndConsumer(EAgreement agreement, String provider, String consumer) {
        logger.debug("setProviderAndConsumer provider:{} - consumer:{}", provider, consumer);

        if (consumer != null) {
            agreement.setConsumer(consumer);
        }
        if (provider != null) {
            EProvider providerObj = new EProvider();
            providerObj.setUuid(provider);
            agreement.setProvider(providerObj);
        }
    }

    public static class CustomContextConverter {

        public void toDataModel(CustomContext in, EAgreement out) {

            if (in.getCreationTime() != null) {
                out.setCreationDate(in.getCreationTime());
            }
        }
    }
}
