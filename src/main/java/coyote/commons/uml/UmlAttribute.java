/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * Attributes are specialization of Properties and are therefore structural 
 * features of a class. This is here for 1.x compatibility; 2.x uses Properties.
 */
public class UmlAttribute extends UmlStructuralFeature {
  private static final Classifier CLASSIFIER = Classifier.ATTRIBUTE;




  /**
   * @param name
   * @param id
   */
  public UmlAttribute(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlAttribute(String name) {
    super(name);
  }




  /**
   * @see UmlNamedElement#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }

}
