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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IEnforcementJobDAO;
import eu.atos.sla.dao.IGuaranteeTermDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EEnforcementJob;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EGuaranteeTerm.GuaranteeTermStatusEnum;
import eu.atos.sla.datamodel.EProvider;
import eu.atos.sla.datamodel.ETemplate;
import eu.atos.sla.enforcement.IEnforcementService;
import eu.atos.sla.modelconversion.ModelConversionException;
import eu.atos.sla.modelconversion.ModelConverter;
import eu.atos.sla.parser.data.GuaranteeTermStatus;
import eu.atos.sla.parser.data.GuaranteeTermsStatus;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.Context;
import eu.atos.sla.service.rest.helpers.exception.DBExistsHelperException;
import eu.atos.sla.service.rest.helpers.exception.DBMissingHelperException;
import eu.atos.sla.service.rest.helpers.exception.InternalHelperException;
import eu.atos.sla.service.rest.helpers.exception.ParserHelperException;


/**
 * 
 * @author Elena Garrido
 */

@Service
@Transactional
public class AgreementHelperE{
    private static Logger logger = LoggerFactory.getLogger(AgreementHelperE.class);

    @Autowired
    private IAgreementDAO agreementDAO;

    @SuppressWarnings("unused")
    @Autowired
    private IGuaranteeTermDAO guaranteeTermDAO;

    @Autowired
    private IProviderDAO providerDAO;

    @Autowired
    private ITemplateDAO templateDAO;

    @Autowired
    private ModelConverter modelConverter;
    
    @Autowired
    private IEnforcementJobDAO enforcementJobDAO;

    @Autowired
    private IEnforcementService enforcementService;
    
    public AgreementHelperE() {
    }

    private boolean doesAgreementIdExistInRepository(String agreementId) {
        return agreementDAO.getByAgreementId(agreementId) != null;
    }

    private boolean doesEnforcementExistInRepository(String agreementId) {
        return enforcementJobDAO.getByAgreementId(agreementId) != null;
    }

    private EProvider providerFromRepository(String providerUUID) {
        if (providerUUID==null) return null;
        return providerDAO.getByUUID(providerUUID) ;
    }
    
    private ETemplate templateFromRepository(String templateUUID) {
        if (templateUUID==null) return null;
        return templateDAO.getByUuid(templateUUID);
    }
    
    
    public String createAgreement(Agreement agreementXML) throws DBMissingHelperException, DBExistsHelperException, InternalHelperException, ParserHelperException {
        try{
            EAgreement agreementStored = null;
    
            if (agreementXML != null) {
    
                // add field AggrementId if it doesn't exist
                if (agreementXML.getAgreementId() == null) {
                    String agreementId = UUID.randomUUID().toString();
                    logger.debug("createAgreement agreement has no uuid, {} will be assigned", agreementId); 
                    agreementXML.setAgreementId(agreementId);
                }
    
                if (!doesAgreementIdExistInRepository(agreementXML.getAgreementId())) {
                    EAgreement agreement = modelConverter.getAgreementFromAgreementXML(agreementXML, "");
                    
                    String providerUuid = agreement.getProvider().getUuid();
                    EProvider provider = providerFromRepository(providerUuid);
                    if (provider == null) {
                        throw new DBMissingHelperException("Provider with id:"+ providerUuid+ " doesn't exist SLA Repository Database");
                    }
                    agreement.setProvider(provider);
    
                    String templateUuid = agreement.getTemplate().getUuid();
                    ETemplate template = templateFromRepository(templateUuid);
                    if (template == null) {
                        throw new DBMissingHelperException("Template with id:"+ templateUuid+ " doesn't exist SLA Repository Database");
                    }
                    agreement.setTemplate(template);
    
                    agreementStored = this.agreementDAO.save(agreement);
                    
                    /* create an stopped enforcement job */
                    if (!doesEnforcementExistInRepository(agreementStored.getAgreementId())) {
                        // the enforcement doesn't eist
                        EEnforcementJob ejob = 
                                enforcementService.createEnforcementJob(agreementStored.getAgreementId());
                        logger.debug("EnforcementJob {} created", ejob.getId());
                    } else {
                        throw new DBExistsHelperException("Enforcement with id:"
                                + agreementStored.getAgreementId()
                                + " already exists in the SLA Repository Database");
                    }
                    
    
                } else {
                    throw new DBExistsHelperException("Agreement with id:"+ agreementXML.getAgreementId()+ " already exists in the SLA Repository Database");
                }
            }
    
            if (agreementStored != null) {
                logger.debug("EndOf createAgreement");
                return agreementStored.getAgreementId();
            } else{
                logger.debug("EndOf createAgreement");
                throw new InternalHelperException("Error when creating agreement the SLA Repository Database");
            }
        } catch (ModelConversionException e) {
            logger.error("Error in createAgreement " , e);
            throw new ParserHelperException("Error when creating:" + e.getMessage() );
        }

    }



    private GuaranteeTermsStatus getGuaranteeStatus(String agreementId, List<EGuaranteeTerm> guaranteeTerms) {

        // Pojo GuaranteeTermsStatus
        GuaranteeTermsStatus guaranteeTermsStatus = new GuaranteeTermsStatus();

        List<GuaranteeTermStatus> guaranteeTermStatusList = new ArrayList<GuaranteeTermStatus>();

        // Status GuaranteTerm
        GuaranteeTermStatusEnum agreementStatus = AgreementStatusCalculator.getStatus(guaranteeTerms);

        guaranteeTermsStatus.setAgreementId(agreementId);
        guaranteeTermsStatus.setValue(agreementStatus.toString());

        // Fill GuaranteeTermsStatus pojo
        for (EGuaranteeTerm guaranteeTerm : guaranteeTerms) {
            GuaranteeTermStatus guaranteeTermStatus = new GuaranteeTermStatus();
            guaranteeTermStatus.setName(guaranteeTerm.getName());
            guaranteeTermStatus.setValue(guaranteeTerm.getStatus().toString());
            guaranteeTermStatusList.add(guaranteeTermStatus);
        }

        guaranteeTermsStatus.setGuaranteeTermsStatus(guaranteeTermStatusList);

        return guaranteeTermsStatus;
    }

    public List<EAgreement> getAgreements(String consumerId, String providerId, String templateId, Boolean active) {
        logger.debug("StartOf getAgreements consumerId:{} - providerId:{} - templateId:{} - active:{}", 
                consumerId, providerId, templateId, active);
        List<EAgreement> agreements = agreementDAO.search(consumerId, providerId, templateId, active);
        logger.debug("EndOf getAgreements");
        return agreements;
    }


    public EAgreement getAgreementByID(String id) {
        logger.debug("StartOf getAgreementByID id:{}", id);
        EAgreement agreement = agreementDAO.getByAgreementId(id);
        logger.debug("EndOf getAgreementByID");
        return agreement;
    }


    public Context getAgreementContextByID(String id) throws InternalHelperException {
        logger.debug("StartOf getAgreementContextByID id:{}", id);
        EAgreement agreement = agreementDAO.getByAgreementId(id);
        Context context = null;
        try {
            if (agreement!= null)
                context = modelConverter.getContextFromAgreement(agreement);
        } catch (ModelConversionException e) {
            logger.error("Error getAgreementContextByID ",e);
            throw new InternalHelperException(e.getMessage());
        }
        logger.debug("EndOf getAgreementContextByID");
        return context;
    }
    
    
    public List<EAgreement> getActiveAgreements(long actualDate) {
        logger.debug("StartOf getActiveAgreements actualDate:{}", actualDate);
        List<EAgreement> agreements = agreementDAO.getByActiveAgreements(actualDate);
        logger.debug("EndOf getActiveAgreements");
        return agreements;
        
    }

    public boolean deleteByAgreementId(String agreementId) {
        logger.debug("StartOf deleteByAgreementId agreementId:{}", agreementId);
        boolean deleted = false;
        EEnforcementJob enforcementJob = enforcementJobDAO.getByAgreementId(agreementId);
        if (enforcementJob!=null){
            logger.debug("EnforcementJob exists associated to agreementId {} it will be stopped and removed", 
                    agreementId);
            enforcementJobDAO.delete(enforcementJob);
        }
        
        EAgreement agreement = agreementDAO.getByAgreementId(agreementId);

        if (agreement != null) {
            deleted = this.agreementDAO.delete(agreement);
        }

        logger.debug("EndOf deleteByAgreementId");
        return deleted;
    }

    public GuaranteeTermsStatus getAgreementStatus(String id) throws DBMissingHelperException{
        logger.debug("StartOf getAgreementStatus id:{}", id);

        EAgreement agreement = agreementDAO.getByAgreementId(id);
        if (agreement == null)
            throw new DBMissingHelperException("The agreementId " + id + " doesn't exist");

        List<EGuaranteeTerm> guaranteeTerms = agreement.getGuaranteeTerms();
        GuaranteeTermsStatus guaranteeTermsStatus = getGuaranteeStatus(id, guaranteeTerms);
        logger.debug("EndOf getAgreementStatus"); 

        return  guaranteeTermsStatus;
    }
    
    public List<EAgreement> getAgreementsPerTemplateAndConsumer(String consumerId, String templateUUID) {
        logger.debug("StartOf getAgreementsPerTemplateAndConsumer consumerId:"+consumerId+ " - templateUUID:"+templateUUID);
        List<EAgreement> agreements = agreementDAO.searchPerTemplateAndConsumer(consumerId, templateUUID);
        logger.debug("EndOf getAgreementsPerTemplateAndConsumer");
        return agreements;
    }


    public static class AgreementStatusCalculator {
        
        
        public static GuaranteeTermStatusEnum getStatus(List<EGuaranteeTerm> guaranteeTerms) {

            GuaranteeTermStatusEnum result = GuaranteeTermStatusEnum.FULFILLED;

            if (guaranteeTerms.size() == 0) {
                result = GuaranteeTermStatusEnum.NON_DETERMINED;
            } else {
                result = GuaranteeTermStatusEnum.FULFILLED;

                for (EGuaranteeTerm guaranteeTerm : guaranteeTerms) {

                    GuaranteeTermStatusEnum termStatus = guaranteeTerm.getStatus();
                    if (termStatus == null
                            || termStatus == GuaranteeTermStatusEnum.NON_DETERMINED) {
                        result = GuaranteeTermStatusEnum.NON_DETERMINED;
                    } else if (termStatus == GuaranteeTermStatusEnum.VIOLATED) {
                        result = GuaranteeTermStatusEnum.VIOLATED;
                        break;
                    }
                }
            }
            return result;
        }
    }
}
