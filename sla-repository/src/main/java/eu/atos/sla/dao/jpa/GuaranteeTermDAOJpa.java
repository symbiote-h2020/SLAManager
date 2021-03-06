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
package eu.atos.sla.dao.jpa;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IGuaranteeTermDAO;
import eu.atos.sla.datamodel.EGuaranteeTerm;

@Repository("GuaranteeTermRepository")
public class GuaranteeTermDAOJpa extends AbstractDAOJpa<EGuaranteeTerm> implements IGuaranteeTermDAO {
    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(GuaranteeTermDAOJpa.class);

    public GuaranteeTermDAOJpa() {
        super(EGuaranteeTerm.class);
    }
    
    public boolean update(EGuaranteeTerm guaranteeTerm) {
        entityManager.merge(guaranteeTerm);
        entityManager.flush();
        return true;
    }

}
