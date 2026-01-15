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
public class UmlStereotype extends UmlNamedElement {
  private static final Classifier CLASSIFIER = Classifier.STEREOTYPE;




  /**
   * @see UmlNamedElement#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }




  /**
   * @param name
   * @param id
   */
  public UmlStereotype(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlStereotype(String name) {
    super(name);
  }

}
