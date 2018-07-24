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

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "PenaltyDefinition")
@Table(name="penaltydefinition")
@Access(AccessType.FIELD)
public class EPenaltyDefinition extends ECompensationDefinition<EPenaltyDefinition> {
    private static final long serialVersionUID = 1L;

    public EPenaltyDefinition() {
        super();
    }
    
    public EPenaltyDefinition(Date timeInterval,
            String valueUnit, String valueExpression) {

        super(CompensationKind.PENALTY, timeInterval, valueUnit, valueExpression);
    }

    public EPenaltyDefinition(int count, String valueUnit, String valueExpression) {

        super(CompensationKind.PENALTY, count, valueUnit, valueExpression);
    }

    public EPenaltyDefinition(int count, Date timeInterval, String action, String valueUnit, String valueExpression, 
            String validity) {
        
        super(CompensationKind.CUSTOM_PENALTY, count, timeInterval, action, valueUnit, valueExpression, validity);
    }
}
