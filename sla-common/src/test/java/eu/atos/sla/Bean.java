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
package eu.atos.sla;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Bean {
    
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Property {
        
        @XmlAttribute
        private String key;
        
        @XmlAttribute
        private String value;
        
        public Property() {
        }
        
        public Property(String key, String value) {
            super();
            this.key = key;
            this.value = value;
        }
    
        public String getKey() {
            return key;
        }
        
        public String getValue() {
            return value;
        }
    }

    @XmlAttribute(name = "Id")
    private Long id;

    @XmlAttribute(name = "Description")
    private String description;
    
    @XmlAnyElement(lax = true)
    protected List<Object> properties;
    
    public Bean() {
        this.properties = new ArrayList<Object>();
    }
    
    public Bean(Long id, String description) {
        this.id = id;
        this.description = description;
        this.properties = new ArrayList<Object>();
    }

    public Long getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void addProperty(Object property) {
        this.properties.add(property);
    }
}