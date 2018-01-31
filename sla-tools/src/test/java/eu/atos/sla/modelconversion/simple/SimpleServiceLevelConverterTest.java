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
package eu.atos.sla.modelconversion.simple;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.atos.sla.JsonParser;
import eu.atos.sla.XmlParser;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.ServiceLevelObjective;
import eu.atos.sla.parser.data.wsag.custom.SimpleCustomServiceLevel;

public class SimpleServiceLevelConverterTest {

    XmlParser<Agreement> xmlParser;
    SimpleServiceLevelConverter sloConverter = new SimpleServiceLevelConverter();
    JsonParser<Agreement> jsonParser;
    
    public SimpleServiceLevelConverterTest() throws JAXBException {
        
        xmlParser = new XmlParser<Agreement>(Agreement.class, SimpleCustomServiceLevel.class);
        jsonParser = new JsonParser<Agreement>(Agreement.class);
    }
    @Test
    public void testToDataModelFromXml() throws JAXBException {
        InputStream is = this.getClass().getResourceAsStream("/samples/test_parse_business.xml");
        Agreement a = xmlParser.deserialize(is);
        
        ServiceLevelObjective slo = a.getTerms().getAllTerms().getGuaranteeTerms().get(0).getServiceLevelObjective();
        
        EGuaranteeTerm gt = new EGuaranteeTerm();
        sloConverter.toDataModel(slo, gt);
        
        assertEquals("ResponseTime LT 100", gt.getServiceLevel());
    }

    @Test
    public void testToDataModelFromJson() throws JsonParseException, JsonMappingException, IOException {
        InputStream is = this.getClass().getResourceAsStream("/samples/test_json_agreement.json");
        Agreement a = jsonParser.deserialize(is);
        
        ServiceLevelObjective slo = a.getTerms().getAllTerms().getGuaranteeTerms().get(0).getServiceLevelObjective();
        
        EGuaranteeTerm gt = new EGuaranteeTerm();
        sloConverter.toDataModel(slo, gt);
        
        assertEquals("metric1 BETWEEN (0.05, 1)", gt.getServiceLevel());
    }
    
    @Test
    public void testToDataModelUnsupported() throws JAXBException {
        InputStream is = this.getClass().getResourceAsStream("/samples/test_servicelevel_unsupported.xml");
        Agreement a = xmlParser.deserialize(is);
        
        ServiceLevelObjective slo = a.getTerms().getAllTerms().getGuaranteeTerms().get(0).getServiceLevelObjective();
        
        EGuaranteeTerm gt = new EGuaranteeTerm();
        try {
            sloConverter.toDataModel(slo, gt);
            fail("Should have failed");
        } catch (UnsupportedOperationException e) {
            /* success */
        }
    }
}
