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

//import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * 
 */
public class ContextTest {

  /**
   * 
   */
  @Test
  public void testGenericContext() {
    // Create a generic security context
    Context context = new GenericContext( "Demo" );

    // Add some roles to the context
    Role role = new Role( "admin" );

    // specify the permission for this role
    role.addPermission( new Permission( "ticket", Permission.CREATE ) );

    // add the role to the context
    context.add( role );

    // Add some logins to the context
    Login login = new Login( new CredentialSet( "user1", "SeCr3t" ) );

    // add a role to the login
    login.addRole( "admin" );

    // Add the login to the context
    context.add( login );

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Now try to get a login with some invalid credentials
    login = context.getLogin( new CredentialSet( "user5", "MyPa55w04d" ) );
    assertNull( "Should not be able to get a login for user5", login );

    // try a valid username but slightly different password
    login = context.getLogin( new CredentialSet( "user1", "SeCr3T" ) );
    assertNull( "Should not be able to get a login for user1", login );

    
    // This should work
    login = context.getLogin( new CredentialSet( "user1", "SeCr3t" ) );
    assertNotNull( "user1 could not be validated", login );

    // Now see if the login is allowed to create a ticket
    //assertTrue( "user1 whould be allowed to create a ticket", login.hasPermission( "ticket", Permission.CREATE ) );

  }

}
