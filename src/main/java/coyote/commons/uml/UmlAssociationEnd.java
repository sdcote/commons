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
public class UmlAssociationEnd extends UmlNamedElement {
  private static final Classifier CLASSIFIER = Classifier.ASSOCIATION_END;

  private static final Aggregation DEFAULT_AGGREGATION = Aggregation.NONE;
  private static final Scope DEFAULT_SCOPE = Scope.INSTANCE;

  private String type;
  private UmlMultiplicity multiplicity = null;
  private boolean navigable = false;
  private boolean ordered = false;
  private Changeability changeability = Changeability.NONE;

  private Aggregation aggregation = DEFAULT_AGGREGATION;
  private Scope targetScope = DEFAULT_SCOPE;




  /**
   * @param type
   */
  public UmlAssociationEnd(final String type) {
    Assert.isNotBlank(type);
    setType(type);
  }




  /**
   * @param type
   * @param name
   */
  public UmlAssociationEnd(final String type, final String name) {
    Assert.isNotBlank(type);
    setType(type);
    setName(name);
  }




  /**
   * @return the changeability
   */
  public Changeability getChangeability() {
    return changeability;
  }




  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }




  /**
   * @return the multiplicity
   */
  public UmlMultiplicity getMultiplicity() {
    return multiplicity;
  }




  /**
   * @return the type
   */
  public String getType() {
    return type;
  }




  /**
   * @return the navigable
   */
  public boolean isNavigable() {
    return navigable;
  }




  /**
   * @return the ordered
   */
  public boolean isOrdered() {
    return ordered;
  }




  /**
   * @param changeability the changeability to set
   */
  public void setChangeability(final Changeability changeability) {
    if (changeability != null) {
      this.changeability = changeability;
    } else {
      this.changeability = Changeability.NONE;
    }
  }




  /**
   * @param mltplcty the multiplicity to set
   */
  public void setMultiplicity(final UmlMultiplicity mltplcty) {
    this.multiplicity = mltplcty;
  }




  /**
   * @param flag the navigable to set
   */
  public void setNavigable(final boolean flag) {
    navigable = flag;
  }




  /**
   * @param flag the ordered to set
   */
  public void setOrdered(final boolean flag) {
    ordered = flag;
  }




  /**
   * @param type the type to set
   */
  public void setType(final String type) {
    this.type = type;
  }




  /**
   * @return the aggregation
   */
  public Aggregation getAggregation() {
    return aggregation;
  }




  /**
   * @param aggregation the aggregation to set
   */
  public void setAggregation(Aggregation aggregation) {
    if (aggregation != null) {
      this.aggregation = aggregation;
    } else {
      this.aggregation = DEFAULT_AGGREGATION;
    }
  }




  /**
   * @return the targetScope
   */
  public Scope getTargetScope() {
    return targetScope;
  }




  /**
   * @param scope the targetScope to set
   */
  public void setTargetScope(Scope scope) {
    if (scope != null) {
      targetScope = scope;
    } else {
      this.targetScope = DEFAULT_SCOPE;
    }
  }

}
