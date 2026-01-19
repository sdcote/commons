/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * Properties are structural features and often represented as Attributes of a 
 * class.
 */
public class UmlProperty extends UmlStructuralFeature {
  private static final Classifier CLASSIFIER = Classifier.PROPERTY;

  /** The ID of the type of data this property holds. */
  private String typeId = null;

  private AggregationKind aggregation = AggregationKind.NONE;




  /**
   * @param name
   * @param id
   */
  public UmlProperty(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlProperty(String name) {
    super(name);
  }




  /**
   * @see UmlNamedElement#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }


  
  /**
   * @return the identifier of the type this property is
   */
  public String getTypeId() {
    return typeId;
  }




  /**
   * @param typeId the flag to set
   */
  public void setTypeId(String typeId) {
    this.typeId = typeId;
  }


    
  public AggregationKind getAggregation() {
    return aggregation;
  }




  public void setAggregation(AggregationKind kind) {
    this.aggregation = kind;
  }

}
