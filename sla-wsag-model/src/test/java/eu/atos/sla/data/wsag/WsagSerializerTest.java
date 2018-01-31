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
package eu.atos.sla.data.wsag;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import eu.atos.sla.JsonParser;
import eu.atos.sla.XmlParser;
import eu.atos.sla.parser.DurationUtils;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.AllTerms;
import eu.atos.sla.parser.data.wsag.Context;
import eu.atos.sla.parser.data.wsag.CustomServiceLevel;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.atos.sla.parser.data.wsag.ServiceProperties;
import eu.atos.sla.parser.data.wsag.Template;
import eu.atos.sla.parser.data.wsag.Terms;
import eu.atos.sla.parser.data.wsag.Variable;
import eu.atos.sla.parser.data.wsag.custom.SimpleCustomServiceLevel;
import eu.atos.sla.parser.data.wsag.custom.SimpleCustomServiceLevel.ViolationWindow;


public class WsagSerializerTest {
    private XmlParser<Template> templateParser;
    private XmlParser<Agreement> agreementParser;
    
    public WsagSerializerTest() throws JAXBException {
        templateParser = new XmlParser<Template>(Template.class, SimpleCustomServiceLevel.class);
        agreementParser = new XmlParser<Agreement>(Agreement.class, SimpleCustomServiceLevel.class);
    }

    @Test
    public void serializeTemplate() throws JAXBException {
    
        Template template = new Template();
        template.setName("template-name");
        template.setTemplateId("template-id");

        Context ctx = new Context();
        ctx.setService("service");
        ctx.setServiceProvider(Context.ServiceProvider.AGREEMENT_RESPONDER.toString());
        ctx.setAgreementInitiator("client");
        ctx.setAgreementResponder("provider");
        ctx.setExpirationTime(new Date());
        template.setContext(ctx);
        
        GuaranteeTerm gt = new GuaranteeTerm();
        gt.setName("gt-name");
        Terms terms = new Terms();
        AllTerms allTerms = new AllTerms();
        terms.setAllTerms(allTerms);
        
        template.setTerms(terms);

        templateParser.serialize(template, System.out);
    }
    
    @Test
    public void serializeTemplateWithLombok() throws JAXBException {
        Template template = Template.builder()
                .templateId("template-id")
                .name("template-name")
                .context(Context.builder()
                        .service("service")
                        .serviceProvider(Context.ServiceProvider.AGREEMENT_RESPONDER.toString())
                        .agreementInitiator("client")
                        .agreementResponder("provider")
                        .expirationTime(new Date())
                        .build()
                )
                .terms(
                    new Terms(
                        AllTerms.builder()
                            .guaranteeTerm(GuaranteeTerm.builder()
                                    .name("gt1")
                                    .build()
                            )
                            .build()
                    )
                )
                .build();
        templateParser.serialize(template, System.out);

    }
    
    @Test
    public void deserializeTemplate() throws JAXBException {
        
        InputStream is = this.getClass().getResourceAsStream("/samples/template01.xml");
        Template template = templateParser.deserialize(is);
        /*
         * Read template is serializable
         */
        templateParser.serialize(template, System.out);
        
        assertEquals("template01", template.getTemplateId());
        assertEquals("ExampleTemplate", template.getName());
        
        Context context = template.getContext();
        assertEquals("provider02", context.getAgreementInitiator());
        assertEquals("provider01", context.getAgreementResponder());
        assertEquals("AgreementInitiator", context.getServiceProvider());
        assertEquals("service3", context.getService());

        List<ServiceProperties> properties = template.getTerms().getAllTerms().getServiceProperties();
        assertEquals(2, properties.size());
        ServiceProperties properties1 = properties.get(0);
        ServiceProperties properties2 = properties.get(1);
        assertEquals("AvailabilityProperties", properties1.getName());
        assertEquals("GPS0001", properties1.getServiceName());
        assertEquals("UsabilityProperties", properties2.getName());
        assertEquals("GPS0001", properties2.getServiceName());
        
        assertEquals(2, properties1.getVariableSet().getVariables().size());
        Variable v1 = properties1.getVariableSet().getVariables().get(0);
        assertEquals("ResponseTime", v1.getName());
        assertEquals("metric:Duration", v1.getMetric());
        assertEquals("qos:ResponseTime", v1.getLocation());
        Variable v2 = properties1.getVariableSet().getVariables().get(1);
        assertEquals("Availability", v2.getName());
        assertEquals("metric:Percentage", v2.getMetric());
        assertEquals("qos:Availability", v2.getLocation());
        
        List<GuaranteeTerm> guaranteeTerms = template.getTerms().getAllTerms().getGuaranteeTerms();
        assertEquals(1, guaranteeTerms. size());
        GuaranteeTerm gt1 = guaranteeTerms.get(0);
        assertEquals("FastReaction", gt1.getName());
        assertEquals("http://www.gps.com/coordsservice/getcoords", gt1.getServiceScope().getValue());
        assertEquals("FastResponseTime", gt1.getServiceLevelObjective().getKpitarget().getKpiName());
        SimpleCustomServiceLevel actualSlo = gt1.getServiceLevelObjective().getKpitarget()
                .getCustomServiceLevel().getAnyAsObject(SimpleCustomServiceLevel.class);
        SimpleCustomServiceLevel expectedSlo = SimpleCustomServiceLevel.builder()
                .constraint("ResponseTime LT 200")
                .violationWindow(new ViolationWindow(2, DurationUtils.newDuration("PT30M")))
                .description("")
                .build();
        assertEquals(expectedSlo, actualSlo);
    }
    
    @Test
    public void serializeAgreementWithLombok() throws JAXBException {
        Agreement agreement = Agreement.builder()
                .agreementId("template-id")
                .name("template-name")
                .context(Context.builder()
                        .service("service")
                        .serviceProvider(Context.ServiceProvider.AGREEMENT_RESPONDER.toString())
                        .agreementInitiator("client")
                        .agreementResponder("provider")
                        .expirationTime(new Date())
                        .build()
                )
                .terms(
                    new Terms(
                        AllTerms.builder()
                            .guaranteeTerm(GuaranteeTerm.builder()
                                    .name("gt1")
                                    .build()
                            )
                            .build()
                    )
                )
                .build();
        agreementParser.serialize(agreement, System.out);

    }
    
    @Test
    public void deserializeSerializeAgreement() throws JAXBException {
        
        InputStream is = this.getClass().getResourceAsStream("/samples/agreement01.xml");
        Agreement agreement = agreementParser.deserialize(is);
        agreementParser.serialize(agreement, System.out);
    }
    
    @Test
    public void deserializeAgreementWithTemplateIdShouldFail() {
        
        InputStream is = this.getClass().getResourceAsStream("/samples/agreement_wrong.xml");
        try {
            agreementParser.deserialize(is);
            fail("Should have failed");
        } catch (JAXBException e) {
            /* does nothing */
        }
    }
    
    @Test
    public void serializeToJson() throws Exception {
        JsonParser<Template> jsonTemplateParser = new JsonParser<Template>(Template.class);
        
        InputStream is = this.getClass().getResourceAsStream("/samples/template01.xml");
        Template template = templateParser.deserialize(is);
        
        jsonTemplateParser.serialize(template, System.out);
    }
    
    @Test
    public void deserializeJson() throws Exception {
        JsonParser<Agreement> jsonParser = new JsonParser<Agreement>(Agreement.class);
        InputStream is = this.getClass().getResourceAsStream("/samples/agreement02.json");
        Agreement agreement = jsonParser.deserialize(is);
        /*
         * Jackson returns a Map when deserializing a json object, which is not serializable to XML.
         * From removing string as valid CustomServiceLevel, a ModelConverter MUST build an appropriate
         * object from the map, and remove the map from the any array.
         * 
         * Let's do the substitution by hand
         */
        JsonParser<SimpleCustomServiceLevel> cslParser = new JsonParser<>(SimpleCustomServiceLevel.class);
        for (GuaranteeTerm gt : agreement.getTerms().getAllTerms().getGuaranteeTerms()) {
            CustomServiceLevel cslObject = gt.getServiceLevelObjective().getKpitarget().getCustomServiceLevel();
            SimpleCustomServiceLevel csl = cslParser.convert(cslObject.getAny().get(0));
            cslObject.getAny().clear();
            cslObject.getAny().add(csl);
        }
        agreementParser.serialize(agreement, System.out);
    }
}
