/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * Enumeration of UML diagram types.
 */
public enum DiagramType {
  /** UseCase diagram - UML Use Case Diagrams capture the behavioral requirements of a system using use case elements, and their interaction with participant actors. */
  USECASECLASS("UseCase"),
  /** Activity diagram - UML Activity Diagrams describe low-level system behavior as a sequence of control and object flow paths. */
  ACTIVITY("Activity"),
  /** State diagram - UML State Machine Diagrams model the internal behavior of an element as a graph of states and transitions. */
  STATE("State"),
  /** Communication diagram - UML Communication Diagrams describe collaborations between elements as a holistic collection of messages passing between them. */
  COMMUNICATION("Collaboration"),
  /** Sequence diagram - UML Sequence Diagrams specify element behavior as a sequential series of messages passing back and forth, against a vertical time scale. */
  SEQUENCE("Sequence"),
  /** Timing diagram - UML Timing Diagrams describe the states of an element, and can also specify the messages passed between elements against a horizontal time scale. */
  TIMING("Timing"),
  /** Interaction diagram - UML Interaction Overview diagrams describe high-level behavior of a system as a sequence of control flows between interactions (or sequences).  */
  INTERACTION("InteractionOverview"),
  /** Class diagram - UML Class Diagrams capture the logical structure of a system as a series of classes, their features and the relationships between them. */
  CLASS("Class"),
  /** Component diagram - UML Component Diagrams define how a system is structured, describing the component 'blocks' and their connectivity with other components in a system. */
  COMPONENT("Component"),
  /** Package diagram - UML Package Diagrams describe the organization of packages, their elements and their corresponding relationships. */
  PACKAGE("Package"),
  /** Object diagram - UML Object Diagrams describe the interaction between class instances, and also specify instance values of features. */
  OBJECT("Object"),
  /** Composite diagram - UML Composite Structure Diagrams describe the internal structure of a structural element as an entity in a system. It can also describe the collaboration between elements to describe their function. */
  COMPOSITE("CompositeStructure"),
  /** Deployment diagram - UML Deployment Diagrams describe how and where system components are deployed onto physical nodes and other artifacts. */
  DEPLOYMENT("Deployment");

  private String name;




  private DiagramType(String s) {
    name = s;
  }




  /**
   * @see Enum#toString()
   */
  @Override
  public String toString() {
    return name;
  }




  public static DiagramType getDiagramTypeByName(String name) {
    if (name != null) {
      for (DiagramType type : DiagramType.values()) {
        if (name.equalsIgnoreCase(type.toString())) {
          return type;
        }
      }
    }
    return null;
  }




  public String getName() {
    return name;
  }

}
