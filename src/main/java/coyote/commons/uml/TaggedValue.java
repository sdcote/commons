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
public class TaggedValue {
  private UmlElement element = null;
  private String name = null;
  private String value = null;




  public TaggedValue(String name, String value, UmlElement element) {
    setName(name);
    setValue(value);
    setElement(element);
  }




  public TaggedValue(String name, String value) {
    setName(name);
    setValue(value);
  }




  /**
   * @return the name
   */
  public String getName() {
    return name;
  }




  /**
   * @param tagName the name to set
   */
  public void setName(String tagName) {
    name = tagName;
  }




  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }




  /**
   * @param tagValue the value to set
   */
  public void setValue(String tagValue) {
    value = tagValue;
  }




  /**
   * @return the modelElement
   */
  public UmlElement getElement() {
    return element;
  }




  /**
   * @param element the modelElement to set
   */
  public void setElement(UmlElement element) {
    this.element = element;
  }
}
