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
public enum Aggregation {
  /** Composition */
  COMPOSITE("composite"),
  /** Aggregation */
  SHARED("shared"),
  /** Not specified */
  NONE("none");

  private String name;




  private Aggregation(String s) {
    name = s;
  }




  /**
   * @see Enum#toString()
   */
  @Override
  public String toString() {
    return name;
  }




  public static Aggregation getAggregationByName(String name) {
    if (name != null) {
      for (Aggregation type : Aggregation.values()) {
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
