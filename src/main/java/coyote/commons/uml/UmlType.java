/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * Type is an abstract UML metaclass which represents a set of values and is 
 * used as a constraint on the range of values represented by associated typed 
 * element. 
 * 
 * <p>A typed element that has this type is constrained to represent values 
 * within the set of values. A typed element with no associated type may 
 * represent values of any type.</p> 
 */
public class UmlType extends UmlElement {
  private static final Classifier CLASSIFIER = Classifier.TYPE;




  /**
   * This returns the type of UML object this is; NOT the UML Classifier to 
   * which this belongs.
   * 
   * @return the Classifier (the type of UML object) for this object.
   */
  public Classifier getClassifier() {
    return CLASSIFIER;
  }




  public UmlType() {}




  /**
   * @param id
   */
  public UmlType(String id) {
    setId(id);
  }

}
