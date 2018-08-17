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

import static org.junit.Assert.*;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.Test;

import eu.atos.sla.XmlParser;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.CustomServiceLevel;
import eu.atos.sla.parser.data.wsag.tests.Foo;
import eu.atos.sla.service.rest.providers.JaxbWsagContextProvider;

public class JaxbWsagContextProviderTest {

    private static final String PACKAGE_NAMES = "eu.atos.sla.parser.data.wsag:eu.atos.sla.parser.data.wsag.tests";

    @Test
    public void testGetContext() throws JAXBException {
        JaxbWsagContextProvider provider = new JaxbWsagContextProvider(PACKAGE_NAMES);
        JAXBContext context = provider.getContext(Agreement.class);
        XmlParser<Agreement> parser = new XmlParser<Agreement>(context);
        InputStream is = this.getClass().getResourceAsStream("/samples/test_parse_agreement_multiple_ns.xml");
        
        Agreement a = parser.deserialize(is);
        assertNotNull(a);
        CustomServiceLevel csl0 = a.getTerms().getAllTerms().getGuaranteeTerms().get(0)
                .getServiceLevelObjective().getKpitarget().getCustomServiceLevel();
        
        Foo foo = csl0.getAnyAsObject(Foo.class);
        assertNotNull(foo);
        assertEquals("Availability GT 99", foo.getProperty());
    }

}
