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
package eu.atos.sla.modelconversion;

import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import eu.atos.sla.XmlParser;
import eu.atos.sla.datamodel.ETemplate;
import eu.atos.sla.parser.data.wsag.Template;

public class TemplateEntityToWsagAdapterTest {

    @Test
    public void testApply() throws JAXBException {
        InputStream is = this.getClass().getResourceAsStream("/samples/template01.xml");
        XmlParser<Template> xmlParser = new XmlParser<Template>(
                "eu.atos.sla.parser.data.wsag:eu.atos.sla.parser.data.wsag.tests:eu.atos.sla.parser.data.wsag.custom");

        Template wsag = xmlParser.deserialize(is);
        String text = xmlParser.toString(wsag);
        
        ETemplate entity = new ETemplate();
        entity.setText(text);
        
        new TemplateEntityToWsagAdapter(xmlParser).apply(entity);
    }

}
