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

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import eu.atos.sla.dao.IBreachDAO;
import eu.atos.sla.dao.IPenaltyDAO;
import eu.atos.sla.dao.IViolationDAO;
import eu.atos.sla.dao.IViolationDAO.SearchParameters;
import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EBreach;
import eu.atos.sla.datamodel.EPenaltyDefinition;
import eu.atos.sla.datamodel.EViolation;

/**
 * Implements the access to a repository that stores breaches, violations and compensations.
 * @author rsosa
 *
 */
public class Repository implements IBreachRepository, IViolationRepository, ICompensationRepository {

    @Autowired
    IBreachDAO breachDao;

    @Autowired
    IViolationDAO violationDao;
    
    @Autowired
    IPenaltyDAO penaltyDao;
    
    public Repository() {
    }

    @Override
    public List<EBreach> getBreachesByTimeRange(EAgreement agreement, String kpiName, Date begin, Date end) {

        List<EBreach> result = breachDao.getByTimeRange(agreement, kpiName, begin, end);
        return result;
    }

    @Override
    public void saveBreaches(List<EBreach> breaches) {

        for (EBreach breach : breaches) {
        
            breachDao.save(breach);
        }
    }

    @Override
    public List<EViolation> getViolationsByTimeRange(EAgreement agreement,
            String guaranteeTermName, Date begin, Date end) {
        SearchParameters params = newSearchParameters(agreement, guaranteeTermName, begin, end);
        List<EViolation> result = violationDao.search(params);
        
        return result;
    }

    @Override
    public Date getLastViolationDate(EAgreement agreement, String kpiName) {
        Date result = violationDao.getLastViolationDate(agreement, kpiName);
        return result;
    }

    private SearchParameters newSearchParameters(EAgreement agreement,
                                                 String guaranteeTermName, Date begin, Date end) {
        SearchParameters params = new SearchParameters();
        params.setAgreementId(agreement.getAgreementId());
        params.setGuaranteeTermName(guaranteeTermName);
        params.setBegin(begin);
        params.setEnd(end);
        return params;
    }

    @Override
    public Date getLastPenaltyDate(String agreementId, EPenaltyDefinition def, String kpiName) {
        Date result = penaltyDao.getLastPenaltyDate(agreementId, def, kpiName);
        return result;
    }
}
