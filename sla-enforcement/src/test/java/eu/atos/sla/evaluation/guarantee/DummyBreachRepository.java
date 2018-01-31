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
package eu.atos.sla.evaluation.guarantee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EBreach;
import eu.atos.sla.evaluation.constraint.IConstraintEvaluator;
import eu.atos.sla.evaluation.constraint.simple.SimpleConstraintEvaluator;
import eu.atos.sla.evaluation.guarantee.IBreachRepository;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * Breach repository that takes a list of metrics and auto calculates which are breaches.  
 * 
 * Useful for testing
 */
public class DummyBreachRepository implements IBreachRepository {

    private List<IMonitoringMetric> metrics;
    private IConstraintEvaluator constraintEvaluator;
    private String constraint;
    
    public DummyBreachRepository() {
        constraintEvaluator = new SimpleConstraintEvaluator();
    }
    
    public DummyBreachRepository(String constraint) {
        this.metrics = null;
        constraintEvaluator = new SimpleConstraintEvaluator();
        this.constraint = constraint;
    }

    public DummyBreachRepository(String constraint, List<IMonitoringMetric> metrics) {
        this.metrics = new ArrayList<IMonitoringMetric>(metrics);
        constraintEvaluator = new SimpleConstraintEvaluator();
        this.constraint = constraint;
    }
    
    public void init(String constraint, List<IMonitoringMetric> metrics) {
        this.metrics = metrics;
        this.constraint = constraint;
    }
    
    @Override
    public List<EBreach> getBreachesByTimeRange(EAgreement agreement, String kpiName,
            Date begin, Date end) {

        if (metrics == null) {
            return Collections.<EBreach>emptyList();
        }
        
        List<EBreach> result = new ArrayList<EBreach>();
        for (IMonitoringMetric metric : metrics) {
            Date metricDate = metric.getDate();
            String metricValue = metric.getMetricValue();
            if (begin.before(metricDate) && end.after(metricDate) && isBreach(metric)) {
                result.add(newBreach(kpiName, metricValue, metricDate));
            }
        }
        
        return result;
    }

    @Override
    public void saveBreaches(List<EBreach> breaches) {

        System.out.println("Saving list of breaches: " + breaches.size());
        for (EBreach breach : breaches) {
            metrics.add(newMonitoringMetric(breach));
        }
    }

    private IMonitoringMetric newMonitoringMetric(final EBreach breach) {

        IMonitoringMetric result = new IMonitoringMetric() {
            @Override
            public Date getDate() {
                return breach.getDatetime();
            }
            @Override
            public String getMetricKey() {
                return breach.getKpiName();
            }
            @Override
            public String getMetricValue() {
                return breach.getValue();
            }
        };
        return result;
    }

    private EBreach newBreach(String kpiName, String value, Date date) {
        EBreach b = new EBreach();
        
        b.setKpiName(kpiName);
        b.setValue(value);
        b.setDatetime(date);

        return b;
    }

    /**
     * Fast and dirty function to know if the value is a breach (to don't setup a ConstraintEvaluator).
     */
    private boolean isBreach(IMonitoringMetric metric) {
        List<IMonitoringMetric> breaches = constraintEvaluator.evaluate(
                metric.getMetricKey(), 
                constraint, 
                Collections.singletonList(metric));
        
        return breaches.size() > 0;
    }
    
}