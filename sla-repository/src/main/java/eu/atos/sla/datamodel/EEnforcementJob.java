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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity(name = "EnforcementJob")
@Table(name = "enforcement_job")
@NamedQueries({
        @NamedQuery(name = EEnforcementJob.QUERY_FIND_ALL, query = "SELECT o FROM EnforcementJob o"),
        @NamedQuery(name = EEnforcementJob.QUERY_FIND_NOT_EXECUTED, query = "SELECT o FROM EnforcementJob o "
                + "WHERE o.enabled = true AND (o.lastExecuted < :since OR o.lastExecuted is null)"),
        @NamedQuery(name = EEnforcementJob.QUERY_FIND_BY_AGREEMENT_ID, query = "SELECT o FROM EnforcementJob o "
                + "WHERE o.agreement.agreementId = :agreementId") })
public class EEnforcementJob extends AbstractEntity<EEnforcementJob> implements Serializable {

    private static final long serialVersionUID = -4913452966352163156L;

    public final static String QUERY_FIND_ALL = "EnforcementJob.findAll";
    public final static String QUERY_FIND_NOT_EXECUTED = "EnforcementJob.findNotExecuted";
    public final static String QUERY_FIND_BY_AGREEMENT_ID = "EnforcementJob.findByAgreementId";
    public final static String QUERY_START_AGREEMENT = "EnforcementJob.startAgreement";
    public final static String QUERY_STOP_AGREEMENT = "EnforcementJob.stopAgreement";

    private Date firstExecuted;
    private Date lastExecuted;
    private boolean enabled;
    private EAgreement agreement;

    /**
     * Date of last enforcement start
     */
   @Column(name = "first_executed")
    public Date getFirstExecuted() {
        return firstExecuted;
    }

   /**
    * Last datetime where the job was executed
    */
    @Column(name = "last_executed")
    public Date getLastExecuted() {

        return lastExecuted;
    }

    /**
     * EnforcementJob enabled or not 
     */
    @Column(name = "enabled", columnDefinition = "BIT", length = 1)
    public boolean getEnabled() {

        return enabled;
    }

    /**
     * Agreement being enforced.
     */
    @ManyToOne(targetEntity = EAgreement.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "agreement_id", referencedColumnName = "id", nullable = false)
    public EAgreement getAgreement() {

        return agreement;
    }

    public void setFirstExecuted(Date firstExecuted) {
        this.firstExecuted = firstExecuted;
    }

    public void setLastExecuted(Date lastExecuted) {
        this.lastExecuted = lastExecuted;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAgreement(EAgreement agreement) {
        this.agreement = agreement;
    }

}
