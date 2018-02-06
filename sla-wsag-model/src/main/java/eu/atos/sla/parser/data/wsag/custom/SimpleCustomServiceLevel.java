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
package eu.atos.sla.parser.data.wsag.custom;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.Duration;

import eu.atos.sla.parser.DurationUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

@XmlRootElement(name = "Slo")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter(value = AccessLevel.NONE)
@ToString
@Builder
public class SimpleCustomServiceLevel {
    
    @XmlElement(name = "Constraint")
    private String constraint;
    
    @XmlElement(name = "ViolationWindow")
    @Singular
    private List<ViolationWindow> violationWindows;

    @XmlElement(name = "Description")
    private String description;
    
    public SimpleCustomServiceLevel() {
        violationWindows = new ArrayList<SimpleCustomServiceLevel.ViolationWindow>();
        constraint = "";
        description = "";
    }
    
    public SimpleCustomServiceLevel(String constraint, List<ViolationWindow> violationWindows, String description) {
        this.constraint = constraint;
        this.violationWindows = new ArrayList<SimpleCustomServiceLevel.ViolationWindow>();
        this.violationWindows.addAll(violationWindows);
        this.description = description;
    }

    @Override
    public int hashCode() {
        /*
         * constraint not take into account because processed differently in equals()
         */
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((violationWindows == null) ? 0 : violationWindows.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleCustomServiceLevel other = (SimpleCustomServiceLevel) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (violationWindows == null) {
            if (other.violationWindows != null)
                return false;
        } else if (!violationWindows.equals(other.violationWindows))
            return false;

        /* special handling of constraint: threshold to be considered by value */
        if (constraint == null) {
            if (other.constraint != null)
                return false;
        } 
        else {
            ConstraintParts thisConstraintParts = new ConstraintParts(this.constraint);
            ConstraintParts otherConstraintParts = new ConstraintParts(other.constraint);
            
            if (!thisConstraintParts.equals(otherConstraintParts)) {
                return false;
            }
        }
        
        return true;
    }


    @Data
    @Setter(value = AccessLevel.NONE)
    @Builder
    public static class ViolationWindow {
        @XmlAttribute
        private int count;
        @XmlAttribute
        private Duration interval;
    
        public ViolationWindow() {
            count = 1;
            interval = DurationUtils.EMPTY_DURATION;
        }
        
        public ViolationWindow(int count, Duration interval) {
            this.count = count;
            this.interval = interval;
        }
    }
    
    private static final class ConstraintParts {
        private String metric;
        private String operator;
        private Double threshold;
        
        public ConstraintParts(String constraint) {
            int thresholdIdx = constraint.lastIndexOf(' ') + 1;
            String thresholdStr = constraint.substring(thresholdIdx);
            threshold = Double.valueOf(thresholdStr);
            
            String rest =  constraint.substring(0, thresholdIdx - 1);
            int operatorIdx = rest.lastIndexOf(' ') + 1;
            operator = rest.substring(operatorIdx);
            
            metric = rest.substring(0, operatorIdx - 1);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((metric == null) ? 0 : metric.hashCode());
            result = prime * result + ((operator == null) ? 0 : operator.hashCode());
            result = prime * result + ((threshold == null) ? 0 : threshold.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ConstraintParts other = (ConstraintParts) obj;
            if (metric == null) {
                if (other.metric != null)
                    return false;
            } else if (!metric.equals(other.metric))
                return false;
            if (operator == null) {
                if (other.operator != null)
                    return false;
            } else if (!operator.equals(other.operator))
                return false;
            if (threshold == null) {
                if (other.threshold != null)
                    return false;
            } else if (!threshold.equals(other.threshold))
                return false;
            return true;
        }
    }
}
