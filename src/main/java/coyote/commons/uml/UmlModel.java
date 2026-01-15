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
 * This represent a UML model.
 */
public class UmlModel extends UmlClassifier {
  private static final Classifier CLASSIFIER = Classifier.MODEL;

  private final List<UmlDiagram> diagrams = new ArrayList<UmlDiagram>();




  /**
   * @param name
   * @param id
   */
  public UmlModel(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlModel(String name) {
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
   * Models always contain root elements so don't set a parent.
   * 
   * @see UmlNamespace#addElement(UmlNamedElement)
   */
  @Override
  public void addElement(UmlNamedElement child) {
    ownedElements.add(child);
  }




  public void addDiagram(UmlDiagram diagram) {
    diagrams.add(diagram);
  }




  /**
   * @return the diagrams owned by this model
   */
  public List<UmlDiagram> getDiagrams() {
    return diagrams;
  }

}
