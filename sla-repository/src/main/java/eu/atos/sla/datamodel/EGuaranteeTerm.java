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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;


/**
 * A GuaranteeTerm in ws-agreement is defined as:
 * <pre>{@code
 * <wsag:GuaranteeTerm Name="xs:string" Obligated="wsag:ServiceRoleType">
 *   <wsag:ServiceScope ServiceName="xs:string">xs:any?</wsag:ServiceScope>*
 *   <wsag:QualifyingCondition>xs:any</wsag:QualifyingCondition>?
 *   <wsag:ServiceLevelObjective>
 *     <wsag:KPITarget>
 *       <wsag:KPIName>xs:string</wsag:KPIName>
 *       <wsag:CustomServiceLevel>xs:any</wsag:CustomServiceLevel>
 *     </wsag:KPITarget>
 *   </wsag:ServiceLevelObjective>
 * </wsag:GuaranteeTerm>
 * }</pre>
 * 
 * The GT is "interpreted" in the core as:
 * <ul>
 * <li>Name: is a name to identify the term between all the terms.
 * <li>Obligated: it is always considered as "Provider", despite the attribute value.
 * <li>ServiceScope: the scope where the term takes into account. The serviceName refers 
 *   to a serviceName defined in the agreement. The text, if set, describe the element/service term
 *   this guarantee term applies to. <b>It is a restriction of the core to have one ServiceScope per
 *   GT as much</b>.
 * <li>QualifyingCondition. When this GT is going to be enforced. This is provider domain value.
 * <li>KPIName: The name of a kpi
 * <li>CustomServiceLevel: the constraint that the KPI has to fulfill
 * </ul>
 * 
 * Valid constraints:
 * <ul>
 * <li>current-response-time < desired-response-time
 * <li>current-response-time < 100
 * </ul>
 * 
 * In these constraints, current-response-time" and "desired-response-time" are service 
 * properties (wsag:ServiceProperties/Variable). The changing value is considered to be retrieved from
 * a external monitoring module. The location of the static value (wsag:Variable/Location) is considered 
 * to be the place where it is defined in the ServiceDescriptionTerms. 
 * 
 * The constraints are given a name with the KPIName. In the constraints above, a possible KPIName could be 
 * "response-time".
 * 
 * <b>It is a restriction of the core that there can be only one "changing variable" per ServiceLevelObjective</b>.
 * 
 * @author rsosa
 *
 */
@Entity(name = "GuaranteeTerm")
@Table(name = "guarantee_term")
@NamedQueries({ @NamedQuery(name = "GuaranteeTerm.findAll", query = "SELECT p FROM GuaranteeTerm p") })
public class EGuaranteeTerm extends AbstractEntity<EGuaranteeTerm> implements Serializable {
    
    public static enum GuaranteeTermStatusEnum {
        FULFILLED, VIOLATED, NON_DETERMINED
    }
    
    public static int ENFORCED_AT_END = -1;

    private static final long serialVersionUID = -8140757088864002129L;
    private String name;
    private String serviceName;
    private String serviceScope;
    private String kpiName;
    private String serviceLevel;
    private List<EViolation> violations;
    private List<EPenalty> penalties;
    private List<EPolicy> policies;
    private GuaranteeTermStatusEnum status;
    private EBusinessValueList businessValueList;

    private Date lastSampledDate;
    private Integer  samplingPeriodFactor;
    
    public EGuaranteeTerm() {

        this.status = GuaranteeTermStatusEnum.NON_DETERMINED;
    }

    /**
     * Name of this guarantee term.
     */
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Name of service this guarantee term applies to.
     */
    @Column(name = "service_name")
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Describes to what service element specifically a guarantee term applies.
     */
    @Column(name = "service_scope")
    public String getServiceScope() {
        return serviceScope;
    }

    public void setServiceScope(String serviceScope) {
        this.serviceScope = serviceScope;
    }

    /**
     * KPI of the Service level objective associated with this guarantee term.
     */
    @Column(name = "kpi_name")
    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    /**
     * The service level (the constraint) of this guarantee term.
     */
   @Column(name = "service_level")
    public String getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(String serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    /**
     * The policies for this guarantee term.
     */
    @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = EPolicy.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "guarantee_term_id", referencedColumnName = "id", nullable = true)
    public List<EPolicy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<EPolicy> policies) {
        this.policies = policies;
    }

    /**
     * The violations detected for this guarantee term.
     */
    @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = EViolation.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "guarantee_term_id", referencedColumnName = "id", nullable = true)
    public List<EViolation> getViolations() {
        return violations;
    }

    public void setViolations(List<EViolation> violations) {
        this.violations = violations;
    }

    @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = EPenalty.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "guarantee_term_id", referencedColumnName = "id", nullable = true)
    public List<EPenalty> getPenalties() {
        return penalties;
    }

    public void setPenalties(List<EPenalty> penalties) {
        this.penalties = penalties;
    }

    /**
     * Guarantee term status
     */
    @Column(name = "status", nullable = false)
    public GuaranteeTermStatusEnum getStatus() {
        return status;
    }

    public void setStatus(GuaranteeTermStatusEnum status) {
        this.status = status;
    }

    /**
     * BusinessValueList: business values, each expressing a different value aspect of the objective.
     */
    @OneToOne(targetEntity = EBusinessValueList.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "business_value_id")
    public EBusinessValueList getBusinessValueList() {
        return businessValueList;
    }

    public void setBusinessValueList(EBusinessValueList businessValueList) {
        this.businessValueList = businessValueList;
    }

    @Column(name = "lastSampledDate", nullable = true)
    public Date getLastSampledDate() {
        return lastSampledDate;
    }

    public void setLastSampledDate(Date lastSampledDate) {
        this.lastSampledDate = lastSampledDate;
    }

    @Column(name = "samplingPeriodFactor", nullable = true)
    public Integer getSamplingPeriodFactor() {
        return samplingPeriodFactor;
    }

    public void setSamplingPeriodFactor(Integer samplingPeriodFactor) {
        this.samplingPeriodFactor = samplingPeriodFactor;
    }
    
    
}
