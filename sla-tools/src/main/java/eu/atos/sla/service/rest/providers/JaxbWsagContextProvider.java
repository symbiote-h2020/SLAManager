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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.Context;
import eu.atos.sla.parser.data.wsag.Template;

@Component
@Provider
@Produces(MediaType.APPLICATION_XML)
/**
 * JAX-RS provider that generates a personalized JAXBContext to de/serialize WSAG documents that contain 
 * domain expressions.
 * 
 * WSAG let domain expressions in several parts of the agreement. 
 * E.g. Context, ServiceDescriptionTerms and CustomServiceLevel.
 * 
 * @see http://stackoverflow.com/a/18439379
 */
public class JaxbWsagContextProvider implements ContextResolver<JAXBContext> {
    private static Logger logger = LoggerFactory.getLogger(JaxbWsagContextProvider.class);
    private JAXBContext context = null;
    
    @Value("${converter.jaxb.packages:EMPTY}")
    private String contextPath = "";
    
    public JaxbWsagContextProvider() {
    }

    /**
     * Builds a context from package names.
     * @param contextPath Colon (:) separated list of packages with JAXB classes
     * @see JAXBContext#newInstance(String)
     */
    public JaxbWsagContextProvider(String contextPath) {
        this.contextPath = contextPath;
    }
    
    @Override
    public JAXBContext getContext(Class<?> type) {
        JAXBContext context = null;
        if (type == Agreement.class || type == Template.class || type == Context.class) {
            
            context = getOrBuildContext(type);
        }
        return context;
    }

    private JAXBContext getOrBuildContext(Class<?> type) {
        
        if(context == null) {
            logger.debug("Creating WSAG ContextProvider; contextPath={}", contextPath);
            try {
                context = JAXBContext.newInstance(contextPath);
            } catch (JAXBException e) {
                logger.error("Cannot get context for type " + type.getName());
            }
        }
        return context;
    }
}
