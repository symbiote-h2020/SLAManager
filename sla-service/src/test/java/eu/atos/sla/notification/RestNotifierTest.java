/**
 * Copyright 2017 Atos
 * Contact: Atos <roman.sosa@atos.net>
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
package eu.atos.sla.notification;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.atos.sla.XmlParser;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.ECompensation;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EPenalty;
import eu.atos.sla.datamodel.EPenaltyDefinition;
import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.evaluation.guarantee.GuaranteeTermEvaluator.GuaranteeTermEvaluationResult;
import eu.atos.sla.modelconversion.simple.SimpleModelConverter;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.Template;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class RestNotifierTest {

    private MockWebServer server;
    private URL serverUrl;
    private RestNotifier notifierJson;
    private RestNotifier notifierXml;
    
    @Before
    public void setup() throws IOException, JAXBException {
        
        server = new MockWebServer();
        server.start();
        serverUrl = server.url("/").url();

        XmlParser<Agreement> agreementParser = new XmlParser<>(Agreement.class);
        XmlParser<Template> templateParser = new XmlParser<>(Template.class);
        SimpleModelConverter modelConverter = new SimpleModelConverter(agreementParser, templateParser);
        String trickyUrl = " , " + serverUrl.toString() + " , ";
        notifierJson = new RestNotifier(modelConverter, trickyUrl, MediaType.APPLICATION_JSON, "", "");
        notifierXml = new RestNotifier(modelConverter, trickyUrl, MediaType.APPLICATION_XML, "", "");
    }
    
    @After
    public void shutdown() throws IOException {

        server.shutdown();
    }
    
    
    @Test
    public void testOnFinishEvaluationJson() throws InterruptedException {
        server.enqueue(new MockResponse()
                .addHeader("Content-type", "application/json")
                .setBody("{}"));
        
        EAgreement a = new EAgreement("agreement-a");
        Map<EGuaranteeTerm, GuaranteeTermEvaluationResult> map = buildMap(a);
        
        notifierJson.onFinishEvaluation(a, map);
        
        RecordedRequest req = server.takeRequest();
        System.out.println("body = " + req.getBody().readUtf8());
    }
    
    @Test
    public void testOnFinishEvaluationXml() throws InterruptedException {
        server.enqueue(new MockResponse()
                .addHeader("Content-type", "application/xml")
                .setBody("<no-content/>"));
        
        EAgreement a = new EAgreement("agreement-a");
        Map<EGuaranteeTerm, GuaranteeTermEvaluationResult> map = buildMap(a);
        
        notifierXml.onFinishEvaluation(a, map);
        
        RecordedRequest req = server.takeRequest();
        System.out.println("body = " + req.getBody().readUtf8());
    }

    private Map<EGuaranteeTerm, GuaranteeTermEvaluationResult> buildMap(EAgreement a) {
        List<EGuaranteeTerm> gts = new ArrayList<>();
        
        EGuaranteeTerm gt1 = newGuaranteeTerm("gt1");
        gts.add(gt1);
        
        EGuaranteeTerm gt2 = newGuaranteeTerm("gt2");
        gts.add(gt2);
        
        a.setGuaranteeTerms(gts);
        
        Map<EGuaranteeTerm, GuaranteeTermEvaluationResult> map = new HashMap<>();
        
        List<EViolation> violations = new ArrayList<>();
        EViolation v1 = newViolation(a.getAgreementId(), "v1", gt1);
        violations.add(v1);
        
        EPenaltyDefinition def1 = newEPenaltyDefinition("1");
        EPenaltyDefinition def2 = newEPenaltyDefinition("2");
        
        @SuppressWarnings("rawtypes")
        List<ECompensation> compensations = new ArrayList<>();
        compensations.add(newPenalty(a.getAgreementId(), gt1, v1, def1));
        compensations.add(newPenalty(a.getAgreementId(), gt1, v1, def2));

        map.put(gt1, new GuaranteeTermEvaluationResult(violations, compensations));
        return map;
    }

    private EPenaltyDefinition newEPenaltyDefinition(String valueExpression) {
        EPenaltyDefinition def = new EPenaltyDefinition(new Date(60 * 1000), "euro", valueExpression);
        return def;
    }

    private EViolation newViolation(String agreementId, String uuid, EGuaranteeTerm gt) {
        EViolation v = new EViolation();
        v.setUuid(uuid);
        v.setActualValue("100");
        v.setContractUuid(agreementId);
        v.setDatetime(new Date());
        v.setExpectedValue("50");
        v.setKpiName(gt.getKpiName());
        v.setServiceName(gt.getServiceName());
        v.setServiceScope(gt.getServiceScope());
        return v;
    }

    private EGuaranteeTerm newGuaranteeTerm(String name) {
        EGuaranteeTerm gt = new EGuaranteeTerm();
        gt.setName(name);
        gt.setKpiName(name + "kpi");
        
        return gt;
    }
    
    @SuppressWarnings("rawtypes")
    private ECompensation newPenalty(String agreementId, EGuaranteeTerm gt, EViolation v, EPenaltyDefinition def) {
        EPenalty result = new EPenalty(agreementId, v.getDatetime(), gt.getKpiName(), def, v);
        return result;
    }
}
