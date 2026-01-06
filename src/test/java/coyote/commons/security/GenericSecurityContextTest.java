/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.security;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class GenericSecurityContextTest {

    private static SecurityContext context = null;
    private static final String ADMIN_ROLE = "admin";


    /**
     * @throws java.lang.Exception
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {

        // Create a generic security context
        context = new GenericSecurityContext("Demo");

        // Add some roles to the context
        Role role = new Role(ADMIN_ROLE);

        // specify the permission for this role
        role.addPermission(new Permission("ticket", Permission.CREATE));

        // add the role to the context
        context.add(role);

        // Add some logins to the context
        Login login = new Login(new GenericSecurityPrincipal("ID:12345", "user1"), new CredentialSet(CredentialSet.PASSWORD, "SeCr3t"));

        // add a role to the login
        login.addRole(role);

        // Add the login to the context
        context.add(login);
    }


    /**
     * @throws java.lang.Exception
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        //context.terminate();
    }


    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    public void setUp() throws Exception {
    }


    /**
     * @throws java.lang.Exception
     */
    @AfterEach
    public void tearDown() throws Exception {
    }


    /**
     *
     */
    @Test
    public void testBasic() {

        // Make sure we have a role
        Role newRole = context.getRole(ADMIN_ROLE);
        assertNotNull(newRole, "admin role cold not be retrieved");

        // Now some login tests...

        Login login = null;

        // Now try to get a login with some invalid credentials
        login = context.getLogin("user5", new CredentialSet(CredentialSet.PASSWORD, "MyPa55w04d"));
        assertNull(login, "Should not be able to get a login for user5");

        // try a valid username but slightly different password
        login = context.getLogin("user1", new CredentialSet(CredentialSet.PASSWORD, "SeCr3T"));
        assertNull(login, "Should not be able to get a login for user1");

        // This should work
        login = context.getLogin("user1", new CredentialSet(CredentialSet.PASSWORD, "SeCr3t"));
        assertNotNull(login, "user1 could not be validated");

        // Make sure we can get a security principal associated to this login
        SecurityPrincipal principal = login.getPrincipal();
        assertNotNull(principal);
        assertEquals("ID:12345", principal.getId());
        assertEquals("user1", principal.getName());

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // - -
        // Now see if the login is allowed to create a ticket
        assertTrue(context.allows(login, Permission.CREATE, "ticket"), "user1 should be allowed to create a ticket");

        // Now make sure the login is not allowed to delete an order
        assertFalse(context.allows(login, Permission.DELETE, "order"), "user1 should not be allowed to delete an order");

        // Since we have a valid login, let's create a session
        Session session = context.createSession(login);
        assertNotNull(session, "Could not create a session for user1");

        String sessionId = session.getId();

        // Now let's try to retrieve the login, by the session identifier
        Session userSession = context.getSession(sessionId);
        assertNotNull(userSession, "Could not retrieve a session for by its identifier");

        // Try to retrieve the login from an identified session
        Login userLogin = context.getLoginBySession(sessionId);
        assertNotNull(userLogin, "Could not retrieve a login by its session identifier");

        // Try to retrieve the session for the login
        Session loginSession = context.getSession(login);
        assertNotNull(loginSession, "Could not retrieve a session by its login");
    }

}
