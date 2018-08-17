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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;

/**
 * A POJO object storing a provider's info.
 * 
 * @author rsosa, prey
 */
@Entity(name = "Provider")
@Table(name = "provider")
@NamedQueries({
        @NamedQuery(name = EProvider.QUERY_FIND_ALL, query = "SELECT p FROM Provider p"),
        @NamedQuery(name = EProvider.QUERY_FIND_BY_UUID, query = "SELECT p FROM Provider p where p.uuid = :uuid"),
        @NamedQuery(name = EProvider.QUERY_FIND_BY_NAME, query = "SELECT p FROM Provider p where p.name = :name") })
public class EProvider extends AbstractEntity<EProvider> implements Serializable {
    
    public final static String QUERY_FIND_ALL = "Provider.findAll";
    public final static String QUERY_FIND_BY_UUID = "Provider.getByUuid";
    public final static String QUERY_FIND_BY_NAME = "Provider.getByName";

    private static final long serialVersionUID = -6655604906240872609L;

    private String uuid;
    private String name;
    private List<ETemplate> templates;

    public EProvider() {
    }
    
    public EProvider(Long id, String uuid, String name) {
        this.setId(id);
        this.uuid = uuid;
        this.name = name;
    }
    
    /**
     * The provider is recognized by external parties by this UUID
     */
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    /**
     * Provider's name
     */
    @Column(name = "name", unique=true,nullable=false)
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Template list 
     */
    @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = ETemplate.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", referencedColumnName = "id", nullable = true)
    public List<ETemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(List<ETemplate> templates) {
        this.templates = templates;
    }

    public void addTemplate(ETemplate template){
        if (templates == null) templates = new ArrayList<ETemplate>();
        templates.add(template);
    }
}
