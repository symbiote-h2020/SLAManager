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

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.monitoring.IMonitoringMetric;
import eu.atos.sla.monitoring.simple.MonitoringMetric;

@SuppressWarnings("unused")
public class KairosPusherIT {

    KairosPusher pusher;
    
    public KairosPusherIT() throws MalformedURLException {
        URL url = new URL("http://localhost:8009");
        pusher =  new KairosPusher(url);
    }
    
    @Test
    public void pushMetrics() {
        
        List<IMonitoringMetric> metrics = buildDataPoints();
        pusher.pushMetrics("agreement-id", "test-metric", metrics);
    }

    @Test
    public void pushViolations() {
    
        List<EViolation> violations = buildViolations();
        
        pusher.pushViolations("agreement-id", "test-violations", violations);
    }
    
    private List<EViolation> buildViolations() {
        List<EViolation> result = new ArrayList<>();
        
        Date now = new Date();
        result.add(newEViolation(now, 99.1));
        result.add(newEViolation(new Date(now.getTime() - 60 * 1000), 39.5));
        return result ;
    }

    private EViolation newEViolation(Date date, Double actualValue) {
        EViolation result = new EViolation();
        result.setDatetime(date);
        result.setActualValue(actualValue.toString());
        return result ;
    }

    private List<IMonitoringMetric> buildDataPoints() {
        
        List<IMonitoringMetric> result = new ArrayList<IMonitoringMetric>();
        Date now = new Date();
        for (int i = 0; i < 30; i++) {
            Date date = new Date(now.getTime() - 30 * 1000 * (29 - i));
            MonitoringMetric m = new MonitoringMetric("test-metric", Math.random(), date);
            
            result.add(m);
        }
        return result;
    }

    @After
    public void shutdown() throws IOException {
        pusher.close();
    }
}
