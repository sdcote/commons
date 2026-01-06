/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
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


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This is a set of tests which demonstrate how to use the StatBoard class in
 * regular operations.
 * 
 * <p>Demo tests are used to illustrate how to use public portions of the code 
 * and are intended to be illustrative and interrogatory.
 */
public class StatBoardDemo {

  /**
   * @throws Exception
   */
  @BeforeAll
  public static void setUpBeforeClass() throws Exception {}




  /**
   * @throws Exception
   */
  @AfterAll
  public static void tearDownAfterClass() throws Exception {}




  @Test
  public void testGetId() {
    StatBoard scorecard = new StatBoardImpl();

    // All scorecards should have an identifier
    assertNotNull( scorecard.getId() );

  }

}
