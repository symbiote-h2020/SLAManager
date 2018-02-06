/**
 * Copyright 2015 Atos
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.ECompensation;
import eu.atos.sla.datamodel.EGuaranteeTerm;
import eu.atos.sla.datamodel.EPenalty;
import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.evaluation.guarantee.GuaranteeTermEvaluator.GuaranteeTermEvaluationResult;
import eu.atos.sla.modelconversion.ModelConverter;
import eu.atos.sla.notification.IAgreementEnforcementNotifier;
import eu.atos.sla.parser.data.Penalty;
import eu.atos.sla.parser.data.Violation;

/**
 * Notifier that POSTs the occurred violations and penalties to a list of URLs.
 * 
 * The body contents a serialized {@link Notification} according to the contentType.
 * 
 * The constructors accepting url as String expect a CSV of URLs (empty values and whitespaces are discarded)
 * 
 * Optionally, not empty user and password can be passed to constructor to use BASIC auth.
 * 
 * @see Notification 
 */
public class RestNotifier implements IAgreementEnforcementNotifier {
    private static final Logger logger = LoggerFactory.getLogger(RestNotifier.class);

    @Autowired
    private ModelConverter modelConverter;

    public static final MultivaluedMap<String, String> EMPTY_MAP = 
            new MultivaluedHashMap<String, String>();
    
    private final String contentType;
    private final MultivaluedMap<String, Object> headersMap = new MultivaluedHashMap<>();
    private final Client client;
    private final List<WebTarget> resources;
    
    public RestNotifier(String urls, String contentType) {
        this(urls, contentType, "", "");
    }
    
    public RestNotifier(List<String> urls, String contentType) {
        this(urls, contentType, "", "");
    }
    
    public RestNotifier(String urls, String contentType, String authUser, String authPassword) {
        this(splitUrls(urls), contentType, authUser, authPassword);
    }

    public RestNotifier(List<String> urls, String contentType, String authUser, String authPassword) {
        logger.info("Creating RestNotifier URL={} contentType={}", urls, contentType);
        
        this.contentType = contentType;
        headersMap.add("Content-type", contentType);

        this.client = getClient(authUser, authPassword);
        this.resources = new ArrayList<WebTarget>();
        
        for (String url : urls) {
            if (!url.isEmpty()) {
                WebTarget resource = client.target(url.trim());
                this.resources.add(resource);
            }
        }
    }
    
    /**
     * Constructor for the non autowired version
     */
    public RestNotifier(ModelConverter modelConverter, String urls, String contentType, 
            String authUser, String authPassword) {
        this(splitUrls(urls), contentType, authUser, authPassword);
        this.modelConverter = modelConverter;
    }
    
    private static List<String> splitUrls(String url) {
        return Arrays.asList(url.trim().split(","));
    }

    private Client getClient(String user, String password) {
        
        ClientConfig config = new ClientConfig()
                .register(JacksonFeature.class);
        
        if (!"".equals(user)) {
            HttpAuthenticationFeature auth = HttpAuthenticationFeature.basic(user, password);
            config.register(auth);
        }
        
        Client client = ClientBuilder.newClient(config);
        return client;
    }
    
    @Override
    public void onFinishEvaluation(
            EAgreement agreement,
            Map<EGuaranteeTerm, GuaranteeTermEvaluationResult> guaranteeTermEvaluationMap) {

        if (modelConverter == null) {
            throw new NullPointerException("modelConverter has not been set");
        }
        if (resources.size() == 0) {
            return;
        }
        
        Notification data = buildNotification(guaranteeTermEvaluationMap);
        for (WebTarget resource : resources) {
            send(resource, data);
        }
    }

    private Notification buildNotification(
            Map<EGuaranteeTerm, GuaranteeTermEvaluationResult> evaluation) {

        List<Violation> violations = new ArrayList<>();
        List<Penalty> penalties = new ArrayList<>();
        
        for (GuaranteeTermEvaluationResult o : evaluation.values()) {
            for (EViolation violation : o.getViolations()) {
                
                Violation item = modelConverter.getViolationXML(violation);
                violations.add(item);
            }
            for (ECompensation<?> compensation : o.getCompensations()) {
                
                if (compensation instanceof EPenalty) {
                    Penalty item = modelConverter.getPenaltyXML((EPenalty)compensation);
                    penalties.add(item);
                }
            }
        }
        return new Notification(violations, penalties);
    }

    private void send(WebTarget target, Object data) {
        Response response = method(HttpMethod.POST, target, data, EMPTY_MAP, headersMap);
        
        int status = response.getStatus();
        logger.debug("Compensations notified. Status={}", status);
        if (!isOk(status)) {
            throw new RestNotifierException(status, response.readEntity(String.class));
        }
    }
    
    /**
     * Executes a method.
     * 
     * @param method method name (one of HttpMethod.*)
     * @param target WebTarget where to send the request. The final url is target.uri?queryparam1=queryvalue1&...
     * @param data in the request body: a string, a jaxb annotated pojo, etc.
     * @param queryParams MultivaluedMap of query parameters
     * @param headers MultivaluedMap of headers
     * @return A {@link RequestResponse} with the response (getStatus() to check
     *         status; getEntity() to get body)
     */
    private Response method(String method, WebTarget target,
            Object data, MultivaluedMap<String, String> queryParams,
            MultivaluedMap<String, Object> headers) {

        target = applyQueryParams(target, queryParams);

        Response response = target
                .request(MediaType.APPLICATION_JSON)
                .headers(headers)
                .method(method, Entity.entity(data, contentType));
               
        return response;
    }
    
    private WebTarget applyQueryParams(WebTarget target, MultivaluedMap<String, String> params) {

        for (String key : params.keySet()) {
            for (String value : params.get(key)) {
                target = target.queryParam(key, value);
            }
        }
        return target;
    }
    
    private boolean isOk(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    public static class RestNotifierException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        
        private String body;
        private int status;
        public RestNotifierException(int status, String body) {
            super(body);
            this.body = body;
            this.status = status;
        }
        
        public String getBody() {
            return body;
        }
        
        public int getStatus() {
            return status;
        }
    }
}
