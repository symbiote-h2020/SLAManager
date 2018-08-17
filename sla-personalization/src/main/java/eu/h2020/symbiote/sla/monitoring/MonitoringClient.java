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

import eu.h2020.symbiote.cloud.monitoring.model.AggregatedMetrics;
import eu.h2020.symbiote.cloud.monitoring.model.DeviceMetric;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface MonitoringClient {
  
  @RequestLine("POST " + MonitoringConstants.METRICS_DATA)
  @Headers("Content-Type: application/json")
  List<DeviceMetric> postMetrics(List<DeviceMetric> metrics);
  
  @RequestLine("GET " + MonitoringConstants.METRICS_DATA)
  @Headers("Content-Type: application/json")
  List<DeviceMetric> getMetrics(@QueryMap Map<String, String> parameters);
  
  @RequestLine("GET " + MonitoringConstants.AGGREGATED_DATA)
  @Headers("Content-Type: application/json")
  List<AggregatedMetrics> getAggregatedMetrics(@QueryMap Map<String, String> parameters);
  
  @RequestLine("GET " + MonitoringConstants.SUMMARY_DATA+"?federation={federation}&metric={metric}")
  @Headers("Content-Type: application/json")
  Map<String, Double> getSummaryMetric(@Param("federation") String federationId,
                                       @Param("metric") String inputMetric);
}
