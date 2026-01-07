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
package coyote.commons.network.http.auth;


import coyote.commons.ByteUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.network.http.HTTP;
import coyote.commons.network.http.MockSession;
import coyote.commons.network.http.TestHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class GenericAuthProviderTest {
    private static final String AUTH_CONFIG = "{ \"Users\" : [ { \"Name\" : \"admin\", \"Password\" : \"secret\", \"Groups\" : \"sysop,devop\" },{ \"Name\" : \"sysop\", \"Password\" : \"secret\", \"Groups\" : \"sysop\" }, { \"Name\" : \"devop\", \"Password\" : \"secret\", \"Groups\" : \"devop\" }, { \"Name\" : \"user\", \"Password\" : \"secret\" } ] }";

    private static final String MD5 = "MD5";
    private static final String UTF8 = "UTF8";

    static {
        try {
            @SuppressWarnings("unused")
            MessageDigest md = MessageDigest.getInstance(MD5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        UTF8.getBytes(StandardCharsets.UTF_8);
    }


    /**
     * @throws Exception
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {

    }


    /**
     * @throws Exception
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {

    }


    /**
     *
     */
    @Test
    public void testDefaultAuthProvider() {
        AuthProvider provider = new GenericAuthProvider();
        assertNotNull(provider);
    }


    /**
     *
     */
    @Test
    public void testDefaultAuthProviderConfig() {
        try {
            Config cfg = new Config(AUTH_CONFIG);

            AuthProvider provider = new GenericAuthProvider(cfg);
            assertNotNull(provider);

        } catch (ConfigurationException e) {
            fail(e.getMessage());
        }
    }


    /**
     * Test method for {@link GenericAuthProvider#isSecureConnection(coyote.commons.network.http.HTTPSession)}.
     */
    //@Ignore
    public void testIsSecureConnection() {
        fail("Not yet implemented"); // TODO
    }


    @Test
    public void digestTest() {
        GenericAuthProvider provider = new GenericAuthProvider();
        String password = "secret";

        provider.setDigestRounds(1);
        byte[] barray = provider.digest(password.getBytes(StandardCharsets.UTF_8));
        String result = ByteUtil.bytesToHex(barray);
        assertEquals("5E BE 22 94 EC D0 E0 F0 8E AB 76 90 D2 A6 EE 69", result);

        provider.setDigestRounds(2);
        barray = provider.digest(password.getBytes(StandardCharsets.UTF_8));
        result = ByteUtil.bytesToHex(barray);
        assertEquals("9E 76 90 17 C8 5F 06 49 77 FE 6A 65 8F 20 7F A6", result);

        provider.setDigestRounds(3);
        barray = provider.digest(password.getBytes(StandardCharsets.UTF_8));
        result = ByteUtil.bytesToHex(barray);
        assertEquals("09 C5 10 DF 26 46 5A EE 2F 81 E7 16 DF 44 A3 B7", result);

        provider.setDigestRounds(4);
        barray = provider.digest(password.getBytes(StandardCharsets.UTF_8));
        result = ByteUtil.bytesToHex(barray);
        assertEquals("CA 7C B2 24 AC 50 ED 0F 42 90 0D 3F BB 4E 85 90", result);

        provider.setDigestRounds(5);
        barray = provider.digest(password.getBytes(StandardCharsets.UTF_8));
        result = ByteUtil.bytesToHex(barray);
        assertEquals("93 FC B3 61 1F 8E 12 DF 71 9A 47 8A C2 EA 36 14", result);

    }


    /**
     * Test method for {@link GenericAuthProvider#isAuthenticated(coyote.commons.network.http.HTTPSession)}.
     */
    @Test
    public void testIsAuthenticated() {
        try {
            Config cfg = new Config(AUTH_CONFIG);

            GenericAuthProvider provider = new GenericAuthProvider(cfg);
            assertNotNull(provider);
            //System.out.println( "Provider is using "+provider.getDigestRounds()+" digest Rounds" );

            GenericAuthProvider.User user = provider.getUser("user");
            assertNotNull(user);
            //String name = user.getName();
            //byte[] barray = user.getPassword();
            //String result = ByteUtil.bytesToHex( barray );
            //System.out.println( "User: "+name+" password: "+result );

            // create a mock session
            MockSession session = new MockSession();

            // Generate an Authorization header for a user in our test configuration
            String username = "user";
            String password = "secret";
            String basicAuth = TestHttpClient.calculateHeaderData(username, password);
            session.addRequestHeader(HTTP.HDR_AUTHORIZATION.toLowerCase(), basicAuth);

            // Have the provider validate this session
            assertTrue(provider.isAuthenticated(session));

            // the user and groups should be populate in the session
            assertNotNull(session.getUserName());
            assertEquals(session.getUserName(), username);
            assertNotNull(session.getUserGroups());
            assertEquals(0, session.getUserGroups().size());

        } catch (ConfigurationException e) {
            fail(e.getMessage());
        }
    }


    /**
     * Test method for {@link GenericAuthProvider#isAuthorized(coyote.commons.network.http.HTTPSession, String)}.
     */
    @Test
    public void testIsAuthorized() {
        try {
            Config cfg = new Config(AUTH_CONFIG);

            GenericAuthProvider provider = new GenericAuthProvider(cfg);
            assertNotNull(provider);

            GenericAuthProvider.User user = provider.getUser("user");
            assertNotNull(user);
            //String name = user.getName();
            //byte[] barray = user.getPassword();

            // create a mock session
            MockSession session = new MockSession();

            // Generate an Authorization header for a user in our test configuration
            String username = "user";
            String password = "secret";
            String basicAuth = TestHttpClient.calculateHeaderData(username, password);
            session.addRequestHeader(HTTP.HDR_AUTHORIZATION.toLowerCase(), basicAuth);
            // Have the provider validate and set the username in the session
            assertTrue(provider.isAuthenticated(session));
            // Have the provider check role based access of this session
            assertFalse(provider.isAuthorized(session, "devop"));

            // Generate an Authorization header for the 'admin' user
            username = "admin";
            password = "secret";
            basicAuth = TestHttpClient.calculateHeaderData(username, password);
            session = new MockSession();
            session.addRequestHeader(HTTP.HDR_AUTHORIZATION.toLowerCase(), basicAuth);
            assertTrue(provider.isAuthenticated(session));
            assertTrue(provider.isAuthorized(session, "devop"));


            username = "admin";
            password = "";
            basicAuth = TestHttpClient.calculateHeaderData(username, password);
            session = new MockSession();
            session.addRequestHeader(HTTP.HDR_AUTHORIZATION.toLowerCase(), basicAuth);
            assertFalse(provider.isAuthenticated(session));


            username = "";
            password = "secret";
            basicAuth = TestHttpClient.calculateHeaderData(username, password);
            session = new MockSession();
            session.addRequestHeader(HTTP.HDR_AUTHORIZATION.toLowerCase(), basicAuth);
            assertFalse(provider.isAuthenticated(session));

            username = " ";
            password = " ";
            basicAuth = TestHttpClient.calculateHeaderData(username, password);
            session = new MockSession();
            session.addRequestHeader(HTTP.HDR_AUTHORIZATION.toLowerCase(), basicAuth);
            assertFalse(provider.isAuthenticated(session));

            username = "";
            password = "";
            basicAuth = TestHttpClient.calculateHeaderData(username, password);
            session = new MockSession();
            session.addRequestHeader(HTTP.HDR_AUTHORIZATION.toLowerCase(), basicAuth);
            assertFalse(provider.isAuthenticated(session));

        } catch (ConfigurationException e) {
            fail(e.getMessage());
        }

    }

}
