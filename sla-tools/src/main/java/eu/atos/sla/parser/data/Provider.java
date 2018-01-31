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
package eu.atos.sla.parser.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.atos.sla.datamodel.EProvider;

/**
 * A POJO object storing a provider's info.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "provider")
public class Provider{

    @XmlElement(name = "uuid")
    String uuid;
    @XmlElement(name = "name")
    String name;

    public Provider() {
    }

    public Provider(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Provider(EProvider p) {
        this.uuid = p.getUuid();
        this.name = p.getName();
    }
    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
