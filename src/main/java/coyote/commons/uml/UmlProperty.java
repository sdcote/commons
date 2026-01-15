/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * Properties are structural features and often represented as Attributes of a 
 * class.
 */
public class UmlProperty extends UmlStructuralFeature {
  private static final Classifier CLASSIFIER = Classifier.PROPERTY;




  /**
   * @param name
   * @param id
   */
  public UmlProperty(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlProperty(String name) {
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
