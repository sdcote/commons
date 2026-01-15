/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * Namespace is an abstract named element that contains (or owns) a set of 
 * named elements that can be identified by name. In other words, namespace is 
 * a container for named elements. 
 */
public class UmlNamespace extends UmlNamedElement {
  private static final Classifier CLASSIFIER = Classifier.NAMESPACE;




  /**
   * @see UmlNamedElement#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }




  public UmlNamespace() {}




  /**
   * @param name
   * @param id
   */
  public UmlNamespace(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlNamespace(String name) {
    super(name);
  }

}
