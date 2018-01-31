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
package eu.atos.sla.parser.data.wsag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.atos.sla.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Context")
@Builder
@Data
@AllArgsConstructor
public class Context {
    
    /**
     * ServiceProvider element must be one of these.
     */
    public enum ServiceProvider {
        AGREEMENT_INITIATOR("AgreementInitiator"), 
        AGREEMENT_RESPONDER("AgreementResponder");
        
        String label;
        private ServiceProvider(String label) {
            
            this.label = label;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    @XmlElement(name = "AgreementInitiator")
    private String agreementInitiator;
    @XmlElement(name = "AgreementResponder")
    private String agreementResponder;
    @XmlElement(name = "ServiceProvider")
    private String serviceProvider;

    @XmlElement(name = "ExpirationTime")
    private Date  expirationTime;
    @XmlElement(name = "TemplateId")
    private String templateId;
    @XmlElement(name = "Service", namespace="http://sla.atos.eu")
    private String service;

    @XmlAnyElement(lax = true)
    @Singular("anyone")
    private List<Object> any;
    
    public Context() {
        any = new ArrayList<Object>();
    }

    public String getAgreementInitiator() {
        return agreementInitiator;
    }

    public void setAgreementInitiator(String agreementInitiator) {
        this.agreementInitiator = agreementInitiator;
    }

    public String getAgreementResponder() {
        return agreementResponder;
    }

    public void setAgreementResponder(String agreementResponder) {
        this.agreementResponder = agreementResponder;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public List<Object> getAny() {
        return any;
    }
    
    public void setAny(List<Object> any) {
        this.any.clear();
        this.any.addAll(any);
    }
    
    /**
     * Returns content of any as instance of given class. If not found, return null.
     */
    public <T> T getAnyAsObject(Class<T> class_) {
        JsonParser<T> parser = new JsonParser<T>(class_);
        
        for (Object o : any) {
            if (class_.isInstance(o)) {
                return class_.cast(o);
            }
            /*
             * If no directly instance found, try with jackson
             */
            try {
                return parser.convert(o);
            } catch (IllegalArgumentException e) {
                /* try with next */
            }
        }
        
        throw new ClassCastException("Trying to cast CustomServiceLevel as " + class_.getName() + " and found " + any);
    }

}
