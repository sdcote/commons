/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * Feature represents a structural or behavioral characteristic of a classifier 
 * or of instances of classifiers. 
 */
public abstract class UmlFeature extends UmlNamedElement {
  private static final Classifier CLASSIFIER = Classifier.FEATURE;




  /**
   * @param name
   * @param id
   */
  public UmlFeature(String name, String id) {
    super(name, id);
  }




  /**
   * @see UmlNamedElement#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }




  /**
   * @param name
   */
  public UmlFeature(String name) {
    super(name);
  }

  private boolean staticFlag = false;

  private static final Scope DEFAULT_SCOPE = Scope.INSTANCE;
  private Scope ownerScope = DEFAULT_SCOPE;
  private Scope targetScope = DEFAULT_SCOPE;

  private static final Changeability DEFAULT_CHANGEABILITY = Changeability.CHANGABLE;
  private Changeability changeable = DEFAULT_CHANGEABILITY;




  /**
   * @return the staticFlag
   */
  public boolean isStatic() {
    return staticFlag;
  }




  /**
   * @param flag the staticFlag to set
   */
  public void setStatic(boolean flag) {
    this.staticFlag = flag;
  }




  /**
   * @return the ownerScope
   */
  public Scope getOwnerScope() {
    return ownerScope;
  }




  /**
   * @param ownerScope the ownerScope to set
   */
  public void setOwnerScope(Scope ownerScope) {
    this.ownerScope = ownerScope;
  }




  /**
   * @return the targetScope
   */
  public Scope getTargetScope() {
    return targetScope;
  }




  /**
   * @param targetScope the targetScope to set
   */
  public void setTargetScope(Scope targetScope) {
    this.targetScope = targetScope;
  }




  /**
   * @return the changeable
   */
  public Changeability getChangeability() {
    return changeable;
  }




  /**
   * @param changeable the changeable to set
   */
  public void setChangeability(Changeability changeable) {
    this.changeable = changeable;
  }

}
