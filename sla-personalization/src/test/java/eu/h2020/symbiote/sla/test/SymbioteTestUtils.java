package eu.h2020.symbiote.sla.test;

import eu.h2020.symbiote.model.mim.Comparator;
import eu.h2020.symbiote.model.mim.Federation;
import eu.h2020.symbiote.model.mim.FederationMember;
import eu.h2020.symbiote.model.mim.QoSConstraint;
import eu.h2020.symbiote.model.mim.QoSMetric;

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
  
}
