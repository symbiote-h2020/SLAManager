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
package eu.atos.sla.datamodel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;

@Entity(name = "Agreement")
@Table(name = "agreement")
@NamedQueries({
        @NamedQuery(name = EAgreement.QUERY_FIND_ALL, query = "SELECT p FROM Agreement p"),
        @NamedQuery(name = EAgreement.QUERY_FIND_BY_AGREEMENT_ID, query = "SELECT p FROM Agreement p where p.agreementId = :agreementId "),
        @NamedQuery(name = EAgreement.QUERY_FIND_BY_CONSUMER, query = "SELECT p FROM Agreement p where p.consumer = :consumerId "),
        @NamedQuery(name = EAgreement.QUERY_FIND_BY_PROVIDER, query = "SELECT p FROM Agreement p where  p.provider.uuid = :providerUuid "),
        @NamedQuery(name = EAgreement.QUERY_FIND_BY_TEMPLATEUUID, query = "SELECT p FROM Agreement p where  p.template.uuid = :templateUUID "),
        @NamedQuery(name = EAgreement.QUERY_ACTIVE_AGREEMENTS, query = "SELECT p FROM Agreement p where p.expirationDate > :actualDate "),
        @NamedQuery(name = EAgreement.QUERY_FIND_BY_TEMPLATEUUID_AND_CONSUMER, query = "SELECT p FROM Agreement p where (p.template.uuid = :templateUUID) AND (p.consumer = :consumerId)"),
        @NamedQuery(name = EAgreement.QUERY_SEARCH, query = "SELECT a FROM Agreement a "
                + "WHERE (:providerId is null or a.provider.uuid = :providerId) "
                + "AND (:consumerId is null or a.consumer = :consumerId) "
                + "AND (:templateId is null or a.template.uuid = :templateId) "
                + "AND (:active is null "
                + "    or (:active = true and a.expirationDate > current_timestamp()) "
                + "    or (:active = false and a.expirationDate <= current_timestamp()))") })

public class EAgreement extends AbstractEntity<EAgreement> implements Serializable {
    public final static String QUERY_FIND_ALL = "Agreement.findAll";
    public final static String QUERY_FIND_ALL_AGREEMENTS = "Agreement.findAllAgreements";
    public final static String QUERY_FIND_BY_PROVIDER = "Agreement.findByProvider";
    public final static String QUERY_FIND_BY_CONSUMER = "Agreement.findByConsumer";
    public final static String QUERY_FIND_BY_AGREEMENT_ID = "Agreement.getByAgreementId";
    public final static String QUERY_ACTIVE_AGREEMENTS = "Agreement.getActiveAgreements";
    public final static String QUERY_FIND_BY_TEMPLATEUUID = "Agreement.getByTemplateUUID";
    public final static String QUERY_FIND_BY_TEMPLATEUUID_AND_CONSUMER = "Agreement.getByTemplateUUIDAndConsumer";
    public final static String QUERY_SEARCH = "Agreement.search";

    private static final long serialVersionUID = -5939038640423447257L;

    static public class Context {
        
        static public enum ServiceProvider {
            AGREEMENT_INITIATOR("AgreementInitiator"), 
            AGREEMENT_RESPONDER("AgreementResponder");
            
            String label;
            private ServiceProvider(String label) {
                
                this.label = label;
            }
            
            @Override
            public String toString() {
                return label;
            }

            public static ServiceProvider fromString(String value) {
                
                if (AGREEMENT_INITIATOR.toString().equals(value)) {
                    return AGREEMENT_INITIATOR;
                }
                else if (AGREEMENT_RESPONDER.toString().equals(value)) {
                    return AGREEMENT_RESPONDER;
                }
                throw new IllegalArgumentException(String.format(
                        "No enum %s[label=%s]", ServiceProvider.class.getName(), value));
            }
        }
    }
    static public enum AgreementStatus {
        PENDING, OBSERVED, REJECTED, COMPLETE, PENDING_AND_TERMINATING, OBSERVED_AND_TERMINATING, TERMINATED
    }
    
    
    private String agreementId;
    private String consumer;
    private EProvider provider;
    private ETemplate template;
    private Date expirationDate;
    private Date creationDate;
    private AgreementStatus status;
    private String text;
    private List<EServiceProperties> serviceProperties;
    private List<EGuaranteeTerm> guaranteeTerms;
    private String serviceId;
    private Boolean hasGTermToBeEvaluatedAtEndOfEnformcement;
    private String name;
                            
    public EAgreement() {
    }

    public EAgreement(String agreementId) {
        this.agreementId = agreementId;
    }

    /**
     * This agreement is recognized by external parties by the wsag:Agreement/@AgreementId
     * attribute. If the attribute was not set, a new one is generated and
     * communicated to the consumer.
     */
    @Column(name = "agreement_id", unique = true)
    public String getAgreementId() {

        return agreementId;
    }

    public void setAgreementId(String agreementId) {

        this.agreementId = agreementId;
    }

    /**
     * Consumer ID, provided by the consumer.
     */
    @Column(name = "consumer")
    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    /**
     * The provider of the service.
     */
    @ManyToOne(targetEntity = EProvider.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", referencedColumnName = "id", nullable = false)
    public EProvider getProvider() {
        return provider;
    }

    public void setProvider(EProvider provider) {
        this.provider = provider;
    }

    /**
     * The agreement is based on this template.
     */
    @ManyToOne(targetEntity = ETemplate.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id", referencedColumnName = "id", nullable = false)
    public ETemplate getTemplate() {
        return template;
    }

    public void setTemplate(ETemplate template) {
        this.template = template;
    }

    /**
     * This agreement is valid until the expiration date
     */
    @Column(name = "expiration_time")
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Creation date of the agreement
     */
    @Column(name = "creation_time")
    public Date getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * The agreement is in this ws-agreement state. An agreement is based on a
     * template. During the negotiation, an agreement is considered and
     * AgreementOffer. Once the negotiation is accepted, the agreement is
     * considered a contract.
     */
    @Transient
    public AgreementStatus getStatus() {
        return status;
    }

    public void setStatus(AgreementStatus status) {
        this.status = status;
    }

    /**
     * Agreement body. This is an ws-agreement-compliant xml. NOTE: String?
     * Maybe there is a better type.
     */
    @Column(name = "text", columnDefinition = "longtext")
    @Lob
    public String getText() {
        return text;
    }

    @Lob
    public void setText(String text) {
        this.text = text;
    }

    /**
     * ServiceProperties are used to define measurable and exposed properties associated
     * with a service, such as response time and throughtput. The properties are used
     * in expressing service level objectives.
     */
    @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = EServiceProperties.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "agreement_id", referencedColumnName = "id", nullable = true)
    public List<EServiceProperties> getServiceProperties() {
        return serviceProperties;
    }

    public void setServiceProperties(List<EServiceProperties> serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    /**
     * These are the statements to offered service levels.
     */
    @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = EGuaranteeTerm.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "agreement_id", referencedColumnName = "id", nullable = true)
    public List<EGuaranteeTerm> getGuaranteeTerms() {
        return guaranteeTerms;
    }

    public void setGuaranteeTerms(List<EGuaranteeTerm> guaranteeTerms) {
        this.guaranteeTerms = guaranteeTerms;

    }

    /**
     * This is the content of /agreement/context/sla:service element.
     * 
     * This element identifies the unique service offered by this agreement. 
     * It differs from serviceName attributes in that there can be several
     * serviceNames per agreement.
     */
    @Column(name = "service_id")
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    /**
     * some of the GuaranteeTerms should just measured and evaluated at the end of the execution of 
     * the enforcementJob. This variable indicates if it has to be executed or not. 
     */
    @Column(name = "metrics_eval_end")
    public Boolean getHasGTermToBeEvaluatedAtEndOfEnformcement() {
        return hasGTermToBeEvaluatedAtEndOfEnformcement;
    }

    public void setHasGTermToBeEvaluatedAtEndOfEnformcement(
            Boolean hasGTermToBeEvaluatedAtEndOfEnformcement) {
        this.hasGTermToBeEvaluatedAtEndOfEnformcement = hasGTermToBeEvaluatedAtEndOfEnformcement;
    }

    /**
     * Name of the agreement.
     */
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}
