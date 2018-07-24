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
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import eu.atos.sla.XmlParser;
import eu.atos.sla.parser.data.wsag.tests.Bar;
import eu.atos.sla.parser.data.wsag.tests.Foo;
import eu.atos.sla.parser.data.wsag.tests.FooInner;

/*
 * Test xsd:any element in Context.
 * 
 * http://blog.bdoughan.com/2012/12/jaxbs-xmlanyelementlaxtrue-explained.html
 */
public class ContextTest {

    @Test
    public void testSerializeAny() throws JAXBException {
        Context ctx = Context.builder()
                .agreementInitiator("client")
                .agreementResponder("provider")
                .expirationTime(new Date())
                .serviceProvider(Context.ServiceProvider.AGREEMENT_RESPONDER.toString())
                .service("service")
                .anyone(new Foo("attr", "prop", new FooInner("fooInner")))
                .anyone(new Bar("barValue"))
                .build();
        
        XmlParser<Context> parser = new XmlParser<Context>(Context.class, Foo.class, Bar.class);
        parser.serialize(ctx, System.out);
    }
    
    @Test
    public void testDeserializeAny() throws JAXBException {
        
        XmlParser<Context> parser = new XmlParser<Context>(Context.class, Foo.class, Bar.class);
        InputStream is = this.getClass().getResourceAsStream("/samples/context.xml");
        Context ctx = parser.deserialize(is);
        System.out.println(ctx);

        Foo foo = null;
        Bar bar = null;
        for (Object o : ctx.getAny()) {
            if (o instanceof Foo) {
                assertNull(foo);
                foo = (Foo) o;
            }
            else if (o instanceof Bar) {
                assertNull(bar);
                bar = (Bar) o;
            }
        }
        assertEquals("service", ctx.getService());
        assertNotNull(foo);
        assertEquals("attr", foo.getAttribute());
        assertEquals("prop", foo.getProperty());
        assertEquals("fooInner", foo.getChild().getText());
        assertNotNull(bar);
        assertEquals("barElem", bar.getValue());
    }
}
