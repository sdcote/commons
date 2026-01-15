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
public class UmlOperation extends UmlBehavioralFeature {
  private static final Classifier CLASSIFIER = Classifier.OPERATION;

  private boolean query = false;




  /**
   * @param name
   * @param id
   */
  public UmlOperation(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlOperation(String name) {
    super(name);
  }




  /**
   * @return the query
   */
  public boolean isQuery() {
    return query;
  }




  /**
   * @param query the query to set
   */
  public void setQuery(boolean query) {
    this.query = query;
  }




  /**
   * @see UmlNamedElement#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }

}
