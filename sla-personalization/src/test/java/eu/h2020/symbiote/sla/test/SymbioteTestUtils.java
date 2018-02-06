/**
 * Copyright 2017 Atos
 * Contact: Atos <jose.sanchezm@atos.net>
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
package eu.h2020.symbiote.sla.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.h2020.symbiote.model.mim.Comparator;
import eu.h2020.symbiote.model.mim.Federation;
import eu.h2020.symbiote.model.mim.FederationMember;
import eu.h2020.symbiote.model.mim.QoSConstraint;
import eu.h2020.symbiote.model.mim.QoSMetric;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.util.ArrayList;
import java.util.List;

public class SymbioteTestUtils {
  
  public static Federation createTestFederation(String federationId, String platformId) {
    Federation federation = new Federation();
    federation.setId(federationId);
  
    List<FederationMember> members = new ArrayList<>();
  
    FederationMember myself = new FederationMember();
    myself.setPlatformId(platformId);
  
    members.add(myself);
  
    federation.setMembers(members);
  
    List<QoSConstraint> constraints = new ArrayList<>();
  
    QoSConstraint availability = new QoSConstraint();
    availability.setMetric(QoSMetric.availability);
    availability.setResourceType("actuator");
    availability.setDuration(30);
    availability.setComparator(Comparator.greaterThanOrEqual);
    availability.setThreshold(0.9);
  
    constraints.add(availability);
  
    QoSConstraint load = new QoSConstraint();
    load.setMetric(QoSMetric.load);
    load.setComparator(Comparator.lessThan);
    load.setThreshold(75);
  
    constraints.add(load);
  
  
    federation.setSlaConstraints(constraints);
    
    return federation;
  }
  
  public static Message toMessage(Federation federation) {
    try {
      byte[] body = new ObjectMapper().writeValueAsBytes(federation);
      
      MessageProperties properties = new MessageProperties();
      properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
      
      return new Message(body, properties);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }
  
}
