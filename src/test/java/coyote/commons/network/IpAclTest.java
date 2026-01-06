/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.network;

//import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 
 */
public class IpAclTest {






  /**
   * Test method for {@link coyote.commons.network.IpAcl#IpAcl()}.
   * 
   * @throws Exception 
   */
  @Test
  public void testIpAcl() throws Exception {
    IpAcl acl = new IpAcl();
    acl.add( "192.168/16", true );
    acl.add( "10/8", false );
  }




  /**
   * Test method for {@link coyote.commons.network.IpAcl#allows(java.lang.String)}.
   * @throws Exception 
   */
  @Test
  public void testAllowsString() throws Exception {

    // Network rule - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    IpAcl acl = new IpAcl( IpAcl.DENY ); // New ACL with DENY as default
    acl.add( "192.168/16", true ); // add local subnet to be allowed (true)
    acl.add( "10/8", false ); // add a deny (false) rule for another subnet

    // Local should pass
    String arg = "192.168.1.100";
    assertTrue( acl.allows( arg ) ,"Should allow '" + arg + "'" );

    // Other local should not
    arg = "10.8.107.12";
    assertFalse(  acl.allows( arg ) ,"Should NOT allow '" + arg + "'");

    arg = "150.22.78.212";
    assertFalse(  acl.allows( arg ) ,"Should NOT allow '" + arg + "'");

    // Single Address - - - - - - - - - - - - - - - - - - - - - - - - - - -

    acl = new IpAcl( IpAcl.DENY );

    // Only allow this one IP address
    acl.add( "192.168.1.100/32", IpAcl.ALLOW );

    // This should pass
    arg = "192.168.1.100";
    assertTrue(  acl.allows( arg ) ,"Should allow '" + arg + "'");

    // These should not pass
    arg = "10.8.107.12";

    assertTrue(  !acl.allows( arg ),"Should NOT allow '" + arg + "'" );

    arg = "192.168.1.101";

    assertTrue(  !acl.allows( arg ),"Should NOT allow '" + arg + "'" );
  }




  @Test
  public void testParse() throws Exception {
    //
    String rules = " 192.168/16:ALLOW;10/8:ALLOW;172.26.39/23:ALLOW;DEFAULT:DENY";
    IpAcl acl = new IpAcl();
    acl.parse( rules );

    // Local should pass
    String arg = "192.168.1.100";
    assertTrue(  acl.allows( arg ) ,"Should allow '" + arg + "'");

    arg = "10.8.107.12";
    assertTrue(acl.allows( arg ), "Should allow '" + arg + "'" );

    arg = "150.22.78.212";
    assertFalse(  acl.allows( arg ) ,"Should NOT allow '" + arg + "'");
  }

  @Test
  public void testConstrucor() {
    try {
      IpAcl acl = new IpAcl();
      acl.add("192.168/16", true);
      acl.add("10/8", false);
    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }
  }




  @Test
  public void testBasic() {
    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);
      acl.add("172/8", true);
      acl.add("10/8", true);
      acl.add("192.168/16", true);

      String arg = "172.17.0.1";
      assertTrue( acl.allows(arg),"Should allow '" + arg + "'");

      InetAddress address = InetAddress.getByName(arg);
      System.out.println(address);
      assertTrue(acl.allows(address),"Should allow '" + address + "'");

    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }

  }


  @Test
  public void testDenySpecific() {
    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);

      // Order is important! specific addresses should be specified first and broader scopes later since the first
      // network rule matching the argument is returned. This is by design to keep the  matching logic fast.
      acl.add("172.17.0.2/32", false); // deny this specific address in the allowed range
      acl.add("172/8", true); // allow the rest of the network
      System.out.println(acl);

      String arg = "172.17.0.1";
      assertTrue( acl.allows(arg),"Should allow '" + arg + "'");

      InetAddress address = InetAddress.getByName(arg);
      System.out.println(address);
      assertTrue( acl.allows(address),"Should allow '" + address + "'");

      arg = "172.17.0.2";
      assertFalse(acl.allows(arg),"Should deny '" + arg + "'");

      address = InetAddress.getByName(arg);
      assertFalse( acl.allows(address),"Should deny '" + address + "'");

      arg = "172.17.0.3";
      assertTrue( acl.allows(arg),"Should allow '" + arg + "'");

      address = InetAddress.getByName(arg);
      System.out.println(address);
      assertTrue( acl.allows(address),"Should allow '" + address + "'");


    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }

  }




  @Test
  public void testAllows() {
    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);
      acl.add("192.168/16", true);
      acl.add("10/8", false);

      String arg = "192.168.1.100";
      assertTrue( acl.allows(arg),"Should allow '" + arg + "'");

      arg = "10.8.107.12";

      assertFalse( acl.allows(arg),"Should NOT allow '" + arg + "'");

      // if( acl.allows( arg ) )
      // {
      // System.out.println( "Error: ACL allows '" + arg + "'" );
      // }
      // else
      // {
      // System.out.println( "ACL denies '" + arg + "'" );
      // }
    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }

    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);

      // Only allow this one IP address
      acl.add("192.168.1.100/32", IpAcl.ALLOW);

      // This should pass
      String arg = "192.168.1.100";
      assertTrue(acl.allows(arg),"Should allow '" + arg + "'");

      // These should not pass
      arg = "10.8.107.12";

      assertFalse(acl.allows(arg),"Should NOT allow '" + arg + "'");

      arg = "192.168.1.101";

      assertFalse( acl.allows(arg),"Should NOT allow '" + arg + "'");
    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }

    // Test the ordering, 192.168.100 subnet is denied, but the rest of 192.168
    // is allowed
    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);
      acl.add("192.168.100/24", false);
      acl.add("192.168/16", true);

      String arg = "192.168.100.23";
      assertFalse( acl.allows(arg),"Should NOT allow '" + arg + "'");

      arg = "192.168.23.100";
      assertTrue(acl.allows(arg),"Should allow '" + arg + "'");

      arg = "10.8.107.12";
      assertFalse(acl.allows(arg),"Should NOT allow '" + arg + "'");

    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }

  }




  @Test
  public void testAllowAll() {
    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);
      acl.add("255.255.255.255/0", true);
      String arg = "192.168.1.100";
      assertTrue( acl.allows(arg),"Should allow '" + arg + "'");
    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }
    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);
      acl.add("0/0", true);
      String arg = "192.168.1.100";
      assertTrue( acl.allows(arg),"Should allow '" + arg + "'");
    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }
  }
}
