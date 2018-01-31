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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import eu.atos.sla.JsonParser;
import eu.atos.sla.XmlParser;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EPenaltyDefinition;
import eu.atos.sla.datamodel.EPolicy;
import eu.atos.sla.datamodel.ETemplate;
import eu.atos.sla.modelconversion.ModelConversionException;
import eu.atos.sla.modelconversion.simple.BusinessValueListParser;
import eu.atos.sla.modelconversion.simple.SimpleModelConverter;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.ServiceLevelObjective;
import eu.atos.sla.parser.data.wsag.Template;
import eu.atos.sla.parser.data.wsag.Context.ServiceProvider;
import eu.atos.sla.parser.data.wsag.custom.CustomContext;
import eu.atos.sla.parser.data.wsag.tests.Foo;
import eu.atos.sla.parser.data.wsag.CustomServiceLevel;

public class SimpleModelConverterTest {

    private static final String CONTEXT_PATH = 
            "eu.atos.sla.parser.data.wsag:eu.atos.sla.parser.data.wsag.tests:eu.atos.sla.parser.data.wsag.custom";
    private XmlParser<Agreement> xmlParser;
    private XmlParser<Template> templateXmlParser;
    private SimpleModelConverter modelConverter;
    private JsonParser<Agreement> jsonParser;
    
    @Before
    public void setUp() throws Exception {
        xmlParser =  new XmlParser<Agreement>(CONTEXT_PATH);
        templateXmlParser =  new XmlParser<Template>(Template.class);
        jsonParser = new JsonParser<Agreement>(Agreement.class);
        modelConverter = new SimpleModelConverter(xmlParser, templateXmlParser);
        modelConverter.setBusinessValueListParser(new BusinessValueListParser());
        modelConverter.setServiceLevelConverter(new SimpleServiceLevelConverter());
        modelConverter.setContextConverter(new SimpleContextConverter());
    }

    @Test
    public void testParseSimpleAgreement() throws JAXBException {
        
        InputStream is = this.getClass().getResourceAsStream("/samples/test_parse_business.xml");
        Agreement wsag = xmlParser.deserialize(is);
        EAgreement entity = modelConverter.getAgreementFromAgreementXML(wsag, "don't care");
        Agreement wsag2 = xmlParser.deserialize(new StringReader(entity.getText()));
        assertEquals("ExampleAgreement", wsag2.getName());
        assertEquals(60*60*1000, wsag2.getContext().getAnyAsObject(CustomContext.class).getCreationTime().getTime());
    }
    
    @Test
    public void testParseSimpleJsonAgreement() throws IOException, JAXBException  {
        
        InputStream is = this.getClass().getResourceAsStream("/samples/test_json_agreement.json");
        Agreement wsag = jsonParser.deserialize(is);
        EAgreement entity = modelConverter.getAgreementFromAgreementXML(wsag, "don't care");
        assertEquals("metric1 BETWEEN (0.05, 1)", entity.getGuaranteeTerms().get(0).getServiceLevel());
        System.out.println(entity.getText());
        xmlParser.deserialize(new StringReader(entity.getText()));
    }
    
    @Test
    public void testParseMultipleNamespacesAgreement() throws JAXBException {
        modelConverter.setServiceLevelConverter(new ServiceLevelConverter() {
            
            @Override
            public void toDataModel(ServiceLevelObjective in, EGuaranteeTerm out) {
                CustomServiceLevel csl = in.getKpitarget().getCustomServiceLevel();
                Foo foo = csl.getAnyAsObject(Foo.class);
                out.setServiceLevel(foo.getProperty());
            }
        });
        InputStream is = this.getClass().getResourceAsStream("/samples/test_parse_agreement_multiple_ns.xml");
        Agreement wsag = xmlParser.deserialize(is);
        EAgreement entity = modelConverter.getAgreementFromAgreementXML(wsag, "don't care");
        assertEquals("Availability GT 99", entity.getGuaranteeTerms().get(0).getServiceLevel());
        Agreement wsag2 = xmlParser.deserialize(new StringReader(entity.getText()));
        assertEquals("ExampleAgreement", wsag2.getName());
    }
    
    private <T> T readXml(File f) 
            throws JAXBException, FileNotFoundException {
        
        JAXBContext jaxbContext = JAXBContext
                .newInstance(CONTEXT_PATH);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        @SuppressWarnings("unchecked")
        T result = 
                (T) jaxbUnmarshaller.unmarshal(
                        new FileReader(f));
        
        return result;
    }
    
    private File getResourceFile(String path) {
        URL url = this.getClass().getResource(path);
        if (url == null) {
            throw new IllegalArgumentException("Resource file " + path + " not found");
        }
        return new File(url.getFile());
    }
    
    private void checkParseAgreementContext(eu.atos.sla.parser.data.wsag.Agreement agreementXML, 
            ServiceProvider rol) throws JAXBException, FileNotFoundException, ModelConversionException {
        
        
        String expectedProvider;
        String expectedConsumer;
        
        if (rol == null) {
            agreementXML.getContext().setServiceProvider("invalid value here");
            expectedProvider = null;
            expectedConsumer = null;
        }
        else {
            agreementXML.getContext().setServiceProvider(rol.toString());
            if (rol == ServiceProvider.AGREEMENT_INITIATOR) {
                expectedProvider = "initiator";
                expectedConsumer = "responder";
            } else if (rol == ServiceProvider.AGREEMENT_RESPONDER) {
                expectedConsumer = "initiator";
                expectedProvider = "responder";
            }
            else {
                throw new AssertionError();
            }
        }

        String actualProvider;
        String actualConsumer;
        try {
            EAgreement a = modelConverter.getAgreementFromAgreementXML((Agreement)agreementXML, "");
            actualProvider = a.getProvider().getUuid();
            actualConsumer = a.getConsumer();
        } catch (ModelConversionException e) {
            actualProvider = null;
            actualConsumer = null;
        }
        
        /*
         * Match provider
         */
        if (rol == null) {
            assertNull(actualProvider);
            assertNull(actualConsumer);
        }
        else {
            assertEquals(expectedProvider, actualProvider);
            assertEquals(expectedConsumer, actualConsumer);
        }
    }
    
    @Test
    public void testParseAgreementContext() throws JAXBException, FileNotFoundException, ModelConversionException {
        File file = getResourceFile("/samples/test_parse_context.xml");
        eu.atos.sla.parser.data.wsag.Agreement agreementXML = readXml(file);
        
        checkParseAgreementContext(agreementXML, null);
        checkParseAgreementContext(agreementXML, ServiceProvider.AGREEMENT_RESPONDER);
        checkParseAgreementContext(agreementXML, ServiceProvider.AGREEMENT_INITIATOR);
    }
    
    @Test
    public void testNoGuaranteeTerms() throws JAXBException, FileNotFoundException, ModelConversionException {
        File file = getResourceFile("/samples/test_no_guarantee_terms.xml");
        eu.atos.sla.parser.data.wsag.Template templateXML = readXml(file);
        
        ETemplate t = modelConverter.getTemplateFromTemplateXML((Template)templateXML, "");
        assertNotNull(t);
    }
    
    @Test
    public void testCustomBusinessValue() throws JAXBException, FileNotFoundException, ModelConversionException {
        
        File file = getResourceFile("/samples/test_parse_business.xml");
        eu.atos.sla.parser.data.wsag.Agreement agreementXML = readXml(file);
        EAgreement a = modelConverter.getAgreementFromAgreementXML((Agreement)agreementXML, "");
        
        EPenaltyDefinition[] expected = new EPenaltyDefinition[] {
            new EPenaltyDefinition(1, new Date(0), "discount", "%", "35", "P1D"),
            new EPenaltyDefinition(5, new Date(24*60*60*1000), "service", "", "sms", "P1M")
        };
        for (EGuaranteeTerm gt : a.getGuaranteeTerms()) {
            int i = 0;
            for (EPenaltyDefinition actual : gt.getBusinessValueList().getPenalties()) {
                
                assertEquals(expected[i], actual);
                i++;
            }
        }
    }
    
    @Test
    public void testPolicies() throws ModelConversionException, FileNotFoundException, JAXBException {
        
        File file = getResourceFile("/samples/test_parse_windows.xml");
        eu.atos.sla.parser.data.wsag.Agreement agreementXML = readXml(file);
        EAgreement a = modelConverter.getAgreementFromAgreementXML((Agreement)agreementXML, "");

        checkPolicyExists(new EPolicy(2, new Date(120*1000)), a.getGuaranteeTerms().get(0).getPolicies());
        checkPolicyExists(new EPolicy(3, new Date(3600*1000)), a.getGuaranteeTerms().get(0).getPolicies());
    }
    
    private void checkPolicyExists(EPolicy expected, List<EPolicy> actuals) {
        boolean found = false;
        for (EPolicy actual : actuals) {
            System.out.println(actual.getCount() + " " + actual.getTimeInterval());
            if (equalsPolicy(expected, actual)) {
                found = true;
                break;
            }
        }
        if (!found) {
            fail("Policy " + expected + " not found");
        }
    }
    
    /*
     * Being a jpa object, IPolicy do not implement equals.
     * Nice read: http://www.onjava.com/lpt/a/6718
     */
    private boolean equalsPolicy(EPolicy p1, EPolicy p2) {
        if (p1 == null || p2 == null) {
            throw new NullPointerException();
        }
        
        return (p1.getCount() == p2.getCount() && p1.getTimeInterval().equals(p2.getTimeInterval()));
    }
}
