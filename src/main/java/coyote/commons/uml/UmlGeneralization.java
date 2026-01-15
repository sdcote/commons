/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

import coyote.commons.Assert;


/**
 *
 */
public class UmlGeneralization extends UmlNamedElement {
  private static final Classifier CLASSIFIER = Classifier.GENERALIZATION;

  private String subType;
  private String superType;




  public UmlGeneralization(String subtype, String supertype) {
    Assert.isNotBlank(subtype);
    Assert.isNotBlank(supertype);
    subType = subtype;
    superType = supertype;
  }




  /**
   * @return the subType
   */
  public String getSubType() {
    return subType;
  }




  /**
   * @param subType the subType to set
   */
  public void setSubType(String subType) {
    this.subType = subType;
  }




  /**
   * @return the superType
   */
  public String getSuperType() {
    return superType;
  }




  /**
   * @param superType the superType to set
   */
  public void setSuperType(String superType) {
    this.superType = superType;
  }




  public Classifier getClassifier() {
    return CLASSIFIER;
  }

}
