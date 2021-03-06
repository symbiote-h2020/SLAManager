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
package eu.atos.sla.parser.data.wsag.tests;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@XmlRootElement(namespace="http://tests.sla.atos.eu")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Setter(value=AccessLevel.NONE)
public class Foo {
    
    public Foo() {
    }
    
    public Foo(String attribute, String property, FooInner child) {
        this.attribute = attribute;
        this.property = property;
        this.child = child;
    }
    
    @XmlAttribute
    String attribute;
    @XmlElement
    String property;
    @XmlElement
    FooInner child;
    
}