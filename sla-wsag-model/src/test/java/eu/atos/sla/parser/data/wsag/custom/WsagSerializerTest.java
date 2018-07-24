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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import eu.atos.sla.JsonParser;
import eu.atos.sla.XmlParser;
import eu.atos.sla.parser.data.wsag.Template;
import eu.atos.sla.parser.data.wsag.tests.Bar;

public class WsagSerializerTest {
    private XmlParser<Template> xmlParser;
    private JsonParser<Template> jsonParser;
    
    public WsagSerializerTest() throws JAXBException {
        xmlParser = new XmlParser<Template>(
                Template.class, SimpleCustomServiceLevel.class, CustomContext.class, Bar.class);
        jsonParser = new JsonParser<Template>(Template.class);
    }

    @Test
    public void testXml() throws JAXBException, IOException {
    
        InputStream is = this.getClass().getResourceAsStream("/samples/template01.xml");
        Template template = xmlParser.deserialize(is);
        SimpleCustomServiceLevel slo = template.getTerms().getAllTerms().getGuaranteeTerms().get(0)
                .getServiceLevelObjective().getKpitarget().getCustomServiceLevel()
                .getAnyAsObject(SimpleCustomServiceLevel.class);
        assertEquals("ResponseTime LT 200", slo.getConstraint());
        assertEquals(2, slo.getViolationWindows().get(0).getCount());
        assertEquals(30 * 60 * 1000, slo.getViolationWindows().get(0).getInterval().getTimeInMillis(new Date(0)));
        CustomContext customContext = template.getContext().getAnyAsObject(CustomContext.class);
        assertEquals(60*1000, customContext.getCreationTime().getTime());
        xmlParser.serialize(template, System.out);
        jsonParser.serialize(template, System.out);
    }
    
    @Test
    public void testJson() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/samples/template01.json");
        Template template = jsonParser.deserialize(is);
        
        /*
         * Check value of Bar instance in context/xs:any
         */
        assertEquals("barElem", template.getContext().getAnyAsObject(Bar.class).getValue());
    }
}
