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
public class UmlDataType extends UmlClassifier {
  private static final Classifier CLASSIFIER = Classifier.DATATYPE;




  /**
   * @param name
   * @param id
   */
  public UmlDataType(String name, String id) {
    super(name, id);
    // TODO Auto-generated constructor stub
  }




  /**
   * @param name
   */
  public UmlDataType(String name) {
    super(name);
    // TODO Auto-generated constructor stub
  }




  /**
   * @see UmlNamedElement#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }

}
