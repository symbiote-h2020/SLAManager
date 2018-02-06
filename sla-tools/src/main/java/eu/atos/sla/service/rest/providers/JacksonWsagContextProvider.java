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
package eu.atos.sla.service.rest.providers;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import eu.atos.sla.JsonParser;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonWsagContextProvider implements ContextResolver<ObjectMapper> {
    private static final Logger logger = LoggerFactory.getLogger(JacksonWsagContextProvider.class);
    
    private final ObjectMapper defaultObjectMapper;

    public JacksonWsagContextProvider() {
        
        defaultObjectMapper = createDefaultMapper();
        logger.info("Creating JacksonWsagContextProvider");
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        
        logger.debug("JacksonWsagContextProvider.getContext()");
        return defaultObjectMapper;
    }
    
    public static ObjectMapper createDefaultMapper() {
        final ObjectMapper jackson = JsonParser.ObjectMapperFactory.createMapper();
        
        jackson.enable(SerializationFeature.INDENT_OUTPUT);
        jackson.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        jackson.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return jackson;
     }
}
