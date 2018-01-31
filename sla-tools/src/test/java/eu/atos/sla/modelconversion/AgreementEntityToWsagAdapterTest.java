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
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.parser.data.wsag.Agreement;

public class AgreementEntityToWsagAdapterTest {

    @Test
    public void testApply() throws JAXBException {
        InputStream is = this.getClass().getResourceAsStream("/samples/test_parse_business.xml");
        XmlParser<Agreement> xmlParser = new XmlParser<Agreement>(
                "eu.atos.sla.parser.data.wsag:eu.atos.sla.parser.data.wsag.tests:eu.atos.sla.parser.data.wsag.custom");

        Agreement wsag = xmlParser.deserialize(is);
        String text = xmlParser.toString(wsag);
        
        EAgreement entity = new EAgreement("id");
        entity.setText(text);
        
        new AgreementEntityToWsagAdapter(xmlParser).apply(entity);
    }

}
