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
package eu.atos.sla.service.rest.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IPenaltyDAO;
import eu.atos.sla.dao.IPenaltyDAO.SearchParameters;
import eu.atos.sla.datamodel.EPenalty;
import eu.atos.sla.modelconversion.ModelConverter;
import eu.atos.sla.parser.data.Penalty;
import eu.atos.sla.service.rest.helpers.exception.ParserHelperException;

/**
 * Helper for Penalty Rest service.
 * @author rsosa
 */

@Service
@Transactional
public class PenaltyHelperE {
    private static Logger logger = LoggerFactory.getLogger(PenaltyHelperE.class);

    @Autowired
    public IPenaltyDAO penaltyDAO;
    
    @Autowired
    private ModelConverter modelConverter;

    public PenaltyHelperE() {
    }

    public Penalty getPenaltyByUuid(UUID uuid) {
        logger.debug("StartOf getViolationByUUID uuid:"+uuid);
        EPenalty storedPenalty = penaltyDAO.getByUuid(uuid.toString());
        Penalty penalty = modelConverter.getPenaltyXML(storedPenalty);
        logger.debug("EndOf getViolationByUUID");
        return penalty;
    }

    public List<Penalty> getPenalties(String agreementId, String guaranteeTerm, Date begin, Date end)
            throws ParserHelperException {
        logger.debug(
                "StartOf getPenaltiesByAgreementId agreementId:{} guaranteeTerm:{} begin:{}  end:{}", 
                agreementId, guaranteeTerm, begin, end);
        
        List<Penalty> penalties = new ArrayList<Penalty>();
        SearchParameters params = new SearchParameters();
        params.setAgreementId(agreementId);
        params.setGuaranteeTermName(guaranteeTerm);
        params.setBegin(begin);
        params.setEnd(end);
        
        List<EPenalty> storedPenalties = penaltyDAO.search(params);
        
        for (EPenalty storedPenalty : storedPenalties) {
            penalties.add(modelConverter.getPenaltyXML(storedPenalty));
        }
        return penalties;
    }
    
}
