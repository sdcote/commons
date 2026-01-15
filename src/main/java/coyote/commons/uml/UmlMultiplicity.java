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
public class UmlMultiplicity {
  private static final Classifier CLASSIFIER = Classifier.MULTIPLICITY;

  private int minimum = 0;
  private int maximum = 1;
  public static final int ANY = -1;




  /**
   * @return the lower-bound of this multiplicity
   */
  public int getLowerBound() {
    return minimum;
  }




  /**
   * @param min the lower-bound to set
   */
  public void setLowerBound(int min) {
    if (min < 0) {
      this.minimum = 0;
    } else {
      this.minimum = min;
    }
  }




  /**
   * @return the upper-bound of this multiplicity
   */
  public int getUpperBound() {
    return maximum;
  }




  /**
   * @param max the upper-bound to set
   */
  public void setUpperBound(int max) {
    if (max < 0) {
      this.maximum = ANY;
    } else {
      if (max < minimum) {
        throw new IllegalArgumentException("Maximum cannot be less than minimum");
      }
      this.maximum = max;
    }
  }




  /**
   * @see Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append(minimum);
    b.append("..");
    if (maximum < 0) {
      b.append('*');
    } else {
      b.append(maximum);
    }
    return b.toString();
  }




  /**
   * @return the classifier
   */
  public static Classifier getClassifier() {
    return CLASSIFIER;
  }

}
