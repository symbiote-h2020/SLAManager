/**
 * Copyright 2017 Atos
 * Contact: Atos <jose.sanchezm@atos.net>
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
package eu.h2020.symbiote.sla.federation;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EProvider;
import eu.atos.sla.datamodel.ETemplate;
import eu.atos.sla.enforcement.IEnforcementService;
import eu.atos.sla.evaluation.constraint.simple.Operator;
import eu.h2020.symbiote.model.mim.Federation;
import eu.h2020.symbiote.model.mim.FederationMember;
import eu.h2020.symbiote.model.mim.QoSConstraint;
import eu.h2020.symbiote.sla.SLAConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class RabbitFederationListener {

    private static final Logger logger = LoggerFactory.getLogger(RabbitFederationListener.class);

    @Value("${PLATFORM_ID}")
    private String platformId;

    @Autowired
    IProviderDAO providerDAO;

    @Autowired
    ITemplateDAO templateDAO;

    @Autowired
    IAgreementDAO agreementDAO;

    @Autowired
    private IEnforcementService enforcementService;

    private ObjectMapper mapper = new ObjectMapper();

    private Federation toFederation(Message message) {
        try {
            return mapper.readValue(message.getBody(), Federation.class);
        } catch (IOException e) {
            logger.warn("Received invalid federation message: " + message.getBody(), e);
        }
        return null;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = SLAConstants.SLA_UNREGISTRATION_QUEUE_NAME,
                    durable = "true",
                    exclusive = "true",
                    autoDelete = "true",
                    arguments=
                            {@Argument(
                                    name = "x-message-ttl",
                                    value="${RABBIT_REPLY_TIMEOUT}",
                                    type="java.lang.Integer")}),
            exchange = @Exchange(
                    value = "${EXCHANGE_NAME_FM}",
                    type = "${EXCHANGE_TYPE_FM}",
                    durable = "${EXCHANGE_DURABLE_FM",
                    autoDelete = "${EXCHANGE_AUTODELETE_FM",
                    internal = "${EXCHANGE_INTERNAL_FM"),
            key = "${FEDERATION_DELETED_KEY}")
    )
    public void federationRemoval(@Payload Message message) {
        logger.info("Got Federation removal request: " + message);
        String federationId = new String(message.getBody());
        logger.info("Got Federation removal for federationId: " + federationId);
        deleteAgreement(federationId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = SLAConstants.SLA_UPDATE_QUEUE_NAME,
                    durable = "true",
                    exclusive = "true",
                    autoDelete = "true",
                    arguments=
                            {@Argument(
                                    name = "x-message-ttl",
                                    value="${RABBIT_REPLY_TIMEOUT}",
                                    type="java.lang.Integer")}),
            exchange = @Exchange(
                    value = "${EXCHANGE_NAME_FM}",
                    type = "${EXCHANGE_TYPE_FM}",
                    durable = "${EXCHANGE_DURABLE_FM",
                    autoDelete = "${EXCHANGE_AUTODELETE_FM",
                    internal = "${EXCHANGE_INTERNAL_FM"),
            key = "${FEDERATION_UPDATE_KEY}")
    )
    public void federationUpdated(@Payload Message message) {
        logger.info("Got Federation updated request: " + message);

        Federation federation = toFederation(message);

        if (federation != null) {
            logger.debug("My platform id is " + platformId);

            deleteAgreement(federation.getId());

            Optional<FederationMember> existing = federation.getMembers().stream()
                    .filter(member ->
                            platformId.equals(member.getPlatformId()))
                    .findFirst();

            if (existing.isPresent()) {
                logger.debug("Found provider = " + existing.get().getPlatformId());
                EProvider provider = providerDAO.getByName(platformId);
                if (provider == null) {
                    provider = new EProvider();
                    provider.setName(platformId);
                    provider.setUuid(UUID.randomUUID().toString());
                    provider = providerDAO.save(provider);
                }

                ETemplate template = new ETemplate();
                template.setProvider(provider);
                template.setName(federation.getId());
                template.setText(federation.getId());
                template.setUuid(UUID.randomUUID().toString());
                template.setServiceId(platformId);
                template = templateDAO.save(template);

                EAgreement agreement = new EAgreement();
                agreement.setTemplate(template);
                agreement.setProvider(provider);
                agreement.setAgreementId(federation.getId());
                agreement.setConsumer(federation.getId());
                agreement.setCreationDate(new Date());
                agreement.setName(federation.getId());
                agreement.setServiceId(federation.getId());

                agreement.setGuaranteeTerms(getGuaranteeTerms(federation.getSlaConstraints()));

                agreement = agreementDAO.save(agreement);

                enforcementService.createEnforcementJob(agreement.getAgreementId());
                enforcementService.startEnforcement(agreement.getAgreementId());
            }
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = SLAConstants.SLA_REGISTRATION_QUEUE_NAME,
                    durable = "true",
                    exclusive = "true",
                    autoDelete = "true",
                    arguments=
                            {@Argument(
                                    name = "x-message-ttl",
                                    value="${RABBIT_REPLY_TIMEOUT}",
                                    type="java.lang.Integer")}),
            exchange = @Exchange(
                    value = "${EXCHANGE_NAME_FM}",
                    type = "${EXCHANGE_TYPE_FM}",
                    durable = "${EXCHANGE_DURABLE_FM",
                    autoDelete = "${EXCHANGE_AUTODELETE_FM",
                    internal = "${EXCHANGE_INTERNAL_FM"),
            key = "${FEDERATION_CREATION_KEY}")
    )
    public void federationCreation(@Payload Message message) {
        logger.info("Got Federation creation request: " + message);
        federationUpdated(message);
    }

    private void deleteAgreement(String agreementId) {
        EAgreement existingAgreement = agreementDAO.getByAgreementId(agreementId);

        if (existingAgreement != null) {
            enforcementService.stopEnforcement(agreementId);
            enforcementService.deleteEnforcementJobByAgreementId(agreementId);
            ETemplate template = existingAgreement.getTemplate();
            agreementDAO.delete(existingAgreement);
            templateDAO.delete(template);
        }
    }

    public List<EGuaranteeTerm> getGuaranteeTerms(List<QoSConstraint> slaConstraints) {
        List<EGuaranteeTerm> guaranteeTerms = new ArrayList<>();
        for (QoSConstraint constraint : slaConstraints) {
            EGuaranteeTerm term = geteGuaranteeTerm(constraint);
            guaranteeTerms.add(term);
        }
        return guaranteeTerms;
    }

    public EGuaranteeTerm geteGuaranteeTerm(QoSConstraint constraint) {
        EGuaranteeTerm term = new EGuaranteeTerm();
        String kpiName = getKpiName(constraint);

        term.setKpiName(kpiName);

        String slo = getSlo(constraint, kpiName);

        term.setServiceLevel(slo);
        return term;
    }

    public String getSlo(QoSConstraint constraint, String kpiName) {
        Operator comparator = null;
        switch (constraint.getComparator()) {
            case equal:
                comparator = Operator.EQ;
                break;
            case lessThanOrEqual:
                comparator = Operator.LE;
                break;
            case lessThan:
                comparator = Operator.LT;
                break;
            case greaterThan:
                comparator = Operator.GT;
                break;
            case greaterThanOrEqual:
                comparator = Operator.GE;
                break;
        }

        return kpiName + " " + comparator.toString() + " " + constraint.getThreshold();
    }

    public String getKpiName(QoSConstraint constraint) {
        String kpiName = constraint.getMetric().toString();
        if (constraint.getResourceType() != null) {
            kpiName = kpiName + "." + constraint.getResourceType();
        }

        String duration = "all";
        if (constraint.getDuration() != null) {
            duration = constraint.getDuration().toString();
        }

        kpiName = kpiName + "." + duration;
        return kpiName;
    }

}