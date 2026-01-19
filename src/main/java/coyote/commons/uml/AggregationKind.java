/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * 
 */
public enum AggregationKind {
  /** The property has no specific aggregation semantics. This is the default value. */
  NONE("none"),
  /** Represents Shared Aggregation (the "white diamond" in UML diagrams). */
  SHARED("shared"),
  /** Represents Composite Aggregation or Composition (the "black diamond" in UML diagrams). */
  COMPOSITE("composite");

  private String name;




  private AggregationKind(String s) {
    name = s;
  }




  /**
   * @see Enum#toString()
   */
  @Override
  public String toString() {
    return name;
  }




  public static AggregationKind getAggregationByName(String name) {
    if (name != null) {
      for (AggregationKind type : AggregationKind.values()) {
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
