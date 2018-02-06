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
package eu.h2020.symbiote.sla.test;

import eu.h2020.symbiote.model.mim.QoSMetric;
import eu.h2020.symbiote.sla.monitoring.MonitoringClient;
import eu.h2020.symbiote.sla.monitoring.SymbioteMetricRetriever;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;

public class MockMetricRetriever extends SymbioteMetricRetriever {
  
  private Map<String, Double> getAnswer(String pki, double value) {
    Map<String, Double> result = new HashMap<>();
    result.put(pki, value);
    return result;
  }
  
  public MockMetricRetriever() {
    MonitoringClient client = Mockito.mock(MonitoringClient.class);
    
    Mockito.when(client.getSummaryMetric(
        Matchers.anyString(),
        Matchers.startsWith(QoSMetric.availability.toString())))
        .thenAnswer(new Answer<Map<String, Double>>() {
          @Override
          public Map<String, Double> answer(InvocationOnMock invocationOnMock) throws Throwable {
            return getAnswer(invocationOnMock.getArgumentAt(1, String.class), 0.6);
          }
        });
  
    Mockito.when(client.getSummaryMetric(
        Matchers.anyString(),
        Matchers.startsWith(QoSMetric.load.toString())))
        .thenAnswer(new Answer<Map<String, Double>>() {
          @Override
          public Map<String, Double> answer(InvocationOnMock invocationOnMock) throws Throwable {
            return getAnswer(invocationOnMock.getArgumentAt(1, String.class), 10.0);
          }
        });
    
    super.setClient(client);
  }
  
}
