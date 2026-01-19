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
public class UmlPort extends UmlProperty {
  private static final Classifier CLASSIFIER = Classifier.PORT;
  private boolean serviceFlag = true;
  private boolean conjugatedFlag = false;

  /**
   * @param name
   * @param id
   */
  public UmlPort(String name, String id) {
    super(name, id);
    setAggregation(AggregationKind.COMPOSITE);
  }

  /**
   * @param name
   */
  public UmlPort(String name) {
    super(name);
    setAggregation(AggregationKind.COMPOSITE);
  }

  /**
   * @see UmlNamedElement#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }

  /**
   * @return true if the port represent a service
   */
  public boolean isService() {
    return serviceFlag;
  }

  /**
   * Set if the port represents a service.
   * 
   * @param flag the value to set the flag, true=the port represents a service.
   */
  public void setServiceFlag(boolean flag) {
    this.serviceFlag = flag;
  }

  /**
   * @return the conjugated flag value
   */
  public boolean isConjugated() {
    return conjugatedFlag;
  }

  /**
   * @param readOnly the flag to set
   */
  public void setConjugated(boolean flag) {
    this.conjugatedFlag = flag;
  }

}
