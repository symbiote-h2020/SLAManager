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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonParserTest {

    private JsonParser<Bean> parser;
    
    @Before
    public void init() {
        parser = new JsonParser<Bean>(Bean.class);
    }

    @Test
    public void testDeserialize() throws JsonParseException, JsonMappingException, IOException {

        String serialized = "{ \"Id\" : 1, \"Description\" : \"property\" }";
        deserialize(serialized);
    }

    @Test
    public void testDeserializeShouldFail() {

        String serialized = "{ \"Id\" : 1, \"Description\" : \"property }";
        try {
            try {
                deserialize(serialized);
                fail("Should have failed");
            } catch (SlaRuntimeException e) {
                throw new SlaRuntimeException(e.getMessage());
            }
        }
        catch(SlaRuntimeException e) {
            /* pass test */
        }
    }

    @Test
    public void testSerialize() {
        Bean b = new Bean(1L, "property");
        try {
            parser.serialize(b, System.out);
        } catch (IOException e) {
            throw new SlaRuntimeException(e.getMessage(), e);
        }
    }

    @Test
    public void testToStringT() throws JsonProcessingException {
        Bean b = new Bean(1L, "property");
        parser.toString(b);
    }
    
    @Test
    public void testConvert() {
        Map<String, Object> map = buildMap("Id", 1L, "Description", "a-description");
        Bean bean = parser.convert(map);
        System.out.println(bean);
    }
    
    @Test
    public void testConvertWithWrongKeyShouldFail() {

        Map<String, Object> map = buildMap("id", 1L, "Description", "a-description");
        checkConvertFails(map);

        map = buildMap("Id", 1L, "description", "a-description");
        checkConvertFails(map);
    }

    @Test
    public void testConvertWithWrongValueShouldFail() {

        Map<String, Object> map = buildMap("Id", "not-a-long", "Description", "a-description");
        checkConvertFails(map);

        map = buildMap("Id", 1L, "Description", new String[]{} /*not a string*/);
        checkConvertFails(map);
    }

    @Test
    public void testConvertWithObjectFail() {

        checkConvertFails(new Object());
        Map<String, Object> map = buildMap("Id", 1L, "Description", "a-description");
        checkConvertFails( new Map[]{map} );
    }

    private Map<String, Object> buildMap(String key1, Object value1, String key2, Object value2) {
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        
        return map;
    }

    private void checkConvertFails(Object o) {
        
        try {
            parser.convert(o);
            fail("Should have failed");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private Bean deserialize(String serialized) throws SlaRuntimeException {
        try {
            InputStream is = new ByteArrayInputStream(serialized.getBytes("UTF-8"));
            Bean b = parser.deserialize(is);
            return b;
        } catch (Exception e) {
            throw new SlaRuntimeException(e.getMessage(), e);
        }
    }

}
