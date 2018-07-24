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
package eu.atos.sla.modelconversion.simple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.atos.sla.datamodel.EPolicy;
import eu.atos.sla.modelconversion.ModelConversionException;

public class ServiceLevelParser {
    private static Logger logger = LoggerFactory.getLogger(ServiceLevelParser.class);
    
    /**
     * A violation is raised if there are "count" occurrences of a breach inside an interval of "interval"
     * seconds.
     */
    public static class Window {
        private int count = 1;
        private int interval = 0;
        
        /**
         * Default constructor where no window is specified in SLO.
         */
        Window() {
        }

        Window(int count, int interval) {
            this.count = count;
            this.interval = interval;
        }
        
        public int getCount() {
            return count;
        }

        public int getInterval() {
            return interval;
        }
        
        @JsonIgnore
        public Date getDateInterval() {
            return new Date(interval * 1000);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof ServiceLevelParser.Window) {
                ServiceLevelParser.Window other = (ServiceLevelParser.Window) obj;
                
                return other.count == count && other.interval == interval;
            }
            return false; 
        }

        @Override
        public String toString() {
            return String.format("Window [count=%s, interval=%s]", count, interval);
        }
    }
    
    public static class Result {
        public static List<ServiceLevelParser.Window> DEFAULT_WINDOWS = java.util.Collections.singletonList(new Window());
        
        String constraint;
        List<ServiceLevelParser.Window> windows;
        
        public Result() {
            this.windows = DEFAULT_WINDOWS;
        }
        public String getConstraint() {
            return constraint;
        }
        
        public List<ServiceLevelParser.Window> getWindows() {
            return windows;
        }
    }
    
    protected static ServiceLevelParser.Result parse(String serviceLevel) throws ModelConversionException {
        ObjectMapper mapper = new ObjectMapper();
        
        String constraint = null;
        List<ServiceLevelParser.Window> windows = null;
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(serviceLevel);
            JsonNode constraintNode = rootNode.path("constraint");
            JsonNode windowsNode = rootNode.path("windows");
            if (windowsNode.isMissingNode()) {
                windowsNode = rootNode.path("policies");
            }
            constraint = textOrJson(constraintNode);
            windows = parseWindows(windowsNode, mapper);
            
            if (constraint==null) throw new ModelConversionException(serviceLevel+" didn't contain the constraint keyword");
            ServiceLevelParser.Result result = new Result();
            result.constraint = constraint;
            result.windows = windows;
            
            return result;
        } catch (JsonProcessingException e) {
            logger.error("Error parsing "+serviceLevel, e);
            throw new ModelConversionException("Error parsing "+serviceLevel+ " message:"+ e.getMessage());
        } catch (IOException e) {
            logger.error("Error parsing "+serviceLevel, e);
            throw new ModelConversionException("Error parsing "+serviceLevel+ " message:"+ e.getMessage());
        }
    }

    protected static List<EPolicy> toPolicies(List<ServiceLevelParser.Window> windows) {
        List<EPolicy> result = new ArrayList<EPolicy>();
        for (ServiceLevelParser.Window w : windows) {
            EPolicy p = new EPolicy(w.getCount(), w.getDateInterval());
            result.add(p);
        }
        return result;
    }

    /**
     * Returns the text value of a node or its inner string representation.
     * 
     * textOrJson( "constraint" : "performance < 10" ) -> "performance < 10"
     * textOrJson( "constraint" : { "hasMaxValue": 10 } ) -> "{\"hasMaxValue\": 10}"
     */
    private static String textOrJson(JsonNode constraintNode) {
        String constraint = null;
        
        if (!constraintNode.isMissingNode()) {
            constraint = constraintNode.textValue();
            if (constraint == null) {
                constraint = constraintNode.toString();
            }
        }
        return constraint;
    }
    
    private static List<ServiceLevelParser.Window> parseWindows(JsonNode windowsNode, ObjectMapper mapper) 
            throws JsonParseException, JsonMappingException, IOException {
        
        List<ServiceLevelParser.Window> result;
        if (windowsNode.isMissingNode()) {
            result = Result.DEFAULT_WINDOWS;
        }
        else {
            result = mapper.readValue(windowsNode.toString(), 
                    mapper.getTypeFactory().constructCollectionType(List.class, ServiceLevelParser.Window.class));
        }
        return result;
    }
}