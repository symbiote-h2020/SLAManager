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

import eu.atos.sla.datamodel.EAgreement.AgreementStatus;

public class AgreementTest {

    @Ignore
    @Test()
    public void testPojo() {

        StringBuilder agreementText = new StringBuilder();

        agreementText.append("text....");

        UUID uuid = UUID.randomUUID();
        AgreementStatus status = AgreementStatus.PENDING;
        String templateUuid = UUID.randomUUID().toString();

        ETemplate template = new ETemplate();
        template.setText("Template name 1");
        template.setUuid(templateUuid);

        EProvider provider = new EProvider(null, UUID.randomUUID().toString(),
                "Provider 2");

        EAgreement agreement = new EAgreement();
        agreement.setAgreementId(uuid.toString());
        agreement.setConsumer("Consumer2");
        agreement.setProvider(provider);
        agreement.setStatus(status);
        agreement.setTemplate(template);
        agreement.setText(agreementText.toString());

        assertEquals(uuid.toString(), agreement.getAgreementId());

    }
}
