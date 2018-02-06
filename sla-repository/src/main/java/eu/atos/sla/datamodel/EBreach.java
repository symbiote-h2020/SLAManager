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
package eu.atos.sla.datamodel;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * A POJO Object that stores all the information from a Breach
 * 
 * @author Pedro Rey - Atos
 */
@Entity(name = "Breach")
@Table(name = "breach")
@NamedQueries({
        @NamedQuery(name = EBreach.QUERY_FIND_ALL, query = "SELECT p FROM Breach p"),
        @NamedQuery(name = EBreach.QUERY_FIND_BY_UUID, query = "SELECT p FROM Breach p where p.agreementUuid = :uuid "),
        @NamedQuery(name = EBreach.QUERY_FIND_BY_TIME_RANGE, query = "SELECT p FROM Breach p where p.agreementUuid = :uuid "
                + "and (:begin is null or p.datetime > :begin) "
                + "and (:end is null or p.datetime < :end) "
                + "and (:variable is null or p.kpiName = :variable) ") })
public class EBreach extends AbstractEntity<EBreach> implements Serializable {

    public final static String QUERY_FIND_ALL = "Breach.findAll";
    public final static String QUERY_FIND_BY_UUID = "Breach.findByUuid";
    public final static String QUERY_FIND_BY_TIME_RANGE = "Breach.findByTimeRange";

    private static final long serialVersionUID = -7800841772508669987L;
    private String contractUUID;
    private Date datetime;
    private String metricName;
    private String value;

    public EBreach() {
    }

    /**
     * UUID of the agreement this breach belongs to
     */
    @Column(name = "contract_uuid")
    public String getAgreementUuid() {
        return contractUUID;
    }

    public void setAgreementUuid(String contractUUID) {
        this.contractUUID = contractUUID;
    }

    /**
     * Date and time of the metric that has generated this breach.
     */
    @Column(name = "datetime")
    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    /**
     * Name of the kpiName that has generated this breach.
     */
    @Column(name = "kpi_name")
    public String getKpiName() {
        return metricName;
    }

    public void setKpiName(String metricName) {
        this.metricName = metricName;
    }

    /**
     * Value of the metric that has generated this breach.
     */
    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
