/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * 
 */
public class CredentialSetTest {

  /**
   * Test method for {@link coyote.commons.security.CredentialSet#CredentialSet()}.
   */
  @Test
  public void testCredentialSet() {
    CredentialSet creds = new CredentialSet();
    assertNotNull( creds );
  }




  /**
   * Test method for {@link coyote.commons.security.CredentialSet#CredentialSet(int)}.
   */
  @Test
  public void testCredentialSetInt() {
    CredentialSet creds = new CredentialSet( 1 );
    assertNotNull( creds );
    assertTrue( creds.getRounds() == 1 );
  }




  /**
   * Test method for {@link coyote.commons.security.CredentialSet#getRounds()}.
   */
  @Test
  public void testGetRounds() {
    CredentialSet creds = new CredentialSet();
    assertNotNull( creds );
    assertTrue( creds.getRounds() == 0 );
  }




  /**
   * Test method for {@link coyote.commons.security.CredentialSet#add(java.lang.String, byte[])}.
   */
  @Test
  public void testAdd() {
    CredentialSet creds = new CredentialSet();
    assertNotNull( creds );
    creds.add( "test", "data".getBytes() );
  }




  /**
   * Test method for {@link coyote.commons.security.CredentialSet#CredentialSet(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testCredentialSetStringString() {
    CredentialSet creds = new CredentialSet( "username", "password" );
    assertNotNull( creds );
  }




  /**
   * Test method for {@link coyote.commons.security.CredentialSet#contains(java.lang.String)}.
   */
  @Test
  public void testContains() {
    CredentialSet creds = new CredentialSet();
    assertNotNull( creds );
    creds.add( "test", "data".getBytes() );
    assertTrue( creds.contains( "test" ) );
    creds = new CredentialSet( "username", "password" );
    assertTrue( creds.contains( creds.ACCOUNT ) );
    assertTrue( creds.contains( creds.PASSWORD ) );
  }

}
