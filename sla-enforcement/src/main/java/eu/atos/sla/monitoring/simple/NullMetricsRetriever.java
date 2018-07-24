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
package eu.atos.sla.monitoring.simple;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import eu.atos.sla.monitoring.IMetricsRetriever;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * MetricsRetriever that does not return metrics, to be used when SLA subscribes to monitoring, instead of actively
 * request for metrics.
 */
public class NullMetricsRetriever implements IMetricsRetriever {

    public NullMetricsRetriever() {
    }

    @Override
    public List<IMonitoringMetric> getMetrics(String agreementId, String serviceScope, String variable, Date begin,
            Date end, int maxResults) {
        return Collections.<IMonitoringMetric>emptyList();
    }

}
