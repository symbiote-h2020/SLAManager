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
package eu.atos.sla.modelconversion;

import java.io.StringReader;

import javax.xml.bind.JAXBException;

import eu.atos.sla.SlaRuntimeException;
import eu.atos.sla.XmlParser;
import eu.atos.sla.common.collections.TransformingList.Adapter;
import eu.atos.sla.datamodel.ETemplate;
import eu.atos.sla.parser.data.wsag.Template;

/**
 * TransformList.Adapter to transform templates from entity model to WSAG model
 */
public final class TemplateEntityToWsagAdapter implements Adapter<ETemplate, Template> {
    
    private XmlParser<Template> xmlParser;
    
    public TemplateEntityToWsagAdapter(XmlParser<Template> xmlParser) {
        this.xmlParser = xmlParser;
    }

    @Override
    public Template apply(ETemplate from) {
        try {
            return xmlParser.deserialize(new StringReader(from.getText()));
        } catch (JAXBException e) {
            throw new SlaRuntimeException(e.getMessage(), e);
        }
    }
}