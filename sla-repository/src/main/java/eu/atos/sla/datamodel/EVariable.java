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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity(name = "Variable")
@Table(name = "variable")
@NamedQueries({
        @NamedQuery(name = EVariable.QUERY_FIND_ALL, query = "SELECT p FROM Variable p"),
        @NamedQuery(name = EVariable.QUERY_FIND_BY_NAME, query = "SELECT p FROM Variable p where p.name = :name") })
public class EVariable extends AbstractEntity<EVariable> implements Serializable {

    public final static String QUERY_FIND_ALL = "Variable.findAll";
    public final static String QUERY_FIND_BY_NAME = "Variable.findByName";

    private static final long serialVersionUID = 36344868689340922L;
    private String name;
    private String metric;
    private String location;

    /**
     * Name of this ServiceProperty
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Xsd type of this property
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metric")
    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    /**
     * Reference to a field in the service terms. In our sla, it is a "conceptual" reference.
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
