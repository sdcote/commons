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
import static org.junit.Assert.assertFalse;
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
		Context context = new GenericContext("Demo");

		// Add some roles to the context
		Role role = new Role("admin");

		// specify the permission for this role
		role.addPermission(new Permission("ticket", Permission.CREATE));

		// add the role to the context
		context.add(role);

		// Make sure it was added
		Role newRole = context.getRole(role.getName());
		assertNotNull("admin role cold not be retrieved", newRole);

		// Add some logins to the context
		Login login = new Login(new CredentialSet("user1", "SeCr3t"));

		// add a role to the login
		login.addRole("admin");

		// Add the login to the context
		context.add(login);

		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		// - -
		// Now try to get a login with some invalid credentials
		login = context.getLogin(new CredentialSet("user5", "MyPa55w04d"));
		assertNull("Should not be able to get a login for user5", login);

		// try a valid username but slightly different password
		login = context.getLogin(new CredentialSet("user1", "SeCr3T"));
		assertNull("Should not be able to get a login for user1", login);

		// This should work
		login = context.getLogin(new CredentialSet("user1", "SeCr3t"));
		assertNotNull("user1 could not be validated", login);

		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		// - -
		// Now see if the login is allowed to create a ticket
		assertTrue("user1 should be allowed to create a ticket", context.loginHasPermission(login, "ticket", Permission.CREATE));

		// Now make sure the login is not allowed to delete an order
		assertFalse("user1 should not be allowed to delete an order", context.loginHasPermission(login, "order", Permission.DELETE));

		// Since we have a valid login, let's create a session
		Session session = context.createSession(login);
		assertNotNull("Could not create a session for user1", session);

		String sessionId = session.getId();

		// Now let's try to retrieve the login, by the session identifier
		Session userSession = context.getSession(sessionId);
		assertNotNull("Could not retrieve a session for by its identifier", userSession);

		// Try to retrieve the login from an identified session
		Login userLogin = context.getLogin(sessionId);
		assertNotNull("Could not retrieve a login for by its session identifier", userLogin);

		// Try to retrieve the session for the login
		Session loginSession = context.getSession(login);
		assertNotNull("Could not retrieve a session for by its login", loginSession);

	}

}
