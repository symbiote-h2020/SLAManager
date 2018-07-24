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

import javax.xml.bind.JAXBException;

import org.junit.Test;

import eu.atos.sla.JsonParser;
import eu.atos.sla.XmlParser;
import eu.atos.sla.parser.DurationUtils;

import static eu.atos.sla.parser.data.wsag.custom.SimpleCustomServiceLevel.ViolationWindow;

public class SimpleCustomServiceLevelTest {
    XmlParser<SimpleCustomServiceLevel> xmlparser;
    JsonParser<SimpleCustomServiceLevel> jsonparser;
    
    public SimpleCustomServiceLevelTest() throws JAXBException {
        xmlparser = new XmlParser<SimpleCustomServiceLevel>(SimpleCustomServiceLevel.class);
        jsonparser = new JsonParser<SimpleCustomServiceLevel>(SimpleCustomServiceLevel.class);
    }
    
    @Test
    public void serializeSlo() throws JAXBException, IOException {
        SimpleCustomServiceLevel slo = SimpleCustomServiceLevel.builder()
                .description("slo-description")
                .constraint("MetricCondition NOT EXISTS")
                .violationWindow(new ViolationWindow())
                .violationWindow(new ViolationWindow(2, DurationUtils.newDuration("PT1H")))
                .build();
        
        xmlparser.serialize(slo, System.out);
        jsonparser.serialize(slo, System.out);
    }

    @Test
    public void deserializeSlo() throws JAXBException {
        InputStream is = this.getClass().getResourceAsStream("/samples/simplecustomservicelevel.xml");
        SimpleCustomServiceLevel slo = xmlparser.deserialize(is);
        System.out.println(slo);
        
        SimpleCustomServiceLevel expected = SimpleCustomServiceLevel.builder()
                .constraint("Metric LT 40")
                .description("This SLO ensures the AVG(CPU usage, 2 min) is below 40%")
                .violationWindow(new ViolationWindow(1, DurationUtils.EMPTY_DURATION))
                .violationWindow(new ViolationWindow(2, DurationUtils.newDuration("PT1H")))
                .build();
                
        assertEquals(expected, slo);
    }
}
