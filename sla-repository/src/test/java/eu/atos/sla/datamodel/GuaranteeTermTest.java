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

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;


public class GuaranteeTermTest {

    @Ignore
    @Test
    public void testPojo() {

        @SuppressWarnings("unused")
        String consumerUuid = UUID.randomUUID().toString();

        EGuaranteeTerm guaranteeTerm = new EGuaranteeTerm();
        
        guaranteeTerm.setName("guarantee term name");
        guaranteeTerm.setServiceName("service Name");
        

        assertEquals("guarantee term name", guaranteeTerm.getName());
        assertEquals("service Name", guaranteeTerm.getServiceName());
        

    }
}