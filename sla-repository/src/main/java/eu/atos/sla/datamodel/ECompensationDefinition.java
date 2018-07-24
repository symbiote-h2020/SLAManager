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
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

/**
 * This element expresses the reward or penalty to be assessed for meeting (or not) an objetive.
 *
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class ECompensationDefinition<T> extends AbstractEntity<T> implements Serializable {
    
    public static enum CompensationKind {
        REWARD,
        CUSTOM_REWARD,
        PENALTY,
        CUSTOM_PENALTY,
        UNKNOWN
    }

    private static final long serialVersionUID = 1L;
    
    protected static int DEFAULT_COUNT = 0;
    protected static Date DEFAULT_INTERVAL = new Date(0);
    protected static String DEFAULT_VALUE_EXPRESSION = "";
    protected static String DEFAULT_VALUE_UNIT = "";
    protected static String DEFAULT_ACTION = "";
    protected static String DEFAULT_VALIDITY = "";
    
    @Column(name="kind", nullable=false)
    @Enumerated(EnumType.STRING)
    private CompensationKind kind;

    @Column(name = "time_interval", nullable=false)
    private Date timeInterval;
    
    @Column(name="number", nullable=false)
    private int count;
    
    @Column(name="value_unit", nullable=false)
    private String valueUnit;
    
    @Column(name="value_expression", nullable=false)
    private String valueExpression;
    
    @Column(name="action", nullable=false)
    private String action;
    
    @Column(name="validity", nullable=false)
    private String validity;
    
    public ECompensationDefinition() {
        this.kind = ECompensationDefinition.CompensationKind.UNKNOWN;
        this.timeInterval = DEFAULT_INTERVAL;
        this.count = DEFAULT_COUNT;
        this.action = DEFAULT_ACTION;
        this.validity = DEFAULT_VALIDITY;
        this.valueExpression = DEFAULT_VALUE_EXPRESSION;
        this.valueUnit = DEFAULT_VALUE_UNIT;
    }
    
    /**
     * Constructor for wsag compensations
     */
    protected ECompensationDefinition(CompensationKind kind, Date timeInterval,
            String valueUnit, String valueExpression) {
        
        checkNotNull(kind, "kind");
        checkNotNull(timeInterval, "timeInterval");
        checkNotNull(valueUnit, "valueUnit");
        checkNotNull(valueExpression, "valueExpression");

        this.kind = kind;
        this.timeInterval = timeInterval;
        this.valueUnit = valueUnit;
        this.valueExpression = valueExpression;

        this.count = DEFAULT_COUNT;
        this.action = DEFAULT_ACTION;
        this.validity = DEFAULT_VALIDITY;
    }

    /**
     * Constructor for wsag compensations
     */
    protected ECompensationDefinition(CompensationKind kind, 
            int count, String valueUnit, String valueExpression) {
        
        checkNotNull(kind, "kind");
        checkNotNull(valueUnit, "valueUnit");
        checkNotNull(valueExpression, "valueExpression");

        this.kind = kind;
        this.count = count;
        this.valueUnit = valueUnit;
        this.valueExpression = valueExpression;
        
        this.timeInterval = DEFAULT_INTERVAL;
        this.action = DEFAULT_ACTION;
        this.validity = DEFAULT_VALIDITY;
    }

    /**
     * Constructor for extended compensations
     */
    protected ECompensationDefinition(CompensationKind kind, int count, Date timeInterval, String action, 
            String valueUnit, String valueExpression, String validity) {
        
        checkNotNull(kind, "kind");
        checkNotNull(timeInterval, "timeInterval");
        checkNotNull(action, "action");
        checkNotNull(valueUnit, "valueUnit");
        checkNotNull(valueExpression, "valueExpression");
        checkNotNull(validity, "validity");

        this.kind = kind;
        this.count = count;
        this.timeInterval = timeInterval;
        this.valueUnit = valueUnit;
        this.valueExpression = valueExpression;
        this.action = action;
        this.validity = validity;
    }
    
    private void checkNotNull(Object o, String property) {
        if (o == null) {
            throw new NullPointerException(property + " cannot be null");
        }
    }

    /**
     * Type of compensation: reward or penalty.
     */
    public CompensationKind getKind() {
        return kind;
    }

    /**
     * When present, defines the assessment interval as a duration.
     * 
     * One of timeInterval or count MUST be specified. 
     */
    public Date getTimeInterval() {
        return timeInterval;
    }

    /**
     * When present, defines the assessment interval as a service specific count, such as
     * number of invocations. 
     * 
     * One of timeInterval or count MUST be specified. 
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Optional element that defines the unit for assessing penalty, such as USD.
     */
    public String getValueUnit() {
        return valueUnit;
    }

    /**
     * This element defines the assessment amount, which can be an integer, a float or an 
     * arbitrary domain-specific expression.
     */
    public String getValueExpression() {
        return valueExpression;
    }
    
    /**
     * In extended compensations, defines the domain-specific type of compensation, such as 
     * "discount", "terminate", "service"...
     */
    public String getAction() {
        return action;
    }
    
    /**
     * In extended compensations, defines the time interval where the action should take place. E.g., 
     * a discount of 10% with a validity of one day (i.e., the prize of that day will have a discount of a 10%).
     * 
     * The validity must be expressed in xs:duration format.
     * 
     * @see http://www.w3.org/TR/xmlschema-2/#duration
     */
    public String getValidity() {
        return validity;
    }
    
    @Override
    public String toString() {
        String fmt = "";
        
        fmt = "<CompensationDefinition("
                + "kind=%s,timeInterval=%d ms,count=%d,action='%s',valueUnit=%s,valueExpression=%s,validity=%s)>";
        return String.format(fmt, 
                kind.toString(),
                timeInterval.getTime(),
                count,
                action,
                valueUnit,
                valueExpression,
                validity);
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + count;
        result = prime * result + ((kind == null) ? 0 : kind.hashCode());
        result = prime * result
                + ((timeInterval == null) ? 0 : timeInterval.hashCode());
        result = prime * result
                + ((validity == null) ? 0 : validity.hashCode());
        result = prime * result
                + ((valueExpression == null) ? 0 : valueExpression.hashCode());
        result = prime * result
                + ((valueUnit == null) ? 0 : valueUnit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ECompensationDefinition)) {
            return false;
        }
        ECompensationDefinition other = (ECompensationDefinition) obj;
        if (action == null) {
            if (other.action != null) {
                return false;
            }
        } else if (!action.equals(other.action)) {
            return false;
        }
        if (count != other.count) {
            return false;
        }
        if (kind != other.kind) {
            return false;
        }
        /*
         * Direct Date compare gives a lot of problems with timezones
         */
        
        if (timeInterval == null) {
            if (other.timeInterval != null) {
                return false;
            }
        } else if (timeInterval.getTime() != other.timeInterval.getTime()) {
            return false;
        }
        if (validity == null) {
            if (other.validity != null) {
                return false;
            }
        } else if (!validity.equals(other.validity)) {
            return false;
        }
        if (valueExpression == null) {
            if (other.valueExpression != null) {
                return false;
            }
        } else if (!valueExpression.equals(other.valueExpression)) {
            return false;
        }
        if (valueUnit == null) {
            if (other.valueUnit != null) {
                return false;
            }
        } else if (!valueUnit.equals(other.valueUnit)) {
            return false;
        }
        return true;
    }
}
