/**
 * Copyright 2017 Atos
 * Contact: Atos <roman.sosa@atos.net>
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
package eu.atos.sla.notification;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.atos.sla.parser.data.Penalty;
import eu.atos.sla.parser.data.Violation;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "notification")
public class Notification {

    private List<Violation> violations = new ArrayList<>();
    private List<Penalty> penalties = new ArrayList<>();
    
    public Notification() {
        
    }
    
    public Notification(List<Violation> violations, List<Penalty> penalties) {

        this.violations = violations;
        this.penalties = penalties;
    }
    
    public List<Violation> getViolations() {
        return violations;
    }
    
    public List<Penalty> getPenalties() {
        return penalties;
    }
}
