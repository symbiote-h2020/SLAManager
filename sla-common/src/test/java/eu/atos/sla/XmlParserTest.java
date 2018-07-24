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
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;

public class XmlParserTest {

    XmlParser<Bean> parser;
    
    @Before
    public void init() throws JAXBException {
        parser = new XmlParser<Bean>(Bean.class);
    }
    
    @Test
    public void testDeserializeShouldSucceed() throws SlaCheckedException {
        Bean b;
        
        b = deserialize("<bean Id=\"1\" Description=\"property\"/>");
        assertEquals((Long)1L, b.getId());
        assertEquals("property", b.getDescription());
    }

    @Test
    public void testDeserializeWithAny() throws SlaCheckedException, JAXBException {
        Bean b;

        parser = new XmlParser<Bean>(Bean.class, Bean.Property.class);

        b = deserialize("<bean Id=\"1\" Description=\"property\"> <property key=\"key1\" value=\"value1\"/> </bean>");
        assertEquals((Long)1L, b.getId());
        assertEquals("property", b.getDescription());
        
        assertEquals(1, b.properties.size());
        Bean.Property p = (Bean.Property) (b.properties.get(0));
        assertEquals("key1", p.getKey());
        assertEquals("value1", p.getValue());
    }

    @Test
    public void testDeserializeShouldFail() {
        try {
            deserialize("<beana fails=\"1\"/>");
            fail("Should have failed");
        } catch (SlaCheckedException e) {
            /* pass test */
        }
    }
    
    @Test
    public void testDeserializeUsingPackageNames() throws JAXBException, SlaCheckedException {
        Bean b;
        
        parser = new XmlParser<Bean>("eu.atos.sla");
        b = deserialize("<bean Id=\"1\" Description=\"property\"> <property key=\"key1\" value=\"value1\"/> </bean>");
        assertEquals((Long)1L, b.getId());
        assertEquals("property", b.getDescription());
        
        assertEquals(1, b.properties.size());
        Bean.Property p = (Bean.Property) (b.properties.get(0));
        assertEquals("key1", p.getKey());
        assertEquals("value1", p.getValue());
    }
    
    @Test
    public void testSerialize() {
        
        Bean b = new Bean(1L, "property");
        try {
            parser.serialize(b, System.out);
        } catch (JAXBException e) {
            throw new SlaRuntimeException(e.getMessage(), e);
        }
    }

    @Test
    public void testSerializeWithAny() throws JAXBException {
        
        parser = new XmlParser<Bean>(Bean.class, Bean.Property.class);
        Bean b = new Bean(1L, "property");
        b.addProperty(new Bean.Property("key1", "value1"));
        try {
            parser.serialize(b, System.out);
        } catch (JAXBException e) {
            throw new SlaRuntimeException(e.getMessage(), e);
        }
    }

    @Test
    public void testSerializeUsingPackageNames() throws JAXBException {
        
        parser = new XmlParser<Bean>("eu.atos.sla");
        Bean b = new Bean(1L, "property");
        b.addProperty(new Bean.Property("key1", "value1"));
        try {
            parser.serialize(b, System.out);
        } catch (JAXBException e) {
            throw new SlaRuntimeException(e.getMessage(), e);
        }
    }

    @Test
    public void testSerializeShouldFail() {
        
        Bean b = new Bean(1L, "property");
        b.addProperty(new Bean.Property("key1", "value1"));
        try {
            try {
                parser.serialize(b, System.out);
                fail("Should have failed");
            } catch (JAXBException e) {
                throw new SlaCheckedException(e.getMessage());
            }
        }
        catch (SlaCheckedException e) {
            /* pass test */
        }
    }
    
    @Test
    public void testToString() {
        
        Bean b = new Bean(1L, "property");
        try {
            parser.toString(b);
        } catch (JAXBException e) {
            throw new SlaRuntimeException("toString() failed");
        }
    }
    
    private Bean deserialize(String serialized) throws SlaCheckedException {
        try {
            InputStream is = new ByteArrayInputStream(serialized.getBytes("UTF-8"));
            Bean b = parser.deserialize(is);

            return b;
        } catch (Exception e) {
            throw new SlaCheckedException(e.getMessage(), e);
        }
    }
}
