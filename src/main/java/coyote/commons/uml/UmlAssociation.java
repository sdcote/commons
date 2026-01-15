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
 * &lt;UML:Association name="SimpleAssociation" xmi.id="EAID_043E6AB6_5725_4160_A908_03A2E380F393" visibility="public" isRoot="false" isLeaf="false" isAbstract="false"&gt;
 */
public class UmlAssociation extends UmlClassifier {
  private static final Classifier CLASSIFIER = Classifier.ASSOCIATION;

  // there should only be two, but the specification does not dictate so
  protected final List<UmlAssociationEnd> ends = new ArrayList<UmlAssociationEnd>();




  public UmlAssociation() {

  }




  /**
   * @param name
   * @param id
   */
  public UmlAssociation(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlAssociation(String name) {
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
   * @param end
   */
  public void addEnd(UmlAssociationEnd end) {
    ends.add(end);
  }




  public List<UmlAssociationEnd> getEnds() {
    return ends;
  }

}
