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
package eu.atos.sla;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * Json serializer/deserializer from/to class that handles jaxb and jackson (in this order) annotations by default.
 *
 * @see https://github.com/FasterXML/jackson-module-jaxb-annotations
 */
public class JsonParser<T> {

    public static class ObjectMapperFactory {
        
        public static ObjectMapper createMapper() {
            
            ObjectMapper mapper = new ObjectMapper();
            AnnotationIntrospector pair = AnnotationIntrospector.pair(
                    new JacksonAnnotationIntrospector(), 
                    new JaxbAnnotationIntrospector(mapper.getTypeFactory())
            );
            mapper.setAnnotationIntrospector(pair);

            return mapper;
        }
    }
    
    private static ObjectMapper mapper = ObjectMapperFactory.createMapper();
    private Class<T> class_;
    
    public JsonParser(Class<T> class_) {
        this.class_ = class_;
    }
    
    /**
     * Deserializes from an InputStream to T class
     * 
     * @see ObjectMapper#readValue(InputStream, Class)
     */
    public T deserialize(InputStream is) throws JsonParseException, JsonMappingException, IOException {
        
        T result = mapper.readValue(is, class_);
        return result;
    }

    /**
     * Serializes a T instance to an open OutputStream
     *
     * @see ObjectMapper#writeValue(OutputStream, Object)
     */
    public void serialize(T t, OutputStream os) throws JsonGenerationException, JsonMappingException, IOException {
        
        mapper.writerWithDefaultPrettyPrinter().writeValue(os, t);
    }
    
    /**
     * Serializes a T instance to a String
     * 
     * @see ObjectMapper#writeValueAsString(Object)
     */
    public String toString(T t) throws JsonProcessingException {
        String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(t);
        return result;
    }
    
    /**
     * Converts a map or array structure in memory to an instance of class T.
     * @throws IllegalArgumentException on error
     * 
     * @see ObjectMapper#convertValue(Object, Class)
     */
    public T convert(Object o) throws IllegalArgumentException {

        return mapper.convertValue(o, class_);
    }

}
