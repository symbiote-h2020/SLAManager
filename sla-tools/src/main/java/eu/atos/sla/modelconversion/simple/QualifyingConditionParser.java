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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.modelconversion.ModelConversionException;

public class QualifyingConditionParser {
    private static final Logger logger = LoggerFactory.getLogger(QualifyingConditionParser.class);
    
    static private final String AT_END = "AT_END";
    static private final String SCHEDULEx = "SCHEDULEx";
    public static class Result {
        int samplingperiodFactor;
        
        protected int getSamplingPeriodFactor() {
            return samplingperiodFactor;
        }
    }
    
    protected static QualifyingConditionParser.Result parse(String qualifyingCondition) throws ModelConversionException {
        ObjectMapper mapper = new ObjectMapper();
        
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(qualifyingCondition);
            JsonNode samplingperiodNode = rootNode.path("samplingperiodfactor");
            logger.debug("samplingperiodNode: "+samplingperiodNode);
            
            String samplingperiodfactor = textOrJson(samplingperiodNode);

            if (samplingperiodfactor==null) throw new ModelConversionException(qualifyingCondition+" didn't contain the samplingperiodfactor keyword");
            QualifyingConditionParser.Result result = new Result();
            if ((samplingperiodfactor.startsWith(SCHEDULEx)) || (samplingperiodfactor.startsWith(AT_END))){
                if (samplingperiodfactor.startsWith(SCHEDULEx)){ 
                    try{
                        result.samplingperiodFactor = Integer.valueOf(samplingperiodfactor.substring(SCHEDULEx.length()).trim());
                    }catch (NumberFormatException e){
                        throw new ModelConversionException(qualifyingCondition+" "+SCHEDULEx+" must be followed by a decimal");
                    }
                }
                if (samplingperiodfactor.startsWith(AT_END)){ 
                    result.samplingperiodFactor = EGuaranteeTerm.ENFORCED_AT_END;
                }
            }else
                throw new ModelConversionException(qualifyingCondition+" must be a multiple from schedule or be executed at the end. Make sure the value starts with "+SCHEDULEx+" or has the word "+AT_END);
            
            return result;
        } catch (JsonProcessingException e) {
            logger.error("Error parsing "+qualifyingCondition, e);
            throw new ModelConversionException("Error parsing "+qualifyingCondition+ " message:"+ e.getMessage());
        } catch (IOException e) {
            logger.error("Error parsing "+qualifyingCondition, e);
            throw new ModelConversionException("Error parsing "+qualifyingCondition+ " message:"+ e.getMessage());
        }
    }

    
    private static String textOrJson(JsonNode samplingperiodNode) {
        String value = null;
        
        if (!samplingperiodNode.isMissingNode()) {
            value = samplingperiodNode.textValue();
            if (value == null) {
                value = samplingperiodNode.toString();
            }
        }
        return value;
    }
    
}