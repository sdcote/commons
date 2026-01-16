/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * 
 */
public class UmlNode extends UmlClassifier {
  private static final Classifier CLASSIFIER = Classifier.NODE;




  /**
   * @param name
   * @param id
   */
  public UmlNode(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlNode(String name) {
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
