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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;

@Entity(name = "ServiceProperties")
@Table(name = "service_properties")
@NamedQueries({
        @NamedQuery(name = EServiceProperties.QUERY_FIND_ALL, query = "SELECT p FROM ServiceProperties p"),
        @NamedQuery(name = EServiceProperties.QUERY_FIND_BY_NAME, query = "SELECT p FROM ServiceProperties p where p.name = :name") })
public class EServiceProperties extends AbstractEntity<EServiceProperties> implements Serializable {

    public final static String QUERY_FIND_ALL = "ServiceProperties.findAll";
    public final static String QUERY_FIND_BY_NAME = "ServiceProperties.findByName";

    private static final long serialVersionUID = 8160422880355293304L;
    private String name;
    private String serviceName;

    private List<EVariable> variableSet;

    public EServiceProperties() {
    }

    /**
     * Name of this ServiceProperties
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * ServiceName
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_name")
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    
    @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = EVariable.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "service_properties_id", referencedColumnName = "id", nullable = true)
    public List<EVariable> getVariableSet() {
        return variableSet;
    }

    public void setVariableSet(List<EVariable> variableSet) {
        this.variableSet = variableSet;
    }

}
