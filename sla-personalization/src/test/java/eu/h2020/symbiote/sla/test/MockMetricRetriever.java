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
