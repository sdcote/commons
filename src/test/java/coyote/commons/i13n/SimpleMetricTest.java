/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and implementation
 */
package coyote.commons.i13n;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 
 */
public class SimpleMetricTest {


  @Test
  public void testToString() {
    SimpleMetric metric = new SimpleMetric( "test", "bytes" );
    metric.sample( 3 );
    metric.sample( 6 );
    metric.sample( 9 );
    assertTrue( metric.getAvgValue() == 6 );
  }

}
