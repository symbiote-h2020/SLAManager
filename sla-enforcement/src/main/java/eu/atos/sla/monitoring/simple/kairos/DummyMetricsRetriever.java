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

import java.net.URL;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * DummyMetricsRetriever that stores the random generated data into a Kairos Db.
 *
 * It needs the KAIROSDB_URL var.
 */
public class DummyMetricsRetriever extends eu.atos.sla.monitoring.simple.DummyMetricsRetriever 
        implements InitializingBean {
    
    private static final Logger logger = LoggerFactory.getLogger(DummyMetricsRetriever.class);

    @Value("${KAIROSDB_URL}")
    private String kairosDbUrlStr;
    
    private URL kairosDbUrl;
    
    @Override
    public List<IMonitoringMetric> getMetrics(String agreementId, String serviceScope, String variable, Date begin,
            Date end, int maxResults) {

        List<IMonitoringMetric> result = super.getMetrics(agreementId, serviceScope, variable, begin, end, maxResults);
        
        if (kairosDbUrl == null) {
            logger.info("Not saving metrics to KairosDb");
        }
        else {
            try (KairosPusher pusher = new KairosPusher(kairosDbUrl)) {
                
                pusher.pushMetrics(agreementId, variable, result);
                logger.info("Metric {} saved to KairosDb", variable);
                
            } catch (Exception e) {
                logger.warn("Exception ignored : {}", e.getMessage());
            }
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        kairosDbUrl = new URL(kairosDbUrlStr);
        logger.debug("KAIROSDB_URL={}", kairosDbUrl.toString());
    }
}
