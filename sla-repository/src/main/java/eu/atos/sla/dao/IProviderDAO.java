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

import java.util.List;

import eu.atos.sla.datamodel.EProvider;

/**
 * DAO interface to access to the Provider information
 * 
 * @author Pedro Rey - Atos
 * 
 */
public interface IProviderDAO {

    /**
     * Returns the Provider from the database by its Id
     * 
     * @param id
     *            of the Provider
     * @return the corresponding Provider from the database
     */
    public EProvider getById(Long id);

    /**
     * Returns the Provider from the database by its UUID
     * 
     * @param id
     *            of the Provider
     * @return the corresponding Provider from the database
     */
    public EProvider getByUUID(String uuid);

    /**
     * Returns the Provider from the database by its name
     * 
     * @param id
     *            of the Provider
     * @return the corresponding Provider from the database
     */
    public EProvider getByName(String providerName);

    /**
     * Returns last Provider from the database
     * 
     * @param id
     *            of the Provider
     * @return the corresponding Provider from the database
     */
    public EProvider getLastProvider();

    /**
     * Returns all the Provider stored in the database
     * 
     * @return all the Provider stored in the database
     */
    public List<EProvider> getAll();

    /**
     * Stores a Provider into the database
     * 
     * @param Provider
     *            Provider to be saved.
     * @return <code>true</code> if the ProviderType was saved correctly
     * @throws Exception 
     */
    public EProvider save(EProvider provider);

    /**
     * Updates a Provider in the database
     * 
     * @param Provider
     *            Provider to be updated
     * @return <code>true</code> if the Provider was saved correctly
     */
    public boolean update(EProvider provider);

    /**
     * Deletes a Provider from the database
     * 
     * @param Provider
     *            to be deleted
     * @return <code>true</code> if the Provider was deleted correctly
     */
    public boolean delete(EProvider provider);

    
    
}
