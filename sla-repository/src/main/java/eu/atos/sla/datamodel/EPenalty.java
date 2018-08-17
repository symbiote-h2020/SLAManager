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

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity(name = "Penalty")
@Table(name="penalty")
@Access(AccessType.FIELD)
@NamedQueries({
    @NamedQuery(name=EPenalty.Query.FIND_BY_UUID, query=
            "SELECT p FROM Penalty p where p.uuid = :uuid"),
    @NamedQuery(name=EPenalty.Query.SEARCH, query=
            "select p from Agreement a "
            + "inner join a.guaranteeTerms t "
            + "inner join t.penalties p "
            + "where (:agreementId is null or a.agreementId = :agreementId) "
            + "and (:termName is null or t.name = :termName) "
            + "and (:begin is null or p.datetime >= :begin) "
            + "and (:end is null or p.datetime < :end) "
            + "order by p.datetime desc"),
    @NamedQuery(name = EPenalty.Query.FIND_LAST_PENALTY, query =
            " select max(p.datetime) from Penalty p "
            + "inner join p.definition d "
            + "inner join p.violation v "
            + "where p.agreementId = :agreementId "
            + "and p.definition.count = :count "
            + "and p.definition.timeInterval = :interval "
            + "and v.kpiName = :kpiName"), 
})
public class EPenalty extends ECompensation<EPenalty> {
    
    public static final class Query {
        public static final String SEARCH = "Penalty.search";
        public static final String FIND_BY_UUID = "Penalty.findByUuid";
        public static final String FIND_LAST_PENALTY = "Penalty.findLastPenalty";
    }
    
    private static final EPenaltyDefinition DEFAULT_PENALTY = new EPenaltyDefinition();
    private static final EViolation DEFAULT_VIOLATION = new EViolation();
    
    @ManyToOne(targetEntity = EPenaltyDefinition.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "definition_id", referencedColumnName = "id", nullable = false)
    private EPenaltyDefinition definition;
    
    @ManyToOne(targetEntity = EViolation.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "violation_id", referencedColumnName = "id", nullable = false)
    private EViolation violation;
    
    public EPenalty() {
        super();
        this.definition = DEFAULT_PENALTY;
        this.violation = DEFAULT_VIOLATION;
    }

    public EPenalty(String agreementId, Date datetime, String kpiName, 
            EPenaltyDefinition definition, EViolation violation) {
        super(agreementId, datetime, kpiName);
        this.definition = definition;
        this.violation = violation;
    }
    
    public EPenaltyDefinition getDefinition() {
        return definition;
    }

    public EViolation getViolation() {
        return violation;
    }
    
    public String toString() {
        return String.format(
                "Penalty [uuid=%s, agreementId=%s, datetime=%s, definition=%s]", 
                getUuid(), getAgreementId(), getDatetime(), definition);
    }

}
