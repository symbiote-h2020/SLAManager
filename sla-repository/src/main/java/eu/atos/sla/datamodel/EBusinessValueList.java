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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;

@Entity(name = "BusinessValueList")
@Table(name = "business_value_list")
@Access(AccessType.FIELD)
public class EBusinessValueList extends AbstractEntity<EBusinessValueList> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Column(name="importance", nullable=false)
    private int importance;
    
    @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = EPenaltyDefinition.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "business_value_id", referencedColumnName = "id", nullable = true)
    private List<EPenaltyDefinition> penalties;

    public EBusinessValueList() {
        this.importance = 0;
        this.penalties = new ArrayList<EPenaltyDefinition>();
    }
    
    public EBusinessValueList(int importance, Collection<EPenaltyDefinition> penalties) {
    
        this.importance = importance;
        this.penalties = new ArrayList<EPenaltyDefinition>();
        if (penalties != null) {
            this.penalties.addAll(penalties);
        }
    }
    
    public List<EPenaltyDefinition> getPenalties() {
        /*
         * provides more collection encapsulation. Each item may still be mutable. 
         */
        return Collections.unmodifiableList(penalties);
    }

    /**
     * Relative importance of meeting an objective.
     * 
     * This core assumes the higher, the more important, with 0 as minimum value.
     */
    public int getImportance() {
        
        return importance;
    }
    
    public void addPenalty(EPenaltyDefinition penalty) {
        
        this.penalties.add(penalty);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getPenalties() == null) ? 0 : getPenalties().hashCode());
        result = prime * result + importance;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof EBusinessValueList)) {
            return false;
        }
        return equals((EBusinessValueList) obj);
    }
    

    public boolean equals(EBusinessValueList other) {
        
        /*
         * list compare is broken if using hibernate.
         */
        ArrayList<EPenaltyDefinition> aux = new ArrayList<EPenaltyDefinition>(getPenalties());
        
        boolean result = importance == other.getImportance() &&
                aux.equals(other.getPenalties());
        return result;
    }
}