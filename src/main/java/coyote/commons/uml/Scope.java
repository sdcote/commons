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
public enum Scope {
  /** The feature pertains to Instances of a Classifier. */
  INSTANCE("instance"),
  /** The feature pertains to an entire Classifier. */
  CLASSIFIER("classifier");

  private String name;




  private Scope(String s) {
    name = s;
  }




  /**
   * @see Enum#toString()
   */
  @Override
  public String toString() {
    return name;
  }




  public static Scope getScopeByName(String name) {
    if (name != null) {
      for (Scope type : Scope.values()) {
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
