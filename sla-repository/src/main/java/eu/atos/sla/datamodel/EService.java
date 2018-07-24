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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;



/**
 * A POJO object storing a provider's info.
 * 
 * @author rsosa
 */
@Entity(name = "Service")
@Table(name = "provider")
@NamedQueries({
        @NamedQuery(name = EService.QUERY_FIND_ALL, query = "SELECT p FROM Service p"),
        @NamedQuery(name = EService.QUERY_FIND_BY_UUID, query = "SELECT p FROM Service p where p.uuid = :uuid"),
        @NamedQuery(name = EService.QUERY_FIND_BY_NAME, query = "SELECT p FROM Service p where p.name = :name") })
public class EService extends AbstractEntity<EService> implements Serializable {
    
    public final static String QUERY_FIND_ALL = "Service.findAll";
    public final static String QUERY_FIND_BY_UUID = "Service.getByUuid";
    public final static String QUERY_FIND_BY_NAME = "Service.getByName";

    private static final long serialVersionUID = -6655604906240872609L;

    Long id; 
    String uuid;
    String name;

    public EService() {
    }
    
    public EService(Long id, String uuid, String name) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
    }
    
    /*
     * Internal generated id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * The service is recognized by external parties by this UUID
     */
    @Column(name = "uuid", unique = true)
    public String getUuid() {
        return uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    /**
     * Service's name
     */
    @Column(name = "name")
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
}
