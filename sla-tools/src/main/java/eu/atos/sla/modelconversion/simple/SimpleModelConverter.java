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
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.atos.sla.XmlParser;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EAgreement.Context.ServiceProvider;
import eu.atos.sla.modelconversion.AgreementEntityToWsagAdapter;
import eu.atos.sla.modelconversion.ModelConversionException;
import eu.atos.sla.modelconversion.ModelConverter;
import eu.atos.sla.modelconversion.simple.QualifyingConditionParser;
import eu.atos.sla.datamodel.EEnforcementJob;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EPenalty;
import eu.atos.sla.datamodel.EProvider;
import eu.atos.sla.datamodel.EServiceProperties;
import eu.atos.sla.datamodel.ETemplate;
import eu.atos.sla.datamodel.EVariable;
import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.parser.data.EnforcementJob;
import eu.atos.sla.parser.data.Penalty;
import eu.atos.sla.parser.data.Provider;
import eu.atos.sla.parser.data.Violation;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.Context;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.atos.sla.parser.data.wsag.ServiceLevelObjective;
import eu.atos.sla.parser.data.wsag.ServiceProperties;
import eu.atos.sla.parser.data.wsag.ServiceScope;
import eu.atos.sla.parser.data.wsag.Template;
import eu.atos.sla.parser.data.wsag.Variable;

/**
 * Default ModelConverter in SLA Core. Translates between WSAG Documents and data model, and allows plugging
 * of additional converters to parse custom domain expressions (SLOs, Service Description Terms...).
 */
public class SimpleModelConverter implements ModelConverter {
    
    private BusinessValueListParser businessValueListParser;
    
    private ServiceLevelConverter serviceLevelConverter;

    private ContextConverter contextConverter;
 
    private static Logger logger = LoggerFactory.getLogger(SimpleModelConverter.class);
    
    private XmlParser<Agreement> agreementXmlParser;
    private XmlParser<Template> templateXmlParser;

    private final AgreementEntityToWsagAdapter agreementAdapter;
    
    public SimpleModelConverter(XmlParser<Agreement> agreementXmlParser, XmlParser<Template> templateXmlParser) {
        if (agreementXmlParser == null) {
            throw new NullPointerException("agreementXmlParser cannot be null");
        }
        if (templateXmlParser == null) {
            throw new NullPointerException("templateXmlParser cannot be null");
        }
        this.agreementXmlParser = agreementXmlParser;
        this.templateXmlParser = templateXmlParser;
        this.agreementAdapter = new AgreementEntityToWsagAdapter(agreementXmlParser);
    }
    
    @Override
    public EAgreement getAgreementFromAgreementXML(
            Agreement agreementXML,
            String payload) throws ModelConversionException {

        EAgreement agreement = new EAgreement();

        // AgreementId
        if (agreementXML.getAgreementId() != null) {
            agreement.setAgreementId(agreementXML.getAgreementId());
        }

        // Context
        Context context = (Context)agreementXML.getContext();
        contextConverter.toDataModel(context, agreement);

        // ServiceProperties

        List<EServiceProperties> servicePropertiesList = new ArrayList<EServiceProperties>();

        List<ServiceProperties> servicePropertiesListXML = agreementXML
                .getTerms().getAllTerms().getServiceProperties();
        if (servicePropertiesListXML == null) {
            servicePropertiesListXML = Collections.<ServiceProperties>emptyList();
        }

        for (ServiceProperties servicePropertiesXML : servicePropertiesListXML) {

            EServiceProperties serviceProperties = new EServiceProperties();

            if (servicePropertiesXML.getName() != null) {
                serviceProperties.setName(servicePropertiesXML.getName());
            }

            if (servicePropertiesXML.getServiceName() != null) {
                serviceProperties.setServiceName(servicePropertiesXML.getServiceName());
            }

            if (servicePropertiesXML != null) {
                serviceProperties.setServiceName(servicePropertiesXML.getServiceName());
            }

            // VariableSet
            if (servicePropertiesXML.getVariableSet() != null) {

                List<EVariable> variables = new ArrayList<EVariable>();
                List<Variable> variablesXML = servicePropertiesXML.getVariableSet().getVariables();
                if (variablesXML != null) {

                    for (Variable variableXML : variablesXML) {

                        EVariable variable = new EVariable();
                        logger.debug("Variable with name:{} -  location:{} - metric:{}", 
                                variableXML.getName(), variableXML.getLocation(), variableXML.getMetric());
                        if (variableXML.getLocation() != null) {
                            variable.setLocation(variableXML.getLocation());
                        }
                        if (variableXML.getMetric() != null) {
                            variable.setMetric(variableXML.getMetric());
                        }
                        if (variableXML.getName() != null) {
                            variable.setName(variableXML.getName());
                        }

                        variables.add(variable);

                    }
                    serviceProperties.setVariableSet(variables);
                }
            }
            servicePropertiesList.add(serviceProperties);
        }

        agreement.setServiceProperties(servicePropertiesList);
        agreement.setName(agreementXML.getName());

        // GuaranteeTerms
        List<EGuaranteeTerm> guaranteeTerms = new ArrayList<EGuaranteeTerm>();

        List<GuaranteeTerm> guaranteeTermsXML = agreementXML.getTerms()
                .getAllTerms().getGuaranteeTerms();

        if (guaranteeTermsXML == null) {
            guaranteeTermsXML = Collections.<GuaranteeTerm> emptyList();
        }

        for (GuaranteeTerm guaranteeTermXML : guaranteeTermsXML) {

            EGuaranteeTerm guaranteeTerm = new EGuaranteeTerm();

            if (guaranteeTermXML.getName() != null) {
                guaranteeTerm.setName(guaranteeTermXML.getName());
            }

            ServiceScope scope = guaranteeTermXML.getServiceScope();
            if (scope != null) {
                logger.debug("guaranteeTerm with name:{} -  servicescopeName:{} - servicescopeValue:{}", 
                        guaranteeTermXML.getName(), scope.getServiceName(), scope.getValue());
                guaranteeTerm.setServiceScope(scope.getValue());
                guaranteeTerm.setServiceName( scope.getServiceName());
            }else
                logger.debug("guaranteeTerm with name:{} - serviceScope is null", guaranteeTermXML.getName() );

            // qualifying condition
            if (guaranteeTermXML.getQualifyingCondition()!= null){
                logger.debug("qualifying condition informed with:{}", guaranteeTermXML.getQualifyingCondition());
                String qc = guaranteeTermXML.getQualifyingCondition();
                if (qc != null) {
                    QualifyingConditionParser.Result parsedQc = QualifyingConditionParser.parse(qc);
                    guaranteeTerm.setSamplingPeriodFactor(parsedQc.getSamplingPeriodFactor());
                    if (parsedQc.getSamplingPeriodFactor() == EGuaranteeTerm.ENFORCED_AT_END) {
                        agreement.setHasGTermToBeEvaluatedAtEndOfEnformcement(true);
                    }
                }
            }
            /*
             * Parse SLO and BusinessValues
             */
            ServiceLevelObjective slo = guaranteeTermXML.getServiceLevelObjective();
            serviceLevelConverter.toDataModel(slo, guaranteeTerm);
            guaranteeTerm.setBusinessValueList(businessValueListParser.parse(guaranteeTermXML));
            
            guaranteeTerms.add(guaranteeTerm);
        }

        agreement.setGuaranteeTerms(guaranteeTerms);

        // Text
        try {
            String serialized = agreementXmlParser.toString(agreementXML);
            agreement.setText(serialized);
        } catch (JAXBException e) {
            throw new ModelConversionException(e.getMessage(), e);
        }

        return agreement;
    }

    // we retrieve the providerUUID from the template and get the provider object from the database 
    private EProvider getProviderFromTemplate(Template templateXML) throws ModelConversionException {
        
        Context context = templateXML.getContext();
        
        String provider = null;
        try {
            ServiceProvider ctxProvider = ServiceProvider.fromString(context.getServiceProvider());
             
            switch (ctxProvider) {
            case AGREEMENT_RESPONDER:
                provider= context.getAgreementResponder();
                break;
            case AGREEMENT_INITIATOR:
                provider= context.getAgreementInitiator();
                break;
            }
        } catch (IllegalArgumentException e) {
            throw new ModelConversionException("The Context/ServiceProvider field must match with the word "+ServiceProvider.AGREEMENT_RESPONDER+ " or "+ServiceProvider.AGREEMENT_INITIATOR);
        }
        
        EProvider providerObj = null;
        if (provider != null) {
            providerObj = new EProvider();
            providerObj.setUuid(provider);
        }
        return providerObj;
    }
    
    @Override
    public ETemplate getTemplateFromTemplateXML(Template templateXML, String payload) throws ModelConversionException{
        ETemplate template = new ETemplate();
        if (templateXML.getTemplateId() != null) {
            logger.debug("TemplateId at header will be used:{}", templateXML.getTemplateId());
            template.setUuid(templateXML.getTemplateId());
        } else {
            // uuid
            if (templateXML.getContext().getTemplateId() != null) {
                logger.debug("TemplateId in context will be used:{}", templateXML.getTemplateId());
                template.setUuid(templateXML.getContext().getTemplateId());
            }else{
                String templateId = UUID.randomUUID().toString();
                template.setUuid(templateId);
            }
        }
        
        if (templateXML.getContext().getService()!=null){
            template.setServiceId(templateXML.getContext().getService());
        }else{
            logger.error("Service is null, field must be informed");
            throw new ModelConversionException("Service is null, field must be informed");
        }
        
        /*
         * This is for Json templates: convert maps and arrays inside CustomServiceLevel for appropriate class
         * in order to be able to serialize to xml later.
         */
        List<GuaranteeTerm> guaranteeTermsXml = templateXML.getTerms().getAllTerms().getGuaranteeTerms();
        if (guaranteeTermsXml == null) {
            guaranteeTermsXml = Collections.emptyList();
        }
        for (GuaranteeTerm gt : guaranteeTermsXml) {
            
            ServiceLevelObjective slo = gt.getServiceLevelObjective();
            serviceLevelConverter.toDataModel(slo, new EGuaranteeTerm());
        }
        // Text
        try {
            String serialized = templateXmlParser.toString(templateXML);
            template.setText(serialized);
        } catch (JAXBException e) {
            throw new ModelConversionException(e.getMessage(), e);
        }
        // Name
        template.setName(templateXML.getName());
        template.setProvider(getProviderFromTemplate(templateXML));
        return template;
    }

    
    @Override
    public Context getContextFromAgreement(EAgreement agreement) throws ModelConversionException {
        Context context = null;

        if (agreement.getText() != null) {

            Agreement agreementXML = agreementAdapter.apply(agreement);
            context = agreementXML.getContext();
        }
        else {
            throw new ModelConversionException("Serialized agreement is null, could not be parsed");
        }
        return context;
    }

    @Override
    public EEnforcementJob getEnforcementJobFromEnforcementJobXML(
            EnforcementJob enforcementJobXML) throws ModelConversionException{

        EEnforcementJob enforcementJob = new EEnforcementJob();
        EAgreement agreement = null;

        if (enforcementJobXML.getAgreementId() != null) {

            agreement = new EAgreement(enforcementJobXML.getAgreementId());
            enforcementJob.setAgreement(agreement);
        } else
            throw new ModelConversionException("AgreementId is null, field must be informed");


        enforcementJob.setEnabled(enforcementJobXML.getEnabled());

        if (enforcementJobXML.getLastExecuted() != null) {
            enforcementJob.setLastExecuted(enforcementJobXML.getLastExecuted());
        }

        return enforcementJob;
    }

    @Override
    public EProvider getProviderFromProviderXML(Provider providerXML) {

        EProvider provider = new EProvider();

        if (providerXML.getUuid() != null) {

            provider.setUuid(providerXML.getUuid());
        } else {

            provider.setUuid(UUID.randomUUID().toString());
        }

        if (providerXML.getName() != null) {
            provider.setName(providerXML.getName());
        }

        return provider;
    }


    @Override
    public Provider getProviderXML(EProvider provider) {

        return new Provider(provider);
    }

    @Override
    public Violation getViolationXML(EViolation violation) {

        return new Violation(violation);
    }

    @Override
    public EnforcementJob getEnforcementJobXML(
            EEnforcementJob enforcementJob) {

        return new EnforcementJob(enforcementJob);
    }
    
    @Override
    public Penalty getPenaltyXML(EPenalty penalty) {
        
        return new Penalty(penalty);
    }

    public void setBusinessValueListParser(
            BusinessValueListParser customBusinessValueParser) {
        this.businessValueListParser = customBusinessValueParser;
    }
    
    public void setServiceLevelConverter(ServiceLevelConverter serviceLevelConverter) {
        this.serviceLevelConverter = serviceLevelConverter;
    }
    
    public void setContextConverter(ContextConverter customContextConverter) {
        this.contextConverter = customContextConverter;
    }
}
