/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * 
 */
public enum Classifier {
  /**  */
  CLASS("Class"),
  /**  */
  DEPENDENCY("Dependency"),
  /**  */
  ASSOCIATION("Association"),
  /** */
  ASSOCIATION_END("AssociationEnd"),
  /**  */
  ELEMENT("Element"),
  /**  */
  NAMED_ELEMENT("NamedElement"),
  /**  */
  PACKAGE("Package"),
  /**  */
  CLASSIFIER("Classifier"),
  /**  */
  COMPONENT("Component"),
  /**  */
  NAMESPACE("Namespace"),
  /**  */
  DATATYPE("DataType"),
  /** Represents a kind of structural feature; often an attribute of a class */
  PROPERTY("Property"),
  /** Represents a kind of behavioral feature */
  OPERATION("Operation"),
  /** */
  INTERFACE("Interface"),
  /** */
  MODEL("Model"),
  /** Support for 1.x notion of a class property */
  ATTRIBUTE("Attribute"),
  /** */
  STEREOTYPE("Stereotype"),
  /** */
  MULTIPLICITY("Multiplicity"),
  /** */
  STRUCTURAL_FEATURE("StructuralFeature"),
  /** */
  BEHAVIORAL_FEATURE("BehavioralFeature"),
  /** */
  FEATURE("Feature"),
  /** */
  TYPE("Type"),
  /** */
  GENERALIZATION("Generalization"),
  /** */
  DIAGRAM_ELEMENT("DiagramElement"),
  /** */
  UML_SHAPE("UMLShape"),
  /** */
  UML_EDGE("UMLEdge"),
  /** */
  DIAGRAM_FRAME("DiagramFrame"),
  /** */
  DIAGRAM("Diagram"),
  /** */
  NODE("Node"),
  /** */
  PORT("Port");

  private String name;




  private Classifier(String s) {
    name = s;
  }




  /**
   * @see Enum#toString()
   */
  @Override
  public String toString() {
    return name;
  }




  public static Classifier getClassifierByName(String name) {
    if (name != null) {
      for (Classifier type : Classifier.values()) {
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
