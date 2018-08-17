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
package eu.atos.sla.parser.data.wsag;

import static org.junit.Assert.*;

import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import eu.atos.sla.XmlParser;
import eu.atos.sla.parser.data.wsag.tests.Bar;
import eu.atos.sla.parser.data.wsag.tests.Foo;
import eu.atos.sla.parser.data.wsag.tests.FooInner;

/*
 * Test xsd:any element in KPITarget.
 * 
 * http://blog.bdoughan.com/2012/12/jaxbs-xmlanyelementlaxtrue-explained.html
 */
public class KPITargetTest {

    @Test
    public void testSerializeTargetAsObject() throws JAXBException {
        KPITarget target = new KPITarget();
        target.setKpiName("a kpi name");
        target.setCustomServiceLevel(new CustomServiceLevel(new Foo("attr", "prop", new FooInner("inner"))));
        XmlParser<KPITarget> parser =  new XmlParser<KPITarget>(KPITarget.class, Foo.class);
        parser.serialize(target, System.out);
    }

    @Test
    public void testDeserializeTargetAsObject() throws JAXBException {
    
        XmlParser<KPITarget> parser = 
                new XmlParser<KPITarget>(KPITarget.class, Foo.class);
        InputStream is = this.getClass().getResourceAsStream("/samples/kpitarget_object.xml");
        KPITarget target = parser.deserialize(is);
        System.out.println(target);

        Foo foo = target.getCustomServiceLevel().getAnyAsObject(Foo.class);
        assertNotNull(foo);
        assertEquals("attr", foo.getAttribute());
        assertEquals("prop", foo.getProperty());
        assertEquals("inner", foo.getChild().getText());

        try {
            target.getCustomServiceLevel().getAnyAsObject(Bar.class);
            fail("Should have thrown ClassCastException");
        } 
        catch (ClassCastException e) {
            System.out.println("Success! Got '" + e.getMessage() + "'");
        }
        
    }

}
