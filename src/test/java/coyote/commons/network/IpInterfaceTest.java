/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.network;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * 
 */
public class IpInterfaceTest {

  /**
   * Test method for {@link coyote.commons.network.IpInterface#getPrimary()}.
   */
  @Test
  public void testGetPrimary() {
    try {
      IpInterface intrfc = IpInterface.getPrimary();
      System.out.println( intrfc );
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

}
