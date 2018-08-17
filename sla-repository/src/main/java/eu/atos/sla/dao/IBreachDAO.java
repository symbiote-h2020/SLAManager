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
package eu.atos.sla.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import eu.atos.sla.datamodel.EAgreement;
import eu.atos.sla.datamodel.EBreach;

/**
 * DAO interface to access to the Breach information
 * 
 * @author Pedro Rey - Atos
 * 
 */
public interface IBreachDAO  {

    /**
     * Returns the Breach from the database by its Id
     * 
     * @param id
     *            of the Breach
     * @return the corresponding Breach from the database
     */
    public EBreach getById(Long id);

    /**
     * Returns the Breach from the database by its Id
     * 
     * @param id
     *            of the Breach
     * @return the corresponding Breach from the database
     */
    public EBreach getBreachByUUID(UUID uuid);

    /**
     * Returns all the Breach stored in the database
     * 
     * @return all the Breach stored in the database
     */
    public List<EBreach> getAll();

    /**
     * Stores a Breach into the database
     * 
     * @param Breach
     *            Breach to be saved.
     * @return <code>true</code> if the BreachType was saved correctly
     * @throws Exception 
     */
    public EBreach save(EBreach breach);

    /**
     * Updates a Breach in the database
     * 
     * @param Breach
     *            Breach to be updated
     * @return <code>true</code> if the Breach was saved correctly
     */
    public boolean update(EBreach breach);

    /**
     * Deletes a Breach from the database
     * 
     * @param Breach
     *            to be deleted
     * @return <code>true</code> if the Breach was deleted correctly
     */
    public boolean delete(EBreach breach);

    /**
     * Retrieves all the breaches associated with the given contract and
     * variable, within a time interval (inclusive ends)
     */
    public List<EBreach> getByTimeRange(EAgreement contract,
            String variable, Date begin, Date end);
}
