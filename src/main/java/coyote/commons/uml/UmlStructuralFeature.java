/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 */
public abstract class UmlStructuralFeature extends UmlFeature {
  private static final Classifier CLASSIFIER = Classifier.STRUCTURAL_FEATURE;

  protected final List<UmlType> types = new ArrayList<UmlType>();

  private boolean readOnly = false;




  /**
   * @param name
   * @param id
   */
  public UmlStructuralFeature(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlStructuralFeature(String name) {
    super(name);
  }




  /**
   * @return the readOnly
   */
  public boolean isReadOnly() {
    return readOnly;
  }




  /**
   * @param readOnly the readOnly to set
   */
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }




  /**
   * @see UmlFeature#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }




  public void addType(UmlType type) {
    types.add(type);
  }




  /**
   * @return the list of owned model elements
   */
  public List<UmlType> getTypes() {
    return types;
  }

}
