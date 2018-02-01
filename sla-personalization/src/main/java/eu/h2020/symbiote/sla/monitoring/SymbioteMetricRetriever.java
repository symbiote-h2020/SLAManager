/**
 * Copyright 2017 Atos
 * Contact: Atos <jose.sanchezm@atos.net>
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
package eu.h2020.symbiote.sla.monitoring;

import eu.atos.sla.monitoring.IMetricsRetriever;
import eu.atos.sla.monitoring.IMonitoringMetric;
import eu.atos.sla.monitoring.simple.kairos.DummyMetricsRetriever;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SymbioteMetricRetriever implements IMetricsRetriever,InitializingBean {
  
  private static final Logger logger = LoggerFactory.getLogger(DummyMetricsRetriever.class);
  
  @Value("${symbiote.monitoring.url}")
  private String monitoringUrl;
  
  private MonitoringClient client;
  
  @Override
  public List<IMonitoringMetric> getMetrics(String agreementId, String serviceScope, String variable,
                                            Date begin, Date end, int maxResults) {
    Map<String, Double> result = client.getSummaryMetric(serviceScope, variable);
    
    return result.entrySet().stream()
               .map(entry -> new SymbioteMonitoringMetric(entry.getKey(), entry.getValue(), end))
               .collect(Collectors.toList());
    
  }
  
  @Override
  public void afterPropertiesSet() throws Exception {
  
    setClient(Feign.builder().encoder(new JacksonEncoder()).decoder(new JacksonDecoder())
                           .target(MonitoringClient.class, monitoringUrl));
    
  }
  
  public void setClient(MonitoringClient client) {
    this.client = client;
  }
}
