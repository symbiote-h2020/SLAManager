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
package eu.atos.sla.monitoring.simple.kairos;

import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.kairosdb.client.HttpClient;
import org.kairosdb.client.builder.Metric;
import org.kairosdb.client.builder.MetricBuilder;

import eu.atos.sla.SlaRuntimeException;
import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.monitoring.IMonitoringMetric;

public class KairosPusher implements Closeable {

    private URL url;
    private HttpClient client;

    public KairosPusher(URL url) {
        this.url = url;
        
        try {
            
            this.client = new HttpClient(this.url.toString());
            
        } catch (MalformedURLException e) {
            /*
             * this should not happen 
             */
            throw new AssertionError(e.getMessage(), e);
        }
    }
    
    /**
     * Pushes a list of datapoints to kairosdb.
     * Assumes all datapoints correspond to the same metric
     */
    public void pushMetrics(String agreementId, String metricName, List<IMonitoringMetric> metrics) {
        
        if (metrics.size() == 0) {
            return;
        }
        MetricBuilder builder = MetricBuilder.getInstance();
        Metric metric = builder.addMetric(metricName)
            .addTag("agreement", agreementId);
        for (IMonitoringMetric point : metrics) {
            metric.addDataPoint(point.getDate().getTime(), point.getMetricValue());
        }
        try {
            this.client.pushMetrics(builder);
        } catch (URISyntaxException e) {
            throw new SlaRuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new SlaRuntimeException(e.getMessage(), e);
        }
    }

    public void pushViolations(String agreementId, String metricName, List<EViolation> violations) {
        
        if (violations.size() == 0) {
            return;
        }
        
        MetricBuilder builder = MetricBuilder.getInstance();
        Metric metric = builder.addMetric(metricName)
            .addTag("agreement", agreementId);
        for (EViolation point : violations) {
            metric.addDataPoint(point.getDatetime().getTime(), point.getActualValue());
        }
        try {
            this.client.pushMetrics(builder);
        } catch (URISyntaxException e) {
            throw new SlaRuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new SlaRuntimeException(e.getMessage(), e);
        }
    }
    
    @Override
    public void close() throws IOException {
        client.shutdown();
    }
}
