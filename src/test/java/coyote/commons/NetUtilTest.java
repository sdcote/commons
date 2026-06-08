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
package coyote.commons;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * 
 */
public class NetUtilTest {

  /**
   * @throws java.lang.Exception
   */
  @BeforeAll
  public static void setUpBeforeClass() throws Exception {}




  /**
   * @throws java.lang.Exception
   */
  @AfterAll
  public static void tearDownAfterClass() throws Exception {}




  /**
   * Test method for {@link coyote.commons.NetUtil#getNextAvailablePort(java.net.InetAddress)}.
   */
  public void testGetNextAvailablePortInetAddress() {
    fail( "Not yet implemented" ); // TODO
  }




  /**
   * Test method for {@link coyote.commons.NetUtil#getNextAvailablePort(int)}.
   */
  public void testGetNextAvailablePortInt() {
    fail( "Not yet implemented" ); // TODO
  }




  /**
   * Test method for {@link coyote.commons.NetUtil#getNextAvailablePort(java.net.InetAddress, int)}.
   */
  public void testGetNextAvailablePortInetAddressInt() {
    fail( "Not yet implemented" ); // TODO
  }




  /**
   * Test method for {@link coyote.commons.NetUtil#getNextServerSocket(java.net.InetAddress, int, int)}.
   */
  public void testGetNextServerSocket() {
    fail( "Not yet implemented" ); // TODO
  }




  /**
   * Test method for {@link coyote.commons.NetUtil#validatePort(int)}.
   */
  public void testValidatePort() {
    fail( "Not yet implemented" ); // TODO
  }




  /**
   * Test method for {@link coyote.commons.NetUtil#getLocalAddress()}.
   */
  public void testGetLocalAddress() {
    fail( "Not yet implemented" ); // TODO
  }




  @Test
  public void testIsNonRoutable() {
    // RFC 1918 Private ranges
    assertTrue(NetUtil.isNonRoutable("10.0.0.1"));
    assertTrue(NetUtil.isNonRoutable("10.255.255.255"));
    assertTrue(NetUtil.isNonRoutable("172.16.0.1"));
    assertTrue(NetUtil.isNonRoutable("172.31.255.255"));
    assertTrue(NetUtil.isNonRoutable("192.168.0.1"));
    assertTrue(NetUtil.isNonRoutable("192.168.255.255"));

    // Link-local range
    assertTrue(NetUtil.isNonRoutable("169.254.0.1"));
    assertTrue(NetUtil.isNonRoutable("169.254.255.255"));

    // Loopback range
    assertTrue(NetUtil.isNonRoutable("127.0.0.1"));
    assertTrue(NetUtil.isNonRoutable("127.255.255.255"));

    // Routable (public) IP addresses
    assertFalse(NetUtil.isNonRoutable("8.8.8.8"));
    assertFalse(NetUtil.isNonRoutable("1.1.1.1"));
    assertFalse(NetUtil.isNonRoutable("172.15.255.255"));
    assertFalse(NetUtil.isNonRoutable("172.32.0.0"));

    // Edge cases
    assertFalse(NetUtil.isNonRoutable(null));
    assertFalse(NetUtil.isNonRoutable(""));
    assertFalse(NetUtil.isNonRoutable("invalid-ip"));
  }


  /**
   * Test method for {@link coyote.commons.NetUtil#getBroadcastAddress(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testGetBroadcastAddress() {
    try {
      String mask = "255.255.0.0";
      InetAddress addr = NetUtil.getLocalBroadcast( mask );
      System.out.println( "Local broadcast for '" + mask + "' = " + addr.getHostName() );
    } catch ( Exception ex ) {
      fail( "Could calc local broadcast address " + ex.getMessage() );
    }

    try {
      String mask = "0.0.0.0";
      InetAddress addr = NetUtil.getLocalBroadcast( mask );
      System.out.println( "Local broadcast for '" + mask + "' = " + addr.getHostName() );
    } catch ( Exception ex ) {
      fail( "Could calc local broadcast address " + ex.getMessage() );
    }
  }

}
