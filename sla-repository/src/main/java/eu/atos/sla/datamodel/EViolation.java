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
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.hibernate.annotations.Fetch;

@Entity(name = "Violation")
@Table(name = "violation")
@NamedQueries({
        @NamedQuery(name = EViolation.QUERY_FIND_ALL, query = "SELECT p FROM Violation p"),
        @NamedQuery(name = EViolation.QUERY_FIND_BY_UUID, query = "SELECT p FROM Violation p where p.uuid = :uuid "),
        @NamedQuery(name = EViolation.QUERY_FIND_VIOLATIONS_BY_AGREEMENT_IN_A_RANGE_OF_DATES, query = 
                "select v from Agreement a "
                + "inner join a.guaranteeTerms t inner join t.violations v "
                + "where a.agreementId = :agreementId "
                + "and (:termName is null or t.name = :termName) "
                + "and (:begin is null or v.datetime >= :begin) "
                + "and (:end is null or v.datetime < :end) "
                + "order by v.datetime desc"),
        @NamedQuery(name = EViolation.QUERY_FIND_VIOLATIONS_BY_AGREEMENT, query = 
                "select v from Agreement a "
                + "inner join a.guaranteeTerms t inner join t.violations v "
                + "where a.agreementId = :agreementId "
                + "and (:termName is null or t.name = :termName) "
                + "order by v.datetime desc"),
        @NamedQuery(name = EViolation.QUERY_FIND_VIOLATIONS_BY_PROVIDER, query = 
                "select v from Agreement a "
                + "inner join a.guaranteeTerms t inner join t.violations v "
                + "where (:providerUuid is null or :providerUuid in ( SELECT uuid FROM a.provider)) "
                + "order by v.datetime desc"),
        @NamedQuery(name = EViolation.QUERY_FIND_VIOLATIONS_BY_PROVIDER_IN_A_RANGE_OF_DATES, query = 
                "select v from Agreement a "
                + "inner join a.guaranteeTerms t inner join t.violations v "
                + "where (:providerUuid is null or :providerUuid in ( SELECT uuid FROM a.provider)) "
                + "and (:begin is null or v.datetime >= :begin) "
                + "and (:end is null or v.datetime < :end) "
                + "order by v.datetime desc"),
        @NamedQuery(name = EViolation.QUERY_FIND_BY_SERVICE_NAME, query = 
                "SELECT p FROM Violation p where p.serviceName= :serviceName "
                + "order by p.datetime desc"),
        @NamedQuery(name = EViolation.QUERY_FIND_BY_DATE_RANGE, query = 
                "SELECT p FROM Violation p "
                + "where p.datetime > :fromDate "
                + "and p.datetime < :untilDate "
                + "order by p.datetime desc "),
        @NamedQuery(name = EViolation.QUERY_SEARCH, query = 
                "select v from Agreement a "
                + "inner join a.guaranteeTerms t "
                + "inner join t.violations v "
                + "where (:agreementId is null or a.agreementId = :agreementId) "
                + "and (:termName is null or t.name = :termName) "
                + "and (:providerUuid is null or :providerUuid in ( SELECT uuid FROM a.provider)) "
                + "and (:begin is null or v.datetime >= :begin) "
                + "and (:end is null or v.datetime < :end) "
                + "order by v.datetime desc"),
        @NamedQuery(name = EViolation.QUERY_FIND_LAST_VIOLATION_DATE, query =
                "SELECT max(v.datetime) FROM Violation v "
                        + "where v.contractUuid = :agreementId "
                        + "and v.kpiName = :kpiName "),
        })
public class EViolation extends AbstractEntity<EViolation> implements Serializable {

    public final static String QUERY_FIND_ALL = "Violation.findAll";
    public final static String QUERY_FIND_BY_UUID = "Violation.getByUUID";
    public final static String QUERY_FIND_VIOLATIONS_BY_PROVIDER_IN_A_RANGE_OF_DATES = "Violation.findByProviderInaRangeOfDates";
    public final static String QUERY_FIND_VIOLATIONS_BY_PROVIDER = "Violation.findByProviders";
    public final static String QUERY_FIND_VIOLATIONS_BY_AGREEMENT_IN_A_RANGE_OF_DATES = "Violation.findByAgreementInaRangeOfDates";
    public final static String QUERY_FIND_VIOLATIONS_BY_AGREEMENT = "Violation.findByAgreement";
    public final static String QUERY_FIND_BY_SERVICE_NAME = "Violation.getByServiceName";
    public final static String QUERY_FIND_BY_DATE_RANGE = "Violation.getByDateRange";
    public final static String QUERY_SEARCH = "Violation.search";
    public final static String QUERY_FIND_LAST_VIOLATION_DATE = "Violation.getLastViolationDate";

    private static final long serialVersionUID = -355383135044719022L;
    private String uuid;
    private String contractUuid;
    private String serviceName;
    private String serviceScope;
    private String kpiName;
    private Date datetime;
    private String expectedValue;
    private List<EBreach> breaches;
    private String actualValue;
    
    private EPolicy policy;

    public EViolation() {
    }

    public EViolation(final EAgreement contract, final EGuaranteeTerm term, 
            final EPolicy policy, final String kpiName, final String actualValue, 
            final String expectedValue, final Date timestamp) {

        this.setUuid(UUID.randomUUID().toString());
        this.setContractUuid(contract.getAgreementId());
        this.setKpiName(kpiName);
        this.setDatetime(timestamp);
        this.setExpectedValue(expectedValue);
        this.setActualValue(actualValue);
        this.setServiceName(term.getServiceName());
        this.setServiceScope(term.getServiceScope());
        this.setContractUuid(contract.getAgreementId());
        this.setPolicy(policy);
    }

    /**
     * Internal generated UUID. The interested external parties are going to
     * identify this violation by the UUID.
     */
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Contract UUID where this violation has been detected.
     */
    @Column(name = "contract_uuid")
    public String getContractUuid() {
        return contractUuid;
    }

    public void setContractUuid(String contractUUID) {
        this.contractUuid = contractUUID;
    }

    /**
     * @see EGuaranteeTerm#getServiceName()
     */
    @Column(name = "service_name")
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @see EGuaranteeTerm#getServiceScope()
     */
    @Column(name = "service_scope")
    public String getServiceScope() {
        return serviceScope;
    }

    public void setServiceScope(String serviceScope) {
        this.serviceScope = serviceScope;
    }

    /**
     * Name of the kpi that has generated this violation.
     */
    @Column(name = "kpi_name")
    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    /**
     * Date and time when the violation was raised.
     */
    @Column(name = "datetime")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    /**
     * Expected value of the non fulfilled metric.
     */
    @Column(name = "expected_value")
    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    /**
     * Measured value of the metric. If a violation is raised by 2 or more breaches,
     * the value is a string of format { value1, value2, ..., valueN }
     */
    @Column(name = "actual_value")
    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    /**
     * Breaches that raised this violation.
     */
    @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = EBreach.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "violation_id", referencedColumnName = "id", nullable = true)
    public List<EBreach> getBreaches() {
        return breaches;
    }

    public void setBreaches(List<EBreach> breaches) {
        this.breaches = breaches;
    }

    @ManyToOne(targetEntity = EPolicy.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "policy_id", referencedColumnName = "id", nullable = true)
    public EPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(EPolicy policy) {
        this.policy = policy;
    }

}