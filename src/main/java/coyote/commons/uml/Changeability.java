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
public enum Changeability {
  /** Any change is permitted */
  CHANGABLE("changeable"),
  /** No changes are permitted once added */
  FROZEN("frozen"),
  /** Only adds are permitted */
  ADD_ONLY("addOnly"),
  /** Not specified */
  NONE("none");

  private String name;




  private Changeability(String s) {
    name = s;
  }




  /**
   * @see Enum#toString()
   */
  @Override
  public String toString() {
    return name;
  }




  public static Changeability getChangeabilityByName(String name) {
    if (name != null) {
      for (Changeability type : Changeability.values()) {
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
