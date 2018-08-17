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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;

import eu.atos.sla.JsonParser;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CustomServiceLevel {
    
    @XmlAnyElement(lax = true)
    private List<Object> any;
    
    public CustomServiceLevel() {
        any = new ArrayList<Object>();
    }
    
    public CustomServiceLevel(Object anyone) {
        any = new ArrayList<Object>();
        any.add(anyone);
    }
    
    public List<Object> getAny() {
        return any;
    }
    
    /**
     * Returns content of customservicelevel as instance of given class. If not found, return null.
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
    
    public void setAny(List<Object> any) {
        this.any.addAll(any);
    }
}