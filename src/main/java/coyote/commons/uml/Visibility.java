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
public enum Visibility {
  /** Visible to everything */
  PUBLIC("public"),
  /** Only visible to the element */
  PRIVATE("private"),
  /** Only visible to the element and its specializations */
  PROTECTED("protected"),
  /** Only visible to the elements in the same package / namespace */
  PACKAGE("package"),
  /** Inaccessible to other objects */
  IMPLEMENTATION("implementation"),
  /** Override of an interface method that should be treated as private, even if it's declared public */
  FORCED("forced public");

  private String name;




  private Visibility(String s) {
    name = s;
  }




  /**
   * @see Enum#toString()
   */
  @Override
  public String toString() {
    return name;
  }




  public static Visibility getVisibilityByName(String name) {
    if (name != null) {
      for (Visibility type : Visibility.values()) {
        if (name.equalsIgnoreCase(type.toString())) {
          return type;
        }
      }
    }
    return null;
  }




  public String getName() {
    return name;
  }

}
