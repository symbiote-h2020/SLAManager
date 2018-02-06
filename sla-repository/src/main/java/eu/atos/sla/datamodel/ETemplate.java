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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity(name = "Template")
@Table(name = "template")
@NamedQueries({
        @NamedQuery(name = ETemplate.QUERY_FIND_ALL, query = "SELECT t FROM Template t"),
        @NamedQuery(name = ETemplate.QUERY_FIND_BY_UUID, query = "SELECT t FROM Template t WHERE t.uuid = :uuid"),
        @NamedQuery(name = ETemplate.QUERY_SEARCH, query = "SELECT t FROM Template t "
                + "WHERE (:providerId is null or t.provider.uuid = :providerId) "
                + "AND (:flagServiceIds is null or t.serviceId in (:serviceIds))"),
        @NamedQuery(name = ETemplate.QUERY_FIND_BY_AGREEMENT, query = "SELECT a.template FROM Agreement a "
                + "WHERE a.agreementId = :agreementId"),
        })
public class ETemplate extends AbstractEntity<ETemplate> implements Serializable {

    public final static String QUERY_FIND_ALL = "Template.findAll";
    public final static String QUERY_FIND_BY_UUID = "Template.getByUuid";
    public final static String QUERY_SEARCH = "Template.search";
    public final static String QUERY_FIND_BY_AGREEMENT = "Template.getByAgreement";
    

    private static final long serialVersionUID = -6390910175637896300L;
    private String uuid;
    private String text;
    private String serviceId;
    private String name;
    private EProvider provider;

    public ETemplate() {
    }

    @Override
    public int hashCode() {
        
        return uuid.hashCode();
    }
     
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ETemplate)) {
            return false;
        }
        ETemplate that = (ETemplate) obj;
        return uuid.equals(that.getUuid());
    }
     
    /**
     * This template is recognized by external parties by this internally generated UUID. 
     */
    @Column(name = "uuid", unique = true, nullable = false)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Template body. This is an ws-agreement-compliant xml.
     * NOTE: String? Maybe there is a better type.
     */
    @Column(name = "text", columnDefinition = "longtext", nullable = false)
    @Lob
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /** 
     * Service from the context
     */
    @Column(name = "service_id", nullable = true)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /** 
     * Name from the template
     */
    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Provider of this template
     */
    @ManyToOne(targetEntity = EProvider.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", referencedColumnName = "id", nullable = false)
    public EProvider getProvider() {
        return provider;
    }

    public void setProvider(EProvider provider) {
        this.provider = provider;
    }

}
