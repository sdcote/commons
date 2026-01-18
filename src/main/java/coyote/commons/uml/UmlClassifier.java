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
public class UmlClassifier extends UmlNamespace {
  private static final Classifier CLASSIFIER = Classifier.NAMESPACE;




  /**
   * @see UmlNamedElement#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }

  protected final List<UmlFeature> features = new ArrayList<UmlFeature>();
  protected final List<UmlDataType> types = new ArrayList<UmlDataType>();

  private boolean abstractFlag = false;




  public UmlClassifier() {}




  /**
   * @param name
   * @param id
   */
  public UmlClassifier(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlClassifier(String name) {
    super(name);
  }




  /**
   * @see UmlElement#isLeaf()
   */
  @Override
  public boolean isLeaf() {
    return (ownedElements.size() == 0 && features.size() == 0 && types.size() == 0);
  }




  /**
   * @return the abstractFlag
   */
  public boolean isAbstract() {
    return abstractFlag;
  }




  /**
   * @param abstractFlag the abstractFlag to set
   */
  public void setAbstract(boolean abstractFlag) {
    this.abstractFlag = abstractFlag;
  }




  public void addFeature(UmlFeature feature) {
    if (feature != null) {
      features.add(feature);
      feature.setParent(this);
    }
  }




  public List<UmlFeature> getFeatures() {
    List<UmlFeature> retval = new ArrayList<UmlFeature>();
    for (UmlFeature feature : features) {
      retval.add(feature);
    }
    return retval;
  }




  public void addDataType(UmlDataType type) {
    if (type != null) {
      types.add(type);
    }
  }




  public List<UmlDataType> getDataTypes() {
    List<UmlDataType> retval = new ArrayList<UmlDataType>();
    for (UmlDataType type : types) {
      retval.add(type);
    }
    return retval;
  }




  public UmlDataType getDataType(String name) {
    for (UmlDataType type : types) {
      if (type.getName().equals(name)) {
        return type;
      }
    }
    return null;
  }




  public UmlDataType findDataType(String name) {
    for (UmlDataType type : types) {
      if (type.getName().equalsIgnoreCase(name)) {
        return type;
      }
    }
    return null;
  }

}
