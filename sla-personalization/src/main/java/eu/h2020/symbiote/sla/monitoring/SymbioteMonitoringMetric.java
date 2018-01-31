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

import eu.atos.sla.monitoring.IMonitoringMetric;

import java.util.Date;

public class SymbioteMonitoringMetric implements IMonitoringMetric{
  
  private String metricKey;
  private String metricValue;
  private Date date;
  
  public SymbioteMonitoringMetric(String metricKey, Double metricValue, Date date) {
    this.metricKey = metricKey;
    this.metricValue = Double.toString(metricValue);
    this.date = date;
  }
  
  @Override
  public String getMetricKey() {
    return metricKey;
  }
  
  @Override
  public String getMetricValue() {
    return metricKey;
  }
  
  @Override
  public Date getDate() {
    return date;
  }
}
