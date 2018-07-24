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
package eu.atos.sla.parser.data;

import java.util.Date;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.atos.sla.datamodel.EViolation;
import eu.atos.sla.parser.DateTimeDeserializerJSON;
import eu.atos.sla.parser.DateTimeSerializerJSON;

/**
 * A POJO Object that stores all the information from a Violation
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "violation")
public class Violation  {

    @XmlElement(name = "uuid")
    private String uuid;
    @XmlElement(name = "agreement_id")
    private String agreementId;
    @XmlElement(name = "service_name")
    private String serviceName;
    @XmlElement(name = "service_scope")
    private String serviceScope;
    @XmlElement(name = "kpi_name")
    private String kpiName;
//    @JsonSerialize(using=DateTimeSerializerJSON.class)
    @JsonDeserialize(using=DateTimeDeserializerJSON.class)
    @XmlElement(name = "datetime")
    private Date datetime;
    @XmlElement(name = "expected_value")
    private String expectedValue;
    @XmlElement(name = "actual_value")
    private String actualValue;

    public Violation() {
    }
    
    public Violation(EViolation v) {
        this.uuid = v.getUuid();
        this.agreementId = v.getContractUuid();
        this.serviceName = v.getServiceName();
        this.serviceScope = v.getServiceScope();
        this.kpiName = v.getKpiName();
        this.datetime = v.getDatetime();
        this.expectedValue = v.getExpectedValue();
        this.actualValue = v.getActualValue();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContractUuid() {
        return agreementId;
    }

    public void setContractUuid(String contractUUID) {
        this.agreementId = contractUUID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceScope() {
        return serviceScope;
    }

    public void setServiceScope(String serviceScope) {
        this.serviceScope = serviceScope;
    }

    @Column(name = "metric_name")
    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String metricName) {
        this.kpiName = metricName;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

}