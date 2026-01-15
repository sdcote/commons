/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * Named element is an abstract element that may have a name. 
 * 
 * <p>The name is used for identification of the named element within the 
 * namespaces in which it is defined or accessible.</p>
 * 
 * <p>UML specification allows named element to be anonymous, i.e. to have no 
 * name. Also, the absence of the name is considered to be different from the 
 * element having empty name.</p>
 * 
 * <p>A named element could also have a qualified name that allows it to be 
 * unambiguously identified within a hierarchy of nested namespaces. Qualified 
 * name is constructed from the names of the containing namespaces starting at 
 * the root of the hierarchy and ending with the name of the named element 
 * itself and separated by "::". If element has no name or one of the 
 * containing namespaces has no name, then element has no qualified name.</p>
 * 
 * <p>Named element could have a visibility. Visibility defines namespaces 
 * where the named element is visible or accessible. If a named element is not 
 * owned by any namespace, then it does not have a visibility.</p> 
 */
public abstract class UmlNamedElement extends UmlElement {

  private String name = null;




  public UmlNamedElement() {}




  public UmlNamedElement(String name) {
    setName(name);
  }




  public UmlNamedElement(String name, String id) {
    setName(name);
    setId(id);
  }




  /**
   * This returns the type of UML object this is; NOT the UML Classifier to 
   * which this belongs.
   * 
   * <p>An named element is a base class for nearly all the other classes in 
   * this API and when dealing with an object at the Named Element level it is 
   * useful to query the type of Named Element this is. This allows a more 
   * programmatic method of determining the tyep of object it is without using 
   * reflection.</p>
   *  
   * @return the Classifier (the type of UML object) for this object.
   */
  public abstract Classifier getClassifier();




  /**
   * @return the name
   */
  public String getName() {
    return name;
  }




  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

}
